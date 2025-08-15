package com.example.projetosaveit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashScreen : AppCompatActivity() {

    var objAutenticacao : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val imageView: ImageView = findViewById(R.id.imageView)
        val tempoSplash = 5000 // 5 segundos

        Handler(Looper.getMainLooper()).postDelayed({
            val userLogin : FirebaseUser? = objAutenticacao.currentUser;
            if (userLogin != null) {
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            startActivity(Intent(this, Login
            ::class.java))
            finish()
        }, tempoSplash.toLong())
    }
}