package com.example.projetosaveit.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.AdapterVitrine
import com.example.projetosaveit.adapter.recycleView.Vitrine
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.api.repository.VitrineRepository
import com.google.firebase.auth.FirebaseAuth

class MinhaVitrine : AppCompatActivity() {
    val objAutenticar = FirebaseAuth.getInstance()
    val repository : VitrineRepository = VitrineRepository()
    var adapter = AdapterVitrine()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_minha_vitrine)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        GetEmpresa.pegarEmailEmpresa(objAutenticar.currentUser?.email.toString()) { empresa ->
            if (empresa != null) {
                val idEmpresa = empresa.id
                getarVitrinePorIdEmpresa(idEmpresa)
            } else {
                Toast.makeText(this, "Erro ao obter empresa.", Toast.LENGTH_SHORT).show()
            }
        }

        val recycleView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.minhaVitrineRc)
        recycleView.adapter = adapter

    }

    fun getarVitrinePorIdEmpresa(idEmpresa : Long) {
        repository.getVitrineEmpresa(idEmpresa).enqueue(object : retrofit2.Callback<List<Vitrine>> {
            override fun onResponse(
                call: retrofit2.Call<List<Vitrine>>,
                response: retrofit2.Response<List<Vitrine>>
            ) {
                if (response.isSuccessful) {
                    val produtos = response.body()
                    adapter.listVitrine = produtos ?: emptyList()
                    if (adapter.listVitrine.isEmpty()) {
                        Toast.makeText(this@MinhaVitrine, "Vitrine vazia.", Toast.LENGTH_SHORT).show()
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MinhaVitrine, "Erro ao obter vitrine.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: retrofit2.Call<List<Vitrine>>,
                t: Throwable
            ) {
                Toast.makeText(this@MinhaVitrine, "Falha na comunicação: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}