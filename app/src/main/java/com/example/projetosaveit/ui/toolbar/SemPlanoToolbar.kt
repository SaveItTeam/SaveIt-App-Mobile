package com.example.projetosaveit.ui.toolbar

import androidx.navigation.ui.AppBarConfiguration
import com.example.projetosaveit.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SemPlanoToolbar : ToolbarProvider {
    override fun setupToolbar(navView: BottomNavigationView): AppBarConfiguration {
        navView.menu.clear()
        navView.inflateMenu(R.menu.bottom_nav_admin_sp)
        return AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.perfil
        ).build()
    }
}