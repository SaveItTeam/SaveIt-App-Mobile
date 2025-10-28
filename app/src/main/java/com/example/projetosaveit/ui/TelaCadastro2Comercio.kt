package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.model.EmpresaInsertDTO
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class TelaCadastro2Comercio : AppCompatActivity() {

//    val repository : Repos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_tela_cadastro2_comercio)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (findViewById<Button>(R.id.btnProximo)).setOnClickListener { v ->
            val nomeEmpresa : String = findViewById<TextInputEditText>(R.id.nomeEmpresaInput).text.toString()
            val cnpjEmpresa : String = findViewById<TextInputEditText>(R.id.cnpjEmpresaInput).text.toString()
            val senhaEmpresa : String = findViewById<TextInputEditText>(R.id.senhaEmpresaInput).text.toString()
            val telefoneEmpresa : String = findViewById<TextInputEditText>(R.id.telefoneEmpresaInput).text.toString()
            val senhaRepetida : String = findViewById<TextInputEditText>(R.id.repitirSenhaInput).text.toString()
            val emailEmpresa : String = findViewById<TextInputEditText>(R.id.emailEmpresaInput).text.toString()

            if (nomeEmpresa.isEmpty() || cnpjEmpresa.isEmpty() || emailEmpresa.isEmpty() || telefoneEmpresa.isEmpty() || senhaEmpresa.isEmpty() || senhaRepetida.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senhaEmpresa.length >= 6) {
                if (senhaEmpresa == senhaRepetida) {
                    val intent : Intent = Intent(this, CadastroEndereco::class.java)

                    val bundle : Bundle = Bundle()
                    bundle.putString("nomeEmpresa", nomeEmpresa)
                    bundle.putString("cnpjEmpresa", cnpjEmpresa)
                    bundle.putString("senhaEmpresa", senhaEmpresa)
                    bundle.putString("telefoneEmpresa", telefoneEmpresa)
                    bundle.putString("emailEmpresa", emailEmpresa)

                    intent.putExtras(bundle)

                    startActivity(intent)
                } else {
                    Toast.makeText(this, "As senhas são diferentes!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show()
            }

            findViewById<TextView>(R.id.btnLogin).setOnClickListener {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}