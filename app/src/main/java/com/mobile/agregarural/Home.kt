package com.mobile.agregarural

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.agregarural.databinding.ActivityHomeBinding
import androidx.recyclerview.widget.RecyclerView


class Home : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterCategoria: CategoriaAdapter
    private lateinit var adapterProdutos: ProdutoItemAdapter

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configurando categorias

        val listaCategoria = listOf(
            Categoria("Pedro", R.drawable.logo),
            Categoria("Francisco", R.drawable.logo),
            Categoria("Juliana", R.drawable.logo),
            Categoria("Murilo", R.drawable.logo),
            Categoria("Eduardo", R.drawable.logo),
        )

        recyclerView = findViewById(R.id.rv_categorias)
        adapterCategoria = CategoriaAdapter(listaCategoria)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapterCategoria



        //Configurando Vitrine Produtos

        val listaProdutos = listOf(

            ProdutoItem("Rações Zardo", 500.0, "Saco 25KG", R.drawable.prod_racoeszardo),
            ProdutoItem("Rações Zardo", 500.0, "Saco 25KG", R.drawable.prod_racoeszardo),
            ProdutoItem("Rações Zardo", 500.0, "Saco 25KG", R.drawable.prod_racoeszardo)
        )

        recyclerView = findViewById(R.id.rv_produtos)
        adapterProdutos = ProdutoItemAdapter(listaProdutos) { produtoClicado ->

            val fragment = TelaProduto()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapterProdutos









    }
}