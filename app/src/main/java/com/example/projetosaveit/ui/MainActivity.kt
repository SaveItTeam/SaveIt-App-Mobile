package com.example.projetosaveit.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.projetosaveit.R
import com.example.projetosaveit.databinding.ActivityMainBinding
import com.example.projetosaveit.ui.toolbar.ToolbarFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val navView: BottomNavigationView = binding!!.navView
        val plano: Int? = intent.extras?.getInt("plano")
        val tipoFunc: Boolean? = intent.extras?.getBoolean("tipoFunc")

        Log.d("MainActivity", "plano: $plano, tipoFunc: $tipoFunc")
//        Factory Method
        val toolbarProvider = ToolbarFactory.criarToolbar(plano, tipoFunc)
        val appBarConfiguration = toolbarProvider.setupToolbar(navView)

        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)
//        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupWithNavController(navView, navController)
    }
}