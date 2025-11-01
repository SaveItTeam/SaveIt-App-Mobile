package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.projetosaveit.R
import com.example.projetosaveit.util.AppLifecycleObserver
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    private val objAutenticacao: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val tempoSplash = 2500

        Handler(Looper.getMainLooper()).postDelayed({
            val userLogin = objAutenticacao.currentUser
            if (userLogin != null && userLogin.email != null) {

                GetEmpresa.pegarEmailEmpresa(userLogin.email!!) { empresa ->
                    if (empresa != null) {
                        val plano = empresa.planId

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("plano", plano)
                        intent.putExtra("isEmpresa", true)
                        AppLifecycleObserver.iniciar(empresa.id.toInt())
                        startActivity(intent)
                        Log.d("plano", plano.toString())
                        finish()
                    } else {
                        GetFuncionario.pegarEmailFunc(userLogin.email!!) { func ->
                            val tipoFunc = func?.isAdmin ?: false

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("plano", 2)
                            intent.putExtra("tipoFunc", tipoFunc)
                            intent.putExtra("isEmpresa", false)
                            AppLifecycleObserver.iniciar(func!!.enterpriseId.toInt())

                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }, tempoSplash.toLong())
    }
}