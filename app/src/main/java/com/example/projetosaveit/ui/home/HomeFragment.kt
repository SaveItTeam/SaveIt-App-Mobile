package com.example.projetosaveit.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projetosaveit.adapter.AdapterProduto
import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.api.repository.LoteRepository
import com.example.projetosaveit.databinding.FragmentHomeBinding
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.ui.AdicionarProduto
import com.example.projetosaveit.ui.VisaoGeralEstoque
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var adapter: AdapterProduto
    private val repository = LoteRepository()
    private var binding: FragmentHomeBinding? = null
    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    val repositoryEmp : EmpresaRepository = EmpresaRepository()
    var idEmpresa : Long = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        adapter = AdapterProduto()
        binding!!.recycleProdutos.adapter = adapter
        binding!!.recycleProdutos.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        var emailEmp : String = objAutenticar.currentUser?.email.toString()
        pegarEmailEmpresa(emailEmp) { empresa ->
            if ((empresa != null)&&(empresa.id.toInt() != 0)) {
                idEmpresa = empresa.id
                carregarProdutos(idEmpresa)
            }else {
                GetFuncionario.pegarEmailFunc(emailEmp) { funcionario ->
                    if ((funcionario != null )&&(funcionario!!.id.toInt() != 0)) {
                        idEmpresa = funcionario!!.enterpriseId
                    }else {
                        Toast.makeText(context, "não foi possivel pegar o id do usuario", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding!!.botaoAdicionarProduto.setOnClickListener {
            val intent = Intent(requireContext(), AdicionarProduto::class.java)
            startActivity(intent)
        }

        binding!!.button7.setOnClickListener { v->
            val intent : Intent = Intent(context, VisaoGeralEstoque::class.java)
            startActivity(intent)
        }


        return root
    }

    private fun carregarProdutos(idEmpresa: Long) {
        repository.getLoteProduto(idEmpresa).enqueue(object : retrofit2.Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>?>, response: Response<List<Produto>?>) {
                if (response.isSuccessful) {
                    response.body()?.let { produtos ->
                        adapter.listProdutos = produtos
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<List<Produto>?>, t: Throwable) {
                Toast.makeText(context, "Erro ao carregar produtos: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun pegarEmailEmpresa(email: String, onResult: (EmpresaDTO?) -> Unit) {
        Log.d("erro", "pegarEmailEmpresa: buscando empresa para email = $email")
        repositoryEmp.getEmpresa(email).enqueue(object : retrofit2.Callback<EmpresaDTO> {
            override fun onResponse(call: Call<EmpresaDTO>, response: Response<EmpresaDTO>) {
                Log.d("teste", "getEmpresa onResponse code=${response.code()}")
                if (response.isSuccessful) {
                    Log.d("teste", "getEmpresa body=${response.body()}")
                    onResult(response.body())
                } else {
                    val errorBodyString = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(context, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<EmpresaDTO>, t: Throwable) {
                Log.e("erro", "getEmpresa onFailure", t)
                onResult(null)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
