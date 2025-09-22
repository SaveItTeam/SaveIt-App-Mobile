package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.google.firebase.auth.FirebaseAuth

class ConfiguracoesPerfil : AppCompatActivity() {
    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuracoes_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (findViewById<Button>(R.id.botaoLogOut)).setOnClickListener { v ->
            objAutenticar.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}