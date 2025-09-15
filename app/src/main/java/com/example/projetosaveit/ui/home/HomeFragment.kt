package com.example.projetosaveit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.projetosaveit.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projetosaveit.adapter.AdapterProduto
import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.api.repository.LoteRepository
import com.example.projetosaveit.api.repository.ProdutoRepository
import com.example.projetosaveit.databinding.FragmentHomeBinding
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.ProdutoDTO
import com.example.projetosaveit.ui.AdicionarProduto
import com.example.projetosaveit.ui.TelaEstoque
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

        var emailEmp : String = objAutenticar.currentUser?.email.toString()
        pegarEmpresaPorEmail(emailEmp) { empresa ->
            if (empresa != null) {
                idEmpresa = empresa.id
            }
        }

        adapter = AdapterProduto()
        binding!!.recycleProdutos.adapter = adapter
        binding!!.recycleProdutos.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        carregarProdutos(idEmpresa)

        binding!!.botaoAdicionarProduto.setOnClickListener {
            val intent = Intent(requireContext(), AdicionarProduto::class.java)
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
                binding?.textHome?.text = "Erro: ${t.message}"
            }
        })
    }

    private fun pegarEmpresaPorEmail(email: String, onResult: (EmpresaDTO?) -> Unit) {
        repositoryEmp.getEmpresa(email).enqueue(object : retrofit2.Callback<EmpresaDTO> {
            override fun onResponse(
                call: Call<EmpresaDTO>,
                response: Response<EmpresaDTO>
            ) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<EmpresaDTO>, t: Throwable) {
                Toast.makeText(context, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}