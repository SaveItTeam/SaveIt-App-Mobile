package com.example.projetosaveit.ui

import android.media.Image
import android.os.Bundle
import android.view.View
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class AtualizarSenha : AppCompatActivity() {
    val objAutenticar = FirebaseAuth.getInstance()
    var idUsuario : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_atualizar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val origem = intent.getStringExtra("origem")
        var senhaNova = findViewById<EditText>(R.id.senhaNova)
        var senhaNova2 = findViewById<EditText>(R.id.senhaNova2)
        var senhaAtual = findViewById<EditText>(R.id.senhaAtual)
        val voltar = findViewById<ImageView>(R.id.voltarSenha)

        GetEmpresa.pegarEmailEmpresa(objAutenticar.currentUser?.email.toString()) { empresa ->
            if (empresa != null) {
                idUsuario = empresa.id
            } else {
                Toast.makeText(this, "Erro ao obter ID da empresa.", Toast.LENGTH_SHORT).show()
            }

        }

        voltar.setOnClickListener {
            finish()
        }

        if (origem == "logado") {
            senhaNova2.visibility = View.VISIBLE
            senhaNova.visibility = View.VISIBLE
            senhaAtual.visibility = View.VISIBLE
        } else {
            senhaNova2.visibility = View.GONE
            senhaNova.visibility = View.GONE
            senhaAtual.visibility = View.GONE
        }

        val btAlterarSenha : Button = findViewById(R.id.btAlterarSenha2)
        btAlterarSenha.setOnClickListener {

            if (senhaNova.text.toString().isEmpty() || senhaNova2.text.toString().isEmpty() || senhaAtual.text.toString().isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }

            if (senhaNova.text.toString().length < 6 || senhaNova2.text.toString().length < 6) {
                Toast.makeText(this, "A senha precisa ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
            }

            if (senhaNova.text.toString() != senhaNova2.text.toString()) {
                Toast.makeText(this, "As senhas são diferentes!", Toast.LENGTH_SHORT).show()
            } else {
                if (origem == "logado") {
                    alterarSenha(senhaNova, senhaAtual)
                } else {
                    esqueceuSenha()
                }
            }
        }
    }

    fun alterarSenha(senhaNova: EditText, senhaAtual: EditText) {
        val user =  objAutenticar.currentUser

        val senhaNovaStr = senhaNova.text.toString()
        val senhaAtualStr = senhaAtual.text.toString()

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, senhaAtualStr)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(senhaNovaStr).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            patchSenhaEmpresa(idUsuario, senhaNova.text.toString())
                            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Erro ao alterar senha: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Senha atual incorreta!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun patchSenhaEmpresa(id: Long, senha: String) {
        val updates = mapOf("password" to senha)
        val empresaRepository = EmpresaRepository()
        empresaRepository.patchEmpresa(id, updates).enqueue(object : retrofit2.Callback<okhttp3.ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<okhttp3.ResponseBody>,
                response: retrofit2.Response<okhttp3.ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AtualizarSenha, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AtualizarSenha, "Erro ao atualizar senha!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<okhttp3.ResponseBody>, t: Throwable) {
                Toast.makeText(this@AtualizarSenha, "Erro: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun esqueceuSenha() {

        val email = findViewById<EditText>(R.id.emailAtualizarS).text.toString().trim()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "E-mail de redefinição enviado!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}