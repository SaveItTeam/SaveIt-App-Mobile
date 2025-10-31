package com.example.projetosaveit.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.util.GetEmpresa
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AtualizarNome : AppCompatActivity() {
    private val empresaRepository = EmpresaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_atualizar_nome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btAlterarNome).setOnClickListener {
            val email = findViewById<EditText>(R.id.emailAtualizarN).text.toString()

            GetEmpresa.pegarEmailEmpresa(email) { empresa ->
                if (empresa != null) {
                    mudarNome(empresa.id)
                } else {
                    Log.d("erro", "pegarEmailEmpresa: Empresa nao encontrada")
                }
            }
        }

        findViewById<ImageView>(R.id.btVoltarAtualizarNome).setOnClickListener {
            finish()
        }
    }

    private fun mudarNome(id: Long) {
        empresaRepository.patchEmpresa(id, mapOf("name" to findViewById<EditText>(R.id.nomeNovo).text.toString())).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AtualizarNome, "Nome atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AtualizarNome, "Erro ao atualizar nome!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("erro", "Erro: ${t.message}")
            }
        })
    }
}