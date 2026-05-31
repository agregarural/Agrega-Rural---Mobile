package com.mobile.agregarural

data class Endereco(
    var id: String = "",
    var cep: String = "",
    var numero: String = "",
    var logradouro: String = "",
    var complemento: String = "",
    var referencia: String = "",
    var criadoEm: Long = 0L
) {
    fun formatado(): String {
        return buildString {
            append("$logradouro, $numero")

            if (complemento.isNotBlank()) {
                append(" - $complemento")
            }

            append("\nCEP: $cep")

            if (referencia.isNotBlank()) {
                append("\nReferência: $referencia")
            }
        }
    }
}