package com.mobile.agregarural.data.model

data class ItemCarrinhos(
    val produto: Produto,
    var quantidade: Int,
    var selecionado: Boolean = false,
    var precoUnitario: Double = produto.preco,
    var usuarioCooperado: Boolean = false
)