package com.example.projetosaveit.ui

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.AdapterConversa
import com.example.projetosaveit.adapter.recycleView.Mensagem
import com.example.projetosaveit.adapter.recycleView.MensagemWS
import com.example.projetosaveit.adapter.recycleView.TipoMensagem
import com.example.projetosaveit.api.network.WebSocketClient
import com.example.projetosaveit.api.repository.ChatRepository
import com.example.projetosaveit.model.ChatDTO
import com.example.projetosaveit.model.ChatInsertDTO
import com.example.projetosaveit.util.GetEmpresa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Conversa : AppCompatActivity() {

    private lateinit var rvMensagens: RecyclerView
    private lateinit var adapter: AdapterConversa
    private val listaMensagens = mutableListOf<Mensagem>()
    private val chatRepository = ChatRepository()
    private val gson = Gson()

    private val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    private var id: Long = 0L
    private var idB: Long = 0L
    private var chatId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_conversa)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mensagem = findViewById<EditText>(R.id.escreverMsg)
        val enviar = findViewById<ImageView>(R.id.enviarMsg)
        rvMensagens = findViewById(R.id.rvMensagens)

        adapter = AdapterConversa(listaMensagens)
        rvMensagens.adapter = adapter
        rvMensagens.layoutManager = LinearLayoutManager(this)

        idB = intent.getLongExtra("empresaId", 0L)
        chatId = intent.getLongExtra("chatId", 0L)

        val empresaLogada: FirebaseUser = objAutenticar.currentUser!!
        val email = empresaLogada.email.toString()

        findViewById<ImageView>(R.id.btVoltarConversa).setOnClickListener {
            WebSocketClient.disconnect()
            finish()
        }

        enviar.isEnabled = false
        GetEmpresa.pegarEmailEmpresa(email) { empresa ->
            id = empresa?.id?.toLong() ?: 0
            enviar.isEnabled = true

            carregarHistorico()
            conectarWebSocket()
        }

        enviar.setOnClickListener {
            val texto = mensagem.text.toString().trim()
            if (texto.isNotEmpty()) {
                enviarMensagem(texto)
                mensagem.text.clear()
            }
        }
    }

    private fun carregarHistorico() {
        chatRepository.getChatHistorico(chatId).enqueue(object : Callback<List<ChatDTO>> {
            override fun onResponse(call: Call<List<ChatDTO>>, response: Response<List<ChatDTO>>) {
                if (response.isSuccessful) {
                    val historico = response.body() ?: emptyList()
                    historico.forEach { chat ->
                        val tipo = if (chat.enterpriseId == id) TipoMensagem.ENVIADA else TipoMensagem.RECEBIDA
                        val msg = Mensagem(chat.text, tipo, chat.enterpriseId)
                        adapter.adicionarMensagem(msg)
                    }
                    rvMensagens.scrollToPosition(adapter.itemCount - 1)
                    marcarMensagensComoLidas()
                } else {
                    Log.e("Historico", "Erro: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ChatDTO>>, t: Throwable) {
                Log.e("Historico", "Falha: ${t.message}", t)
            }
        })
    }

    private fun conectarWebSocket() {
        if (!WebSocketClient.isConnected()) {
            WebSocketClient.connect { msgJson ->
                runOnUiThread {
                    val msgObj = gson.fromJson(msgJson, MensagemWS::class.java)
                    if (msgObj.enterpriseId != id) {
                        val tipo = TipoMensagem.RECEBIDA
                        val novaMsg = Mensagem(msgObj.text, tipo, msgObj.enterpriseId)
                        adapter.adicionarMensagem(novaMsg)
                        rvMensagens.smoothScrollToPosition(adapter.itemCount - 1)
                        marcarMensagensComoLidas()
                    }
                }
            }
        }
    }

    private fun enviarMensagem(texto: String) {
        val msg = Mensagem(texto, TipoMensagem.ENVIADA, id)
        adapter.adicionarMensagem(msg)
        rvMensagens.smoothScrollToPosition(adapter.itemCount - 1)

        val chatRequest = ChatInsertDTO(
            text = texto,
            sentAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            enterpriseId = id,
            otherEnterpriseId = idB,
            read = false
        )

        WebSocketClient.sendMessage(gson.toJson(chatRequest))
    }

    private fun marcarMensagensComoLidas() {
        chatRepository.marcarComoLida(chatId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Chat", "Mensagens marcadas como lidas")
                } else {
                    Log.e("Chat", "Erro ao marcar como lida: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Chat", "Falha ao marcar como lida: ${t.message}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        conectarWebSocket()
    }

    override fun onPause() {
        super.onPause()
        WebSocketClient.disconnect()
    }
}