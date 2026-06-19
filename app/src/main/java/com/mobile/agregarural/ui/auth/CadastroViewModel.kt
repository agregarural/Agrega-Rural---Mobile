package com.mobile.agregarural.ui.auth

import androidx.lifecycle.ViewModel

class CadastroViewModel : ViewModel() {
    var nome: String = ""
    var email: String = ""
    var cpf: String = ""
    var cep: String = ""
    var cooperativa: String = ""
    var ehAssociado: Boolean = false
    var matricula: String = ""
    var tipoUsuario: String = "cliente"
    var coopUid: String = ""
}