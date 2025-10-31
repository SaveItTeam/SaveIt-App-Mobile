package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.VitrineRepository
import com.example.projetosaveit.databinding.ActivityMainBinding
import com.example.projetosaveit.service.VitrineForegroundService
import com.example.projetosaveit.ui.toolbar.ToolbarFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val repositoryVitrine = VitrineRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val navView: BottomNavigationView = binding!!.navView
        val plano: Int? = intent.extras?.getInt("plano")
        val tipoFunc: Boolean? = intent.extras?.getBoolean("tipoFunc")
        val isEmpresa: Boolean = intent.extras?.getBoolean("isEmpresa") ?: false

        Log.d("MainActivity", "plano: $plano, tipoFunc: $tipoFunc")

        // Factory Method
        val toolbarProvider = ToolbarFactory.criarToolbar(plano, tipoFunc, isEmpresa)
        val appBarConfiguration = toolbarProvider.setupToolbar(navView)

        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)
        setupWithNavController(navView, navController)

        if (isEmpresa && plano == 1) {
            navView.post {
                navController.navigate(R.id.navigation_vitrine)
            }
        }

        val serviceIntent = Intent(this, VitrineForegroundService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}