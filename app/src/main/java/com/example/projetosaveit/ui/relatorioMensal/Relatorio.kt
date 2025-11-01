package com.example.projetosaveit.ui.relatorioMensal

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EstoqueRepository
import com.example.projetosaveit.databinding.FragmentRelatorioBinding
import com.example.projetosaveit.model.RelatorioDTO
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth

class Relatorio : Fragment() {

    private val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    private var binding: FragmentRelatorioBinding? = null
    private val estoqueRepository = EstoqueRepository()
    private var idEmpresa = 1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelatorioBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = objAutenticar.currentUser?.email
        if (email == null) {
            Log.e("Relatório", "Usuário não autenticado")
            return
        }

        // Primeiro tenta pegar como empresa
        GetEmpresa.pegarEmailEmpresa(email) { empresa ->
            if (empresa != null) {
                // Usuário é uma empresa
                idEmpresa = empresa.id
                buscarRelatorio()
            } else {
                // Tenta pegar como funcionário
                GetFuncionario.pegarEmailFunc(email) { func ->
                    if (func != null) {
                        val enterpriseId = func.enterpriseId
                        GetEmpresa.pegarIdEmpresa(enterpriseId) { emp ->
                            if (emp != null) {
                                idEmpresa = emp.id
                                buscarRelatorio()
                            } else {
                                Log.e("Relatório", "Empresa não encontrada para o ID: $enterpriseId")
                            }
                        }
                    } else {
                        Log.e("Relatório", "Usuário não encontrado como empresa ou funcionário: $email")
                    }
                }
            }
        }
    }

    private fun buscarRelatorio() {
        estoqueRepository.getRelatorioMensal(idEmpresa)
            .enqueue(object : retrofit2.Callback<List<RelatorioDTO>> {
                override fun onResponse(
                    call: retrofit2.Call<List<RelatorioDTO>>,
                    response: retrofit2.Response<List<RelatorioDTO>>
                ) {
                    if (response.isSuccessful)
                        fillTable(response.body() ?: emptyList())
                }

                override fun onFailure(call: retrofit2.Call<List<RelatorioDTO>>, t: Throwable) {
                    Log.e("Relatório", "Falha: ${t.message}", t)
                }
            })
    }

    private fun fillTable(data: List<RelatorioDTO>) {
        val table = binding?.tableLayout ?: return
        table.removeAllViews()

        val months = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        val monthMap = data.associateBy { it.monthOutput - 1 }

        months.forEachIndexed { index, month ->
            val row = TableRow(requireContext())

            val totals = monthMap[index]
            row.addView(createCell(month))
            row.addView(createCell((totals?.totalInput ?: 0).toString()))
            row.addView(createCell((totals?.totalOutput ?: 0).toString()))
            row.addView(createCell((totals?.totalDiscard ?: 0).toString()))

            table.addView(row)
        }
    }

    private fun createCell(text: String) = TextView(requireContext()).apply {
        this.text = text
        setPadding(8, 8, 8, 8)
        setTextColor(ContextCompat.getColor(requireContext(), R.color.PretoNaoPuro))
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.borda_cell)
        typeface = ResourcesCompat.getFont(requireContext(), R.font.inclusive_sans)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}