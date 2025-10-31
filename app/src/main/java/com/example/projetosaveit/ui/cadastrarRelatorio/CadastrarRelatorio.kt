package com.example.projetosaveit.ui.cadastrarRelatorio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projetosaveit.api.repository.EstoqueRepository
import com.example.projetosaveit.api.repository.LoteRepository
import com.example.projetosaveit.databinding.FragmentCadastrarRelatorioBinding
import com.example.projetosaveit.model.EstoqueDTO
import com.example.projetosaveit.model.LoteDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class CadastrarRelatorio : Fragment() {

    private var binding: FragmentCadastrarRelatorioBinding? = null
    private val loteRepository = LoteRepository()
    private val estoqueRepository = EstoqueRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCadastrarRelatorioBinding.inflate(inflater, container, false)

        binding?.btnCadastrarEstoque?.setOnClickListener {

            val sku = binding?.sku?.text.toString()
            val qntdProd = binding?.qntdProd?.text.toString()
            val qntdSaida = binding?.qntdSaida?.text.toString()
            val motivoDescarte = binding?.motivoDescarte?.text.toString()
            var qntdDescarte = binding?.qntdDescarte?.text.toString()

            if (qntdDescarte.isNullOrEmpty()) {
                qntdDescarte = "0"
            }

            if (sku.isBlank() || qntdProd.isBlank() || qntdSaida.isBlank() || qntdDescarte.isBlank()) {
                Toast.makeText(requireContext(), "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val createdAt = LocalDateTime.now()

            loteRepository.getLoteSku(sku).enqueue(object : Callback<LoteDTO> {
                override fun onResponse(call: Call<LoteDTO>, response: Response<LoteDTO>) {
                    if (response.isSuccessful) {
                        val lote = response.body()
                        if (lote != null) {
                            val estoqueDto = EstoqueDTO(
                                quantityInput = qntdProd.toInt(),
                                quantityOutput = qntdSaida.toInt(),
                                createdAt = createdAt.toString(),
                                productId = lote.productId,
                                batchId = lote.id,
                                discardReason = motivoDescarte,
                                discardQuantity = qntdDescarte.toInt(),
                                id = 0
                            )

                            estoqueRepository.postEstoque(estoqueDto)
                                .enqueue(object : Callback<ResponseBody> {
                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(requireContext(), "Estoque cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(requireContext(), "Erro ao cadastrar estoque", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                        Toast.makeText(requireContext(), "Erro ao cadastrar estoque: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        } else {
                            Toast.makeText(requireContext(), "Lote não encontrado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Erro ao buscar lote", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoteDTO>, t: Throwable) {
                    Toast.makeText(requireContext(), "Erro ao buscar lote: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}