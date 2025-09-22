package com.example.projetosaveit.ui.vitrine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.projetosaveit.adapter.AdapterVitrine
import com.example.projetosaveit.adapter.recycleView.Vitrine
import com.example.projetosaveit.api.repository.VitrineRepository
import com.example.projetosaveit.databinding.FragmentVitrineBinding
import com.example.projetosaveit.model.VitrineDTO
import retrofit2.Call
import retrofit2.Response

class VitrineFragment : Fragment() {
    private var binding: FragmentVitrineBinding? = null

    private lateinit var adapter: AdapterVitrine
    private val repository = VitrineRepository()
    private var vitrine: List<Vitrine> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val dashboardViewModel =
            ViewModelProvider(this).get(VitrineViewModel::class.java)

        binding = FragmentVitrineBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val textView = binding!!.textDashboard
        dashboardViewModel.text.observe(
            viewLifecycleOwner
        ) { text: String? ->
            textView.text =
                text
        }

        adapter = AdapterVitrine()

        binding!!.rvVitrine.adapter = adapter
        binding!!.rvVitrine.setLayoutManager(StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL))

        carregarVitrine()

        return root
    }

    fun carregarVitrine() {
        repository.getVitrine().enqueue(object: retrofit2.Callback<List<VitrineDTO>> {
            override fun onResponse(call: Call<List<VitrineDTO>>, response: Response<List<VitrineDTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        vitrineDTO ->
                        val vitrineModel: List<Vitrine> = vitrineDTO.map { dto ->
                            Vitrine (
                                productId = dto.productId,
                                image = dto.image
                            )
                        }

                        adapter.listVitrine = vitrineModel
                        adapter.notifyDataSetChanged()
                        binding?.rvVitrine?.adapter = adapter
                    }

                }
            }

            override fun onFailure(call: Call<List<VitrineDTO>>, t: Throwable) {
                Toast.makeText(context, "Erro ao carregar Vitrine | " + t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}