package com.example.projetosaveit.ui.toolbar

object ToolbarFactory {
    fun criarToolbar(plano: Int?, tipoFunc: Boolean?, isEmpresa: Boolean): ToolbarProvider {
        return when {
            isEmpresa && plano == 1 -> SemPlanoToolbar()
            isEmpresa && plano == 2 -> AdminToolbar()
            !isEmpresa && tipoFunc == true -> AdminToolbar()
            !isEmpresa && tipoFunc == false -> FuncionarioToolbar()
            else -> SemPlanoToolbar()
        }
    }
}