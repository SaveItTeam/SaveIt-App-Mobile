package com.example.projetosaveit.ui.toolbar

import androidx.navigation.ui.AppBarConfiguration
import com.example.projetosaveit.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminToolbar : ToolbarProvider {
    override fun setupToolbar(navView: BottomNavigationView): AppBarConfiguration {
        navView.menu.clear()
        navView.inflateMenu(R.menu.bottom_nav_admin)
        return AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_vitrine,
            R.id.chat,
            R.id.relatorio,
            R.id.perfil
        ).build()
    }
}