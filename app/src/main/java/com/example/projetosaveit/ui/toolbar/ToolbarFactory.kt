package com.example.projetosaveit.ui.toolbar

object ToolbarFactory {
    fun criarToolbar(plano: Int?, tipoFunc: Boolean?): ToolbarProvider {
        return when {
            tipoFunc != null && tipoFunc && plano == 1 -> AdminToolbar()
            tipoFunc != null && !tipoFunc && plano == 1 -> FuncionarioToolbar()
            plano != null && plano == 0 -> SemPlanoToolbar()
            plano != null && plano == 1 -> AdminToolbar()
            else -> SemPlanoToolbar()
        }
    }
}