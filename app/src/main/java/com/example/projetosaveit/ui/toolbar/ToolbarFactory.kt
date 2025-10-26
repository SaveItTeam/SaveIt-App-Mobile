package com.example.projetosaveit.ui.toolbar

object ToolbarFactory {
    fun criarToolbar(plano: Int?, tipoFunc: Boolean?): ToolbarProvider {
        return when {
            plano != null && plano == 0 -> SemPlanoToolbar()
            tipoFunc != null && tipoFunc -> AdminToolbar()
            tipoFunc != null && !tipoFunc -> FuncionarioToolbar()
            else -> SemPlanoToolbar()
        }
    }
}