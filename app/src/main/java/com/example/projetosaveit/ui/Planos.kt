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
import com.example.projetosaveit.ui.perfil.Perfil

class Planos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_planos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btBasico).setOnClickListener {
            Toast.makeText(this, "Plano Básico assinado!", Toast.LENGTH_SHORT).show()
            val Intent = Intent(this, Perfil::class.java)
            startActivity(Intent)
        }

        findViewById<Button>(R.id.btAvancado).setOnClickListener {
            Toast.makeText(this, "Plano Avançado assinado!", Toast.LENGTH_SHORT).show()
            val Intent = Intent(this, Perfil::class.java)
            startActivity(Intent)
        }
    }
}