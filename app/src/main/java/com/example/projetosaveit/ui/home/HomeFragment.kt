package com.example.projetosaveit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.projetosaveit.adapter.AdapterProduto
import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.api.repository.ProdutoRepository
import com.example.projetosaveit.databinding.FragmentHomeBinding
import com.example.projetosaveit.model.ProdutoDTO
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var adapter: AdapterProduto
    private val repository = ProdutoRepository()
    private var binding: FragmentHomeBinding? = null

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
        carregarProdutos()

        return root
    }

    private fun carregarProdutos() {
        repository.getProdutos().enqueue(object : retrofit2.Callback<List<ProdutoDTO>> {
            override fun onResponse(call: Call<List<ProdutoDTO>?>, response: Response<List<ProdutoDTO>?>) {
                if (response.isSuccessful) {
                    response.body()?.let { produtos ->
                        val listaModel: List<Produto> = produtos.map { dto ->
                            Produto(
                                nomeProduto = dto.name,
                            )
                        }

                        // Agora passa a lista convertida pro adapter
                        adapter.listProdutos = listaModel
                        adapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onFailure(call: Call<List<ProdutoDTO>?>, t: Throwable) {
                binding?.textHome?.text = "Erro: ${t.message}"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}