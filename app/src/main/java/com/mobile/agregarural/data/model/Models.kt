package com.mobile.agregarural.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Produto(
    val nome: String = "",
    val preco: Double = 0.0,
    val descricao: String = "",
    val categoria: String = "",
    val estoque: Int = 0,
    val imagem: String = ""
) : Parcelable

data class ItemCarrinho(
    val nome: String,
    val preco: Double,
    var quantidade: Int
)

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

object MockDatabase {
    val produtos = listOf(
        Produto(
            nome = "Semente de Milho",
            preco = 150.0,
            descricao = "Saco 25KG",
            categoria = "sementes",
            estoque = 10,
            imagem = ""
        )
    )

    val pedidos = listOf(
        Pedido("#091254", PedidoStatus.ENTREGUE, "Detalhes...", "06/12/2012", ""),
        Pedido("#083341", PedidoStatus.EM_ANDAMENTO, "A caminho...", null, ""),
        Pedido("#091255", PedidoStatus.CANCELADO, "Pagamento recusado", null, "")
    )
}