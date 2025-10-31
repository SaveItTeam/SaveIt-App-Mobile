package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.ui.perfil.Perfil
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Planos : AppCompatActivity() {
    private val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    private val empresaRepository = EmpresaRepository()
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_planos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val empresaLogada: FirebaseUser = objAutenticar.currentUser!!

        GetEmpresa.pegarEmailEmpresa(empresaLogada.email.toString()) { empresa ->
            if (empresa != null) {
                // É empresa
                if (empresa.planId == 2) {
                    findViewById<Button>(R.id.btComprar).text = "Já sou Pro"
                } else {
                    id = empresa.id
                }
            } else {
                // É funcionário
                findViewById<Button>(R.id.btComprar).text = "Já sou Pro"
            }
        }

        findViewById<Button>(R.id.btComprar).setOnClickListener {
            Toast.makeText(this, "Plano SaveIt Pro assinado!", Toast.LENGTH_SHORT).show()

            empresaRepository.patchEmpresa(id, mapOf("planId" to 2)).enqueue(object : retrofit2.Callback<okhttp3.ResponseBody> {
                override fun onResponse(
                    call: retrofit2.Call<okhttp3.ResponseBody>,
                    response: retrofit2.Response<okhttp3.ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@Planos, "Plano atualizado com sucesso!", Toast.LENGTH_SHORT).show()

                        Toast.makeText(this@Planos, "Insira um funcionário administrador para continuar.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@Planos, InserirFuncionario::class.java)
                        startActivity(intent)

                        finish()
                    } else {
                        Toast.makeText(this@Planos, "Falha ao atualizar o plano.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<okhttp3.ResponseBody>,
                    t: Throwable
                ) {
                    Toast.makeText(this@Planos, "Erro na requisição: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}