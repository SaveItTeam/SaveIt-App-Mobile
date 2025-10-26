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
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : AppCompatActivity() {

    private val objAutenticar : FirebaseAuth = FirebaseAuth.getInstance()
    private var empresaVar = false
    private var tipoFunc = false
    private var plano: Int = 0

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

        val btEsqueceuSenha : TextView = findViewById(R.id.esqueciSenha)

        btEsqueceuSenha.setOnClickListener {
            val intent = Intent(this, AtualizarSenha::class.java)
            intent.putExtra("origem", "esqueceu")
            startActivity(intent)
        }

        val bt : Button = findViewById(R.id.botaoLogin)
        bt.setOnClickListener {
            val txtEmail: String = findViewById<EditText>(R.id.emailLogin).text.toString()
            val txtSenha: String = findViewById<EditText>(R.id.senhaLogin).text.toString()

            if (txtEmail.isBlank() || txtSenha.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            objAutenticar.signInWithEmailAndPassword(txtEmail, txtSenha)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        GetEmpresa.pegarEmailEmpresa(txtEmail) { empresa ->
                            if (empresa != null) {
                                // É empresa
                                val plano = empresa.planId
                                abrirMainActivity(plano = plano)
                            } else {
                                // Não é empresa, verifica se é funcionário
                                GetFuncionario.pegarEmailFunc(txtEmail) { func ->
                                    val tipoFunc = func?.admin ?: false
                                    abrirMainActivity(tipoFunc = tipoFunc)
                                }
                            }
                        }
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

    private fun abrirMainActivity(plano: Int? = null, tipoFunc: Boolean? = null) {
        val intent = Intent(this, MainActivity::class.java)
        if (plano != null) {
            intent.putExtra("plano", plano)
        }
        if (tipoFunc != null) {
            intent.putExtra("tipoFunc", tipoFunc)
        }
        startActivity(intent)
        finish()
    }
}