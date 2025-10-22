package com.example.projetosaveit.ui.vitrine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.AdapterVitrine
import com.example.projetosaveit.adapter.recycleView.Vitrine
import com.example.projetosaveit.api.repository.VitrineRepository
import com.example.projetosaveit.databinding.FragmentVitrineBinding
import com.example.projetosaveit.model.VitrineDTO
import com.example.projetosaveit.ui.AdicionarProdutoVitrine
import retrofit2.Call
import retrofit2.Response

class VitrineFragment : Fragment() {
    private var binding: FragmentVitrineBinding? = null

    private lateinit var adapter: AdapterVitrine
    private val repository = VitrineRepository()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val dashboardViewModel =
            ViewModelProvider(this).get(VitrineViewModel::class.java)

        binding = FragmentVitrineBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val botoes = listOf(binding!!.button9, binding!!.button15, binding!!.button14)

        botoes.forEach { botao ->
            botao.setOnClickListener {
                botoes.forEach { it.isSelected = false }
                botao.isSelected = true

                botoes.forEach { it.setBackgroundResource(R.drawable.bt_filtro_estilizacao)
                                it.setTextColor(resources.getColor(R.color.BrancoNaoPuro))}
                botao.setBackgroundResource(R.drawable.bt_filtro_estilizacao_selecionado)
                botao.setTextColor(resources.getColor(R.color.PretoNaoPuro))

                carregarVitrine(botao.text.toString())

            }
        }

        binding!!.botaoAdicionarVitrine?.setOnClickListener { v ->
            val intent : Intent = Intent(v.context, AdicionarProdutoVitrine::class.java)
            startActivity(intent)
        }





        adapter = AdapterVitrine()

        binding!!.rvVitrine.adapter = adapter
        binding!!.rvVitrine.setLayoutManager(StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL))

        return root
    }

    fun carregarVitrine(filtro : String) {
        repository.getVitrineProdutos(filtro).enqueue(object : retrofit2.Callback<List<Vitrine>> {
            override fun onResponse(
                call: Call<List<Vitrine>>,
                response: Response<List<Vitrine>>
            ) {
                if (response.isSuccessful) {
                    val produtos = response.body()
                    adapter.listVitrine = produtos ?: emptyList()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Falha ao carregar a vitrine", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Vitrine>>, t: Throwable) {
                Toast.makeText(context, "Erro na requisição: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}