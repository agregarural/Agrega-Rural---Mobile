package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.agregarural.databinding.ActivityMeusPedidosBinding

class MeusPedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeusPedidosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeusPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnEntrega.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MeusPedidosActivity::class.java)
            startActivity(intent)
        })
        binding.btnHome.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            startActivity(intent)
        })

        binding.btnmenu.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        })
        binding.btnperfil.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, HomeFragment::class.java)
            intent.putExtra("OPEN_PERFIL", true)
            startActivity(intent)
            finish()
        })

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
