package com.example.projetosaveit.ui.chats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetosaveit.adapter.AdapterChat
import com.example.projetosaveit.api.repository.ChatRepository
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.databinding.FragmentChatsBinding
import com.example.projetosaveit.model.ChatDTO
import com.example.projetosaveit.model.EmpresaDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatsFragment : Fragment() {

    private val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    private var binding: FragmentChatsBinding? = null
    private val listaOriginal = mutableListOf<ChatDTO>()

    private lateinit var adapter: AdapterChat
    private val chatRepository = ChatRepository()
    private val empresaRepository = EmpresaRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        adapter = AdapterChat()
        binding!!.rvChat?.adapter = adapter
        binding!!.rvChat?.layoutManager = LinearLayoutManager(requireContext())

        val empresaLogada: FirebaseUser = objAutenticar.currentUser!!
        val email = empresaLogada.email

        if (email != null) {
            carregarIdEmpresaEChats(email)
        } else {
            Toast.makeText(context, "Erro: e-mail da empresa não encontrado", Toast.LENGTH_LONG).show()
        }

        val input = binding?.inputPesquisar

        input?.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val empresa = s.toString().trim().lowercase()
                buscarChats(empresa)
            }

            override fun afterTextChanged(s: android.text.Editable?) {
            }
        })

        return binding!!.root
    }

    private fun carregarIdEmpresaEChats(email: String) {

        empresaRepository.getEmpresa(email).enqueue(object : Callback<EmpresaDTO> {
            override fun onResponse(call: Call<EmpresaDTO>, response: Response<EmpresaDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    val empresa = response.body()!!
                    val idEmpresa = empresa.id
                    Log.d("ChatsFragment", "Empresa logada: ${empresa.name} (ID: $idEmpresa)")
                    carregarChats(idEmpresa)
                } else {
                    Toast.makeText(context, "Empresa não encontrada para o e-mail informado", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<EmpresaDTO>, t: Throwable) {
                Toast.makeText(context, "Erro ao buscar empresa: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun carregarChats(idEmpresaLogada: Long) {
        chatRepository.getChatPorEnterprise(idEmpresaLogada).enqueue(object : Callback<List<ChatDTO>> {
            override fun onResponse(call: Call<List<ChatDTO>>, response: Response<List<ChatDTO>>) {
                if (response.isSuccessful && response.body() != null) {
                    val chats = response.body()!!
                    if (chats.isEmpty()) {
                        atualizarAdapter(chats)
                        return
                    }

                    var loadedChats = 0
                    val totalChats = chats.size

                    chats.forEach { chat ->

                        // Buscar a ID da outra empresa que participa da conversa
                        chatRepository.getChatOutraEmpresa(chat.chatId, idEmpresaLogada).enqueue(object : Callback<ChatDTO> {
                            override fun onResponse(call: Call<ChatDTO>, response2: Response<ChatDTO>) {
                                if (response2.isSuccessful && response2.body() != null) {

                                    val outraEmpresaId = response2.body()!!.enterpriseId
                                    chat.enterpriseId = outraEmpresaId

                                    // Buscar a última mensagem
                                    chatRepository.getChatUltimaMensagem(chat.chatId, idEmpresaLogada).enqueue(object : Callback<ChatDTO> {
                                        override fun onResponse(call: Call<ChatDTO>, response4: Response<ChatDTO>) {
                                            if (response4.isSuccessful && response4.body() != null) {
                                                chat.text = response4.body()!!.text

                                                // Buscar dados da outra empresa
                                                empresaRepository.getEmpresaId(outraEmpresaId).enqueue(object : Callback<EmpresaDTO> {
                                                    override fun onResponse(call: Call<EmpresaDTO>, response3: Response<EmpresaDTO>) {
                                                        if (response3.isSuccessful && response3.body() != null) {
                                                            chat.empresa = response3.body()
                                                        }

                                                        // Chat carregado
                                                        loadedChats++
                                                        if (loadedChats == totalChats) {
                                                            atualizarAdapter(chats)
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<EmpresaDTO>, t: Throwable) {
                                                        // Erro ao buscar empresa, passa para a próxima etapa/próximo chat
                                                        loadedChats++
                                                        if (loadedChats == totalChats) {
                                                            atualizarAdapter(chats)
                                                        }
                                                    }
                                                })
                                            }
                                        }

                                        override fun onFailure(call: Call<ChatDTO>, t: Throwable) {
                                            // Erro na última mensagem, ainda podemos mostrar o resto
                                            loadedChats++
                                            if (loadedChats == totalChats) {
                                                atualizarAdapter(chats)
                                            }
                                        }
                                    })

                                } else {
                                    // Falha ao obter outra empresa, passa para o próximo chat
                                    loadedChats++
                                    if (loadedChats == totalChats) {
                                        atualizarAdapter(chats)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ChatDTO>, t: Throwable) {
                                // Erro na requisição da outra empresa, passa para o próximo chat
                                loadedChats++
                                if (loadedChats == totalChats) {
                                    atualizarAdapter(chats)
                                }
                            }
                        })
                    }

                } else {
                    Toast.makeText(requireContext(), "Falha ao carregar chats", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ChatDTO>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erro na requisição: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun atualizarAdapter(chats: List<ChatDTO>) {
        listaOriginal.clear()
        listaOriginal.addAll(chats)
        adapter.listChats = chats.toMutableList()
        adapter.notifyDataSetChanged()
    }

    private fun buscarChats(filtro: String) {
        if (filtro.isEmpty()) {
            adapter.listChats = listaOriginal.toMutableList()
        } else {
            val filtradas = listaOriginal.filter { chat ->
                chat.empresa?.name?.lowercase()?.contains(filtro) == true
            }
            adapter.listChats = filtradas.toMutableList()
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}