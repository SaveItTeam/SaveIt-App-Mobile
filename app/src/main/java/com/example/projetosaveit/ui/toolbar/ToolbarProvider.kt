package com.example.projetosaveit.ui.toolbar

import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView

interface ToolbarProvider {
    fun setupToolbar(navView: BottomNavigationView): AppBarConfiguration
}