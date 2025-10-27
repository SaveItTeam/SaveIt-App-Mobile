package com.example.projetosaveit.ui.toolbar

object ToolbarFactory {
    fun criarToolbar(plano: Int?, tipoFunc: Boolean?): ToolbarProvider {
        return when {
            tipoFunc != null && tipoFunc -> AdminToolbar()
            plano != null && plano == 0 -> SemPlanoToolbar()
            plano != null && plano == 1 -> AdminToolbar()
            tipoFunc != null && !tipoFunc -> FuncionarioToolbar()
            else -> SemPlanoToolbar()
        }
    }
}