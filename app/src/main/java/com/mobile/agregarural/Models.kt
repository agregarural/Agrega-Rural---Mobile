package com.mobile.agregarural

// Modelos
data class Categoria(
    val nome: String,
    val imagemResId: Int
)

data class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    val imagemResId: Int = 0,
    val imagemUrl: String = "",
    val especificacao: String = ""
)

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

// Simulação de Banco de Dados Única
object MockDatabase {
    val categorias = listOf(
        Categoria("Sementes", R.drawable.ic_sementes),
        Categoria("Fertilizantes", R.drawable.ic_fertilizantes),
        Categoria("Defensivos", R.drawable.ic_defensivos),
        Categoria("Aves", R.drawable.ic_aves),
        Categoria("Suínos", R.drawable.ic_suinos)
    )

    val produtos = listOf(
        Produto(1, "Semente de Milho", 150.0, R.drawable.prod_racoeszardo, "", "Saco 25KG"),
        Produto(2, "Fertilizante NPK", 89.90, R.drawable.prod_racoeszardo, "", "Saco 50KG"),
        Produto(3, "Rações Zardo", 500.0, R.drawable.prod_racoeszardo, "", "Saco 25KG"),
        Produto(4, "Ração Aves", 45.0, R.drawable.prod_racoeszardo, "", "Saco 10KG")

    )

    val pedidos = listOf(
        Pedido("#091254", PedidoStatus.ENTREGUE, "Detalhes...", "06/12/2012", ""),
        Pedido("#083341", PedidoStatus.EM_ANDAMENTO, "A caminho...", null, ""),
        Pedido("#091255", PedidoStatus.CANCELADO, "Pagamento recusado", null, "")
    )
}
