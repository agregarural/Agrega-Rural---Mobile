package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.databinding.ActivityHomeBinding
import androidx.recyclerview.widget.RecyclerView


/**
 * Activity principal do aplicativo (Home).
 * Responsável por exibir a lista de categorias e a vitrine de produtos,
 * além de gerenciar a navegação básica através da barra inferior.
 */
class Home : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterCategoria: CategoriaAdapter
    private lateinit var adapterProdutos: ProdutoItemAdapter

    private lateinit var binding: ActivityHomeBinding


    /**
     * Inicializa a activity, configura o View Binding, os listeners dos botões de navegação
     * e os RecyclerViews de categorias e produtos utilizando dados do [MockDatabase].
     *
     * @param savedInstanceState Estado salvo da instância anterior, se houver.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnEntrega.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MeusPedidosActivity::class.java)
            startActivity(intent)
        })
        binding.btnHome.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        })

        binding.btnmenu.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        })
        binding.btnperfil.setOnClickListener(View.OnClickListener {
            openPerfilFragment()
        })

        if (intent.getBooleanExtra("OPEN_PERFIL", false)) {
            openPerfilFragment()
        }

        //Configurando categorias usando MockDatabase
        val rvCategorias = findViewById<RecyclerView>(R.id.rv_categorias)
        adapterCategoria = CategoriaAdapter(MockDatabase.categorias)
        rvCategorias.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategorias.adapter = adapterCategoria



        //Configurando Vitrine Produtos usando MockDatabase
        val rvProdutos = findViewById<RecyclerView>(R.id.rv_produtos)

        // Agora usamos diretamente a lista de Produto do MockDatabase
        adapterProdutos = ProdutoItemAdapter(MockDatabase.produtos) { produtoClicado ->
            val fragment = TelaProduto()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvProdutos.layoutManager = GridLayoutManager(this, 2)
        rvProdutos.adapter = adapterProdutos
    }

    /**
     * Substitui o conteúdo do fragmentContainer pelo [PerfilFragment].
     * Permite que o usuário visualize e edite suas informações de perfil.
     */
    private fun openPerfilFragment() {
        val fragment = PerfilFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
