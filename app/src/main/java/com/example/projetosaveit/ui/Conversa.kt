package com.example.projetosaveit.ui

import android.content.Intent
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
import com.example.projetosaveit.databinding.FragmentChatsBinding
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

    private val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    private var id: Long = 0L
    private var idB: Long = 0L
    private var fotoEmpresaA: String = ""
    private var fotoEmpresaB: String = ""
    private var chatId = 0L
    private var gson = Gson()

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

        fotoEmpresaB = intent.getStringExtra("empresaFoto") ?: ""
        idB = intent.getLongExtra("empresaId", 0L)
        chatId = intent.getLongExtra("chatId", 0L)

        val empresaLogada: FirebaseUser = objAutenticar.currentUser!!
        val email = empresaLogada.email.toString()

        val bt: ImageView = findViewById(R.id.btVoltar)
        bt.setOnClickListener {
            WebSocketClient.disconnect()
            val intent = Intent(this, FragmentChatsBinding::class.java)
            startActivity(intent)
            finish()
        }

        enviar.isEnabled = false
        GetEmpresa.pegarEmailEmpresa(email) { empresa ->
            id = empresa?.id?.toLong() ?: 0
            fotoEmpresaA = empresa?.enterpriseImage.toString()
            enviar.isEnabled = true

            WebSocketClient.connect { msgJson ->
                runOnUiThread {
                    val msgObj = Gson().fromJson(msgJson, MensagemWS::class.java)

                    // Filtra só mensagens de outras empresas
                    if (msgObj.enterpriseId != id) {
                        val tipo = TipoMensagem.RECEBIDA
                        val novaMsg = Mensagem(msgObj.text, tipo, fotoEmpresaB, msgObj.enterpriseId)
                        adapter.adicionarMensagem(novaMsg)
                        rvMensagens.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }
            }

            chatRepository.getChatHistorico(chatId).enqueue(object : Callback<List<ChatDTO>> {
                override fun onResponse(
                    call: Call<List<ChatDTO>>,
                    response: Response<List<ChatDTO>>
                ) {
                    if (response.isSuccessful) {
                        val historico = response.body() ?: emptyList()
                        historico.forEach { chat ->
                            val tipo = if (chat.enterpriseId == id) TipoMensagem.ENVIADA else TipoMensagem.RECEBIDA
                            val foto = if (tipo == TipoMensagem.ENVIADA) fotoEmpresaA else fotoEmpresaB

                            val msg = Mensagem(chat.text, tipo, foto, chat.enterpriseId)
                            adapter.adicionarMensagem(msg)
                        }
                        rvMensagens.scrollToPosition(adapter.itemCount - 1)
                    } else {
                        Log.e("Historico", "Erro: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ChatDTO>>, t: Throwable) {
                    Log.e("Historico", "Falha: ${t.message}", t)
                }
            })
        }

//        Enviar mensagens
        enviar.setOnClickListener {
            val texto = mensagem.text.toString().trim()
            if (texto.isNotEmpty()) {
                val msg = Mensagem(texto, TipoMensagem.ENVIADA, fotoEmpresaA, id)
                adapter.adicionarMensagem(msg)
                rvMensagens.smoothScrollToPosition(adapter.itemCount - 1)

                val chatRequest = ChatInsertDTO(
                    text = texto,
                    sentAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    enterpriseId = id,
                    otherEnterpriseId = idB,
                    read = false
                )

                val json = gson.toJson(chatRequest)

                WebSocketClient.sendMessage(json)
                mensagem.text.clear()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!WebSocketClient.isConnected()) {
            WebSocketClient.connect { msgJson ->
                runOnUiThread {
                    val msgObj = Gson().fromJson(msgJson, MensagemWS::class.java)
                    if (msgObj.enterpriseId != id) {
                        val tipo = TipoMensagem.RECEBIDA
                        val novaMsg = Mensagem(msgObj.text, tipo, fotoEmpresaB, msgObj.enterpriseId)
                        adapter.adicionarMensagem(novaMsg)
                        rvMensagens.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        WebSocketClient.disconnect()
    }
}