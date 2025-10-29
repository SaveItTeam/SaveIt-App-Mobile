package com.example.projetosaveit.ui

import android.app.AlertDialog
import android.widget.Toast
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.AdapterProduto
import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.api.repository.LoteRepository
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Response

class AdicionarProdutoVitrine : AppCompatActivity() {
    lateinit var adapter : AdapterProduto
    val repository : LoteRepository = LoteRepository()
    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    var idEmpresa : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_produto_vitrine)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recycle : RecyclerView = findViewById<RecyclerView>(R.id.rcProds)
        recycle.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        adapter = AdapterProduto()
        recycle.adapter = adapter

        val email = objAutenticar.currentUser?.email.toString()

        GetEmpresa.pegarEmailEmpresa(email.toString()) { empresa ->
            if (empresa != null) {
                idEmpresa = empresa.id
                carregarProdutos(idEmpresa)
            } else {
                GetFuncionario.pegarEmailFunc(email.toString()) { func ->
                    if (func != null) {
                        idEmpresa = func.enterpriseId
                        carregarProdutos(idEmpresa)
                    } else {
                        Toast.makeText(this, "Erro ao identificar usuário.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        findViewById<ImageView>(R.id.btVoltarAdicaoVitrine).setOnClickListener {
            finish()
        }

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
                Toast.makeText(this@AdicionarProdutoVitrine, "Erro ao carregar produtos: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}