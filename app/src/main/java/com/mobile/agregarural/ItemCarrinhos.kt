package com.mobile.agregarural

data class ItemCarrinhos(
    val produto: Produto,
    var quantidade: Int,
    var selecionado: Boolean = false
)