package com.example.projetosaveit.ui

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EstoqueRepository
import com.example.projetosaveit.api.repository.VitrineRepository
import com.example.projetosaveit.model.RelatorioProdutoDTO
import com.example.projetosaveit.ui.CadastroEndereco
import com.example.projetosaveit.util.GetEmpresa
import com.github.mikephil.charting.components.XAxis
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class TelaEstoque : AppCompatActivity() {

    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    var enterpriseId : Long = 0
    val repository : EstoqueRepository = EstoqueRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_estoque)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras
        if (bundle != null) {
            findViewById<TextView>(R.id.textView14).text = bundle.getString("nomeProduto")
            findViewById<TextView>(R.id.textView19).text = bundle.getString("categoriaProduto")
            findViewById<TextView>(R.id.textView28).text = bundle.getString("marcaProduto")
            findViewById<TextView>(R.id.textView30).text = bundle.getString("descricaoProduto")
            findViewById<TextView>(R.id.textView50).text = bundle.getString("dataEntradaProduto")
            findViewById<TextView>(R.id.textView23).text = bundle.getString("validadeProduto")
            findViewById<TextView>(R.id.textView46).text = bundle.getString("quantidadeProduto")
            findViewById<TextView>(R.id.textView25).text = bundle.getString("skuProduto")

            val idProduto : Long = bundle.getLong("idProduto")

            val quantidadeStr = bundle.getString("quantidadeMostrada")
            val quantidadeMaxStr = bundle.getString("quantidadeMaximaProduto")

            val quantidade = quantidadeStr?.toIntOrNull() ?: 0
            val quantidadeMax = quantidadeMaxStr?.toIntOrNull() ?: 1

            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.max = quantidadeMax
            progressBar.progress = quantidade

            val imageUrl = bundle.getString("imagemProduto")
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(findViewById(R.id.imageView28))
            }

            val barChart = findViewById<BarChart>(R.id.barChart)
            barChart.description.isEnabled = false
            barChart.setDrawGridBackground(false)
            barChart.setFitBars(true)
            barChart.animateY(1500)

            GetEmpresa.pegarEmailEmpresa(objAutenticar.currentUser?.email!!) { empresa ->
                if (empresa != null) {
                    enterpriseId  = empresa.id
                }
            }

        }
    }

    private fun getRelatorioProduto(productId : Long, enterpriseId : Long) {
        repository.getRelatorioProduto(productId, enterpriseId).enqueue(object : retrofit2.Callback<List<RelatorioProdutoDTO>> {
            override fun onResponse(
                p0: Call<List<RelatorioProdutoDTO>?>,
                p1: Response<List<RelatorioProdutoDTO>?>
            ) {
                if (p1.isSuccessful) {
                    mostrarGrafico(p1.body()!!)
                }else {
                    val errorBodyString = try {
                        p1.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(this@TelaEstoque, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()                }
            }

            override fun onFailure(
                p0: Call<List<RelatorioProdutoDTO>?>,
                p1: Throwable
            ) {
                Toast.makeText(this@TelaEstoque, p1.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun mostrarGrafico(dados: List<RelatorioProdutoDTO>) {
        val barChart = findViewById<BarChart>(R.id.barChart)

        val entradasInput = ArrayList<BarEntry>()
        val entradasOutput = ArrayList<BarEntry>()

        for ((index, item) in dados.withIndex()) {
            entradasInput.add(BarEntry(index.toFloat(), item.mouthInput.toFloat()))
            entradasOutput.add(BarEntry(index.toFloat(), item.totalOutput.toFloat()))
        }

        val dataSetInput = BarDataSet(entradasInput, "Entradas")
        dataSetInput.color = getColor(R.color.VerdeEscuro)

        val dataSetOutput = BarDataSet(entradasOutput, "Saídas")
        dataSetOutput.color = getColor(R.color.Verde)

        val data = BarData(dataSetInput, dataSetOutput)
        data.barWidth = 0.3f

        barChart.data = data

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        barChart.groupBars(0f, 0.4f, 0.05f)
        barChart.invalidate()
    }

}