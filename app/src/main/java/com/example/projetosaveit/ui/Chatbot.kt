package com.example.projetosaveit.ui.chatbot

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.AdapterChatbot
import com.example.projetosaveit.adapter.AdapterSessoes
import com.example.projetosaveit.adapter.recycleView.MensagemChatbot
import com.example.projetosaveit.adapter.recycleView.TipoMensagemChatbot
import com.example.projetosaveit.api.repository.ChatbotRepository
import com.example.projetosaveit.model.*
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Chatbot : AppCompatActivity() {

    private lateinit var rvMensagens: RecyclerView
    private lateinit var rvConversasDrawer: RecyclerView
    private lateinit var edtMensagem: EditText
    private lateinit var btnEnviar: ImageView
    private lateinit var btnMenu: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var adapterChat: AdapterChatbot
    private lateinit var adapterSessoes: AdapterSessoes
    private lateinit var chatbotRepository: ChatbotRepository

    private val listaMensagens = mutableListOf<MensagemChatbot>()
    private val listaSessoes = mutableListOf<com.example.projetosaveit.adapter.ConversaItem>()

    private var objAutenticar = FirebaseAuth.getInstance()
    private var sessionId: String? = null
    private var funcionarioId: Int = 10
    private var empresaId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        val email = objAutenticar.currentUser?.email.toString()

        findViewById<ImageView>(R.id.btVoltarChatbot).setOnClickListener {
            finish()
        }

        GetEmpresa.pegarEmailEmpresa(email) { empresa ->
            if (empresa != null) {
                empresaId = empresa.id.toInt()
                funcionarioId = 1
                Log.d("Chatbot", "Empresa ID: $empresaId")
                Log.d("Chatbot", "Funcionario ID: $funcionarioId")

                findViewById<TextView>(R.id.txtBoasVindas).text = "Olá, ${empresa.name}\nComo posso te ajudar hoje?"

                inicializarComponentes()
                configurarRecyclerViews()
                configurarListeners()
                carregarHistoricoSessoes()
                val headerView = navView.getHeaderView(0)
                headerView.findViewById<TextView>(R.id.btnNovaConversa)?.setOnClickListener {
                    novaConversa()
                }
            } else {
                GetFuncionario.pegarEmailFunc(email) { func ->
                    if (func != null) {
                        funcionarioId = func.id.toInt()
                        empresaId = func.enterpriseId.toInt()
                        Log.d("Chatbot", "Funcionario ID: $funcionarioId")
                        Log.d("Chatbot", "Empresa ID: $empresaId")

                        findViewById<TextView>(R.id.txtBoasVindas).text = "Olá, ${func.name}\nComo posso te ajudar hoje?"

                        inicializarComponentes()
                        configurarRecyclerViews()
                        configurarListeners()
                        carregarHistoricoSessoes()
                        val headerView = navView.getHeaderView(0)
                        headerView.findViewById<TextView>(R.id.btnNovaConversa)?.setOnClickListener {
                            novaConversa()
                        }
                    } else {
                        Log.e("Chatbot", "Nenhuma empresa ou funcionario encontrado para o email: $email")
                    }
                }
            }
        }
    }

    private fun inicializarComponentes() {
        chatbotRepository = ChatbotRepository()
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        btnEnviar = findViewById(R.id.enviarMsg2)
        edtMensagem = findViewById(R.id.escreverMsg)
        rvMensagens = findViewById(R.id.rvMensagens)
        btnMenu = findViewById(R.id.btMenu)
    }

    private fun configurarRecyclerViews() {
        adapterChat = AdapterChatbot(listaMensagens)
        rvMensagens.layoutManager = LinearLayoutManager(this)
        rvMensagens.adapter = adapterChat

        val headerView = navView.getHeaderView(0)
        rvConversasDrawer = headerView.findViewById(R.id.recyclerConversasDrawer)

        adapterSessoes = AdapterSessoes(listaSessoes) { sessao ->
            abrirConversa(sessao.sessionId)
        }
        rvConversasDrawer.layoutManager = LinearLayoutManager(this)
        rvConversasDrawer.adapter = adapterSessoes
    }

    private fun configurarListeners() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        btnEnviar.setOnClickListener {
            val msg = edtMensagem.text.toString().trim()
            if (msg.isNotEmpty()) {
                enviarMensagemComSessao(msg)
                edtMensagem.text.clear()
            }
        }
    }

    private fun enviarMensagemComSessao(msg: String) {
        if (sessionId == null) {
            val request = IniciarChatRequest(
                empresa_id = empresaId,
                funcionario_id = funcionarioId
            )
            iniciarChat(request, msg)
        } else {
            enviarMensagem(msg)
        }
    }

    private fun iniciarChat(request: IniciarChatRequest, primeiraMsg: String) {
        chatbotRepository.iniciarChat(request).enqueue(object : Callback<IniciarChatResponse> {
            override fun onResponse(
                call: Call<IniciarChatResponse>,
                response: Response<IniciarChatResponse>
            ) {
                if (response.isSuccessful) {
                    sessionId = response.body()?.session_id
                    Log.d("Chatbot", "Sessao iniciada: $sessionId")

                    val tituloSessao = "Sessao ${listaSessoes.size + 1}"
                    adapterSessoes.adicionarConversa(tituloSessao, sessionId ?: "")

                    enviarMensagem(primeiraMsg)
                } else {
                    Log.e("Chatbot", "Erro iniciar chat: ${response.code()} ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<IniciarChatResponse>, t: Throwable) {
                Log.e("Chatbot", "Falha ao iniciar chat", t)
            }
        })
    }

    private fun enviarMensagem(msg: String) {
        val sessaoAtual = sessionId ?: return

        adapterChat.adicionarMensagem(MensagemChatbot(msg, TipoMensagemChatbot.USUARIO))
        rvMensagens.smoothScrollToPosition(adapterChat.itemCount - 1)

        val chatRequest = ChatRequest(
            user_input = msg,
            empresa_id = empresaId,
            funcionario_id = funcionarioId,
            session_id = sessaoAtual
        )

        chatbotRepository.enviarMensagem(chatRequest).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val resposta = response.body()?.resposta_assistente ?: "(sem resposta)"
                    adapterChat.adicionarMensagem(MensagemChatbot(resposta, TipoMensagemChatbot.BOT))
                    rvMensagens.smoothScrollToPosition(adapterChat.itemCount - 1)
                } else {
                    Log.e("Chatbot", "Erro enviar mensagem: ${response.code()} ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                Log.e("Chatbot", "Falha ao enviar mensagem", t)
            }
        })
    }

    private fun carregarHistoricoSessoes() {
        Log.d("Chatbot", "Carregando historico de SESSOES...")

        chatbotRepository.obterHistoricoFuncionario(funcionarioId).enqueue(object : Callback<HistoricoResponse> {
            override fun onResponse(call: Call<HistoricoResponse>, response: Response<HistoricoResponse>) {
                if (response.isSuccessful) {
                    val sessoes = response.body()?.sessoes ?: emptyList()
                    listaSessoes.clear()

                    Log.d("Chatbot", "${sessoes.size} sessoes carregadas")

                    sessoes.forEachIndexed { index, sessao ->
                        val titulo = "Conversa ${index + 1} - ${sessao.data?.substring(0, 10) ?: "Data"}"
                        listaSessoes.add(com.example.projetosaveit.adapter.ConversaItem(titulo, sessao.session_id))
                    }

                    adapterSessoes.notifyDataSetChanged()

                    if (sessoes.isNotEmpty()) {
                        sessionId = sessoes.first().session_id
                        carregarHistoricoMensagens(sessoes.first().session_id)
                    }

                } else {
                    Log.e("Chatbot", "Erro carregar historico de SESSOES: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HistoricoResponse>, t: Throwable) {
                Log.e("Chatbot", "Falha ao carregar historico de SESSOES", t)
            }
        })
    }

    private fun carregarHistoricoMensagens(sessionId: String) {
        Log.d("Chatbot", "Carregando historico de MENSAGENS da sessao: $sessionId")

        val request = HistoricoSessaoRequest(
            funcionario_id = funcionarioId,
            session_id = sessionId
        )

        chatbotRepository.obterHistoricoSessao(request).enqueue(object : Callback<HistoricoResponse> {
            override fun onResponse(call: Call<HistoricoResponse>, response: Response<HistoricoResponse>) {
                if (response.isSuccessful) {
                    val historico = response.body()?.historico ?: emptyList()
                    listaMensagens.clear()

                    Log.d("Chatbot", "${historico.size} mensagens carregadas")

                    historico.forEach { mensagem ->
                        val tipo = when (mensagem.role) {
                            "user" -> TipoMensagemChatbot.USUARIO
                            "assistant" -> TipoMensagemChatbot.BOT
                            else -> TipoMensagemChatbot.BOT
                        }
                        listaMensagens.add(MensagemChatbot(mensagem.content, tipo))
                    }

                    adapterChat.notifyDataSetChanged()
                    if (listaMensagens.isNotEmpty()) {
                        rvMensagens.scrollToPosition(listaMensagens.size - 1)
                    }

                } else {
                    Log.e("Chatbot", "Erro carregar historico de MENSAGENS: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HistoricoResponse>, t: Throwable) {
                Log.e("Chatbot", "Falha ao carregar historico de MENSAGENS", t)
            }
        })
    }

    private fun abrirConversa(sessionSelecionada: String) {
        Log.d("Chatbot", "Abrindo conversa: $sessionSelecionada")
        sessionId = sessionSelecionada
        carregarHistoricoMensagens(sessionSelecionada)
        drawerLayout.closeDrawers()
    }

    fun novaConversa() {
        sessionId = null
        listaMensagens.clear()
        adapterChat.notifyDataSetChanged()
        drawerLayout.closeDrawers()
    }
}