package com.mobile.agregarural

// Modelo para os produtos que aparecem nas listas horizontais
data class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    val imagemUrl: String // Usaremos para carregar via Glide/Picasso depois
)

// Modelo para os pedidos
data class Pedido(
    val id: String,
    val status: PedidoStatus,
    val detalhes: String,
    val data: String?,
    val imagemProdutoUrl: String
)

enum class PedidoStatus {
    ENTREGUE, EM_ANDAMENTO, CANCELADO
}