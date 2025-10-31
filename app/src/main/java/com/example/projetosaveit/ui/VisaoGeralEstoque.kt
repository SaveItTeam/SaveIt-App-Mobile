package com.example.projetosaveit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.recycleView.ProdutoEstoqueRelatorio
import com.example.projetosaveit.adapter.recycleView.Venda
import com.example.projetosaveit.api.repository.EstoqueRepository
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response

class VisaoGeralEstoque : AppCompatActivity() {

    private lateinit var containerProdutos: LinearLayout
    private lateinit var containerTabela: LinearLayout
    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    var idEmpresa: Long = 0
    val repository : EstoqueRepository = EstoqueRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val emailEmpresa = objAutenticar.currentUser!!.email.toString()


        setContentView(R.layout.activity_visao_geral_estoque)

        containerProdutos = findViewById(R.id.containerProdutos)

        containerTabela = findViewById(R.id.containerTabela)


        GetEmpresa.pegarEmailEmpresa(emailEmpresa) { empresa ->
            if ((empresa != null)&&(empresa.id.toInt() != 0)) {
                idEmpresa = empresa.id
                pegarProdutosPorEmpresa(idEmpresa)
                getMovimentacoesProduto(idEmpresa)
            }else {
                GetFuncionario.pegarEmailFunc(emailEmpresa) { funcionario ->
                    if ((funcionario != null )&&(funcionario!!.id.toInt() != 0)) {
                        idEmpresa = funcionario!!.enterpriseId
                        pegarProdutosPorEmpresa(idEmpresa)
                        getMovimentacoesProduto(idEmpresa)
                    }else {
                        Toast.makeText(this@VisaoGeralEstoque, "não foi possivel pegar o id do usuario", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }


    private fun atualizarProgresso(produtos: List<ProdutoEstoqueRelatorio>?) {
        val inflater = LayoutInflater.from(this)
        containerProdutos.removeAllViews()

        produtos?.forEach { produto ->
            val view = inflater.inflate(R.layout.item_progress_bar, containerProdutos, false)

            val nome = view.findViewById<TextView>(R.id.txtNomeProduto)
            val percentual = view.findViewById<TextView>(R.id.txtPercentual)
            val progress = view.findViewById<ProgressBar>(R.id.progressBar)

            nome.text = produto.productName
            val porcentagem = (produto.quantity.toDouble() / produto.maxQuantity * 100)
            percentual.text = String.format("%.1f%%", porcentagem)
            progress.progress = produto.quantity.toInt()
            progress.max = produto.maxQuantity.toInt()

            containerProdutos.addView(view)
        }
    }



    private fun montarTabela(vendas: List<Venda>) {
        val inflater = LayoutInflater.from(this)

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(0xFF3E5C42.toInt())
            setPadding(16, 12, 16, 12)
        }


        val headerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val scale = resources.displayMetrics.density
        val marginTopInPx = (24 * scale + 0.5f).toInt()
        val marginLeftRightPx = (60 * scale + 0.5f).toInt()
        headerParams.setMargins(marginLeftRightPx, marginTopInPx, marginLeftRightPx, 0)
        header.layoutParams = headerParams

        val txtNomeHeader = TextView(this).apply {
            text = "Nome"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val txtVendasHeader = TextView(this).apply {
            text = "Vendas"
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
        }

        header.addView(txtNomeHeader)
        header.addView(txtVendasHeader)
        containerTabela.addView(header)

        vendas.forEachIndexed { index, venda ->
            val linha = inflater.inflate(R.layout.item_tabela_venda, containerTabela, false)
            val nome = linha.findViewById<TextView>(R.id.txtNomeProduto)
            val qtd = linha.findViewById<TextView>(R.id.txtVendas)

            nome.text = venda.productName
            qtd.text = venda.quantityOutput

            if (index % 2 == 0) {
                linha.setBackgroundResource(R.color.Verde)
            } else {
                linha.setBackgroundResource(R.color.Verde50)
            }

            containerTabela.addView(linha)
        }

    }

    private fun pegarProdutosPorEmpresa(idEmpresa : Long) {
        repository.getQuantidadesProduto(idEmpresa).enqueue(object : retrofit2.Callback<List<ProdutoEstoqueRelatorio>>{
            override fun onResponse(
                p0: Call<List<ProdutoEstoqueRelatorio>?>,
                p1: Response<List<ProdutoEstoqueRelatorio>?>
            ) {
                if (p1.isSuccessful) {
                    atualizarProgresso(p1.body())
                }else {
                    val errorBodyString = try {
                        p1.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(this@VisaoGeralEstoque, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                p0: Call<List<ProdutoEstoqueRelatorio>?>,
                p1: Throwable
            ) {
                Toast.makeText(this@VisaoGeralEstoque, "Erro na requisição: " + p1.message, Toast.LENGTH_LONG).show()
            }


        })
    }

    fun getMovimentacoesProduto(idEmprea : Long) {
        repository.getMovimentacoesProduto(idEmprea).enqueue(object : retrofit2.Callback<List<Venda>>{
            override fun onResponse(
                p0: Call<List<Venda>?>,
                p1: Response<List<Venda>?>
            ) {
                if(p1.isSuccessful) {
                    montarTabela(p1.body()!!)
                }else {
                    val errorBodyString = try {
                        p1.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(this@VisaoGeralEstoque, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                p0: Call<List<Venda>?>,
                p1: Throwable
            ) {
                Toast.makeText(this@VisaoGeralEstoque, "Erro na requisição: " + p1.message, Toast.LENGTH_LONG).show()
            }

        })
    }

}
