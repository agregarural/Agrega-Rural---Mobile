package com.mobile.agregarural

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MeusPedidosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_pedidos)

        // Simulação de dados
        val produtosFake = listOf(
            Produto(1, "Semente de Milho", 150.0, ""),
            Produto(2, "Fertilizante NPK", 89.90, ""),
            Produto(3, "Ração Aves", 45.0, "")
        )

        val pedidosFake = listOf(
            Pedido("#091254", PedidoStatus.ENTREGUE, "Detalhes...", "06/12/2012", ""),
            Pedido("#083341", PedidoStatus.EM_ANDAMENTO, "A caminho...", null, ""),
            Pedido("#091255", PedidoStatus.CANCELADO, "Pagamento recusado", null, "")
        )

        // Configurar RecyclerViews
        val rvUltimos = findViewById<RecyclerView>(R.id.rvUltimosPedidos)
        rvUltimos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvUltimos.adapter = ProdutoAdapter(produtosFake)

        val rvLista = findViewById<RecyclerView>(R.id.rvListaPedidos)
        rvLista.layoutManager = LinearLayoutManager(this)
        rvLista.adapter = PedidoAdapter(pedidosFake)

        val rvSugestoes = findViewById<RecyclerView>(R.id.rvSugestoes)
        rvSugestoes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSugestoes.adapter = ProdutoAdapter(produtosFake)
    }
}