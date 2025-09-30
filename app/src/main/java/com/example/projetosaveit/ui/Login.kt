package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : AppCompatActivity() {

    val objAutenticar : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val bt : Button = findViewById(R.id.botaoLogin)
        bt.setOnClickListener {
            val txtEmail: String = findViewById<EditText>(R.id.emailLogin).text.toString()
            val txtSenha: String = findViewById<EditText>(R.id.senhaLogin).text.toString()

            objAutenticar.signInWithEmailAndPassword(txtEmail, txtSenha)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthInvalidUserException -> {
                                Toast.makeText(this, "Usuário não cadastrado", Toast.LENGTH_LONG).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(this, "E-mail ou senha inválidos", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(this, "Erro: ${exception?.message}", Toast.LENGTH_LONG).show()
                                exception?.printStackTrace()
                            }
                        }
                    }
                }
        }

        val btCadastro : TextView = findViewById(R.id.btnCadastro)
        btCadastro.setOnClickListener {
            val intent = Intent(this, TelaCadastro2Comercio::class.java)
            startActivity(intent)
        }
    }
}