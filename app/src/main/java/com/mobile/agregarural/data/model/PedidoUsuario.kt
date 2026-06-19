package com.mobile.agregarural.data.model

data class PedidoUsuario(
    val pedidoId: String = "",
    var status: String = "",
    val valorTotal: Double = 0.0,
    val dataPedido: String = "",
    val dataEntrega: String = "",
    val dataPedidoMillis: Long = 0L,
    val dataEntregaMillis: Long = 0L,
    val pago: Boolean = false,
    val itens: List<ItemPedidoUsuario> = emptyList()
)

data class ItemPedidoUsuario(
    val nome: String = "",
    val precoUnitario: Double = 0.0,
    val quantidade: Int = 0,
    val subtotal: Double = 0.0,
    val imagem: String = "",
    val categoria: String = "",
    val descricao: String = ""
)