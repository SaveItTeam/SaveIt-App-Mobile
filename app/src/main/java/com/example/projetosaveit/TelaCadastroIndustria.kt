package com.example.projetosaveit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class TelaCadastroIndustria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_tela_cadastro_industria)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btCadastro : Button = findViewById(R.id.btCadastrar)
        btCadastro.setOnClickListener {
            salvarUsuario()
        }
    }

    fun salvarUsuario() {
        val autenticao : FirebaseAuth = FirebaseAuth.getInstance()

        val nomeTxt : String = findViewById<EditText>(R.id.nomeCadastro).text.toString()
        val emailTxt : String = findViewById<EditText>(R.id.emailCadastro).text.toString()
        val cnpjTxt : String = findViewById<EditText>(R.id.cnpjCadastro).text.toString()
        val senhaTxt : String = findViewById<EditText>(R.id.senhaCadastro).text.toString()
        val senhaTxt2 : String = findViewById<EditText>(R.id.senhaRepetidaCadastro).text.toString()
        val categoriaTxt : String = findViewById<EditText>(R.id.editText14).text.toString()

        if (nomeTxt.isEmpty() || emailTxt.isEmpty() || cnpjTxt.isEmpty() || senhaTxt.isEmpty() || categoriaTxt.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        if (senhaTxt.length >= 6) {
            if (senhaTxt == senhaTxt2) {
                autenticao.createUserWithEmailAndPassword(emailTxt, senhaTxt)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = autenticao.getCurrentUser()

                            val profileUpdate: UserProfileChangeRequest =
                                UserProfileChangeRequest.Builder()
                                    .setDisplayName(nomeTxt)
                                    .build()

                            user?.updateProfile(profileUpdate)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Empresa cadastrada com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Erro ao cadastrar empresa!", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("CadastroIndustria", "Erro: ${task.exception?.message}")
                        }
                    }
            } else {
                Toast.makeText(this, "As senhas são diferentes!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
        }
    }
}