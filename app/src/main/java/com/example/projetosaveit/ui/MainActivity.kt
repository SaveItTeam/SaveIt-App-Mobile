package com.example.projetosaveit.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.projetosaveit.R
import com.example.projetosaveit.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_vitrine,
            R.id.navigation_notifications,
            R.id.relatorio,
            R.id.perfil
        )
            .build()
        val navController = findNavController(this, R.id.nav_host_fragment_activity_main)
//        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupWithNavController(binding!!.navView, navController)
    }
}