package com.mobile.agregarural

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MeusPedidosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_pedidos)

        // Usando MockDatabase centralizado
        val produtosFake = MockDatabase.produtos
        val pedidosFake = MockDatabase.pedidos

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
