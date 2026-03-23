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
    private lateinit var adapter: CategoriaAdapter

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardProduto.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TelaProduto())
                .addToBackStack(null)
                .commit()
        }

        //Configurando categorias

        val listaCategoria = listOf(

            Categoria("Pedro", R.drawable.logo),
            Categoria("Francisco", R.drawable.logo),
            Categoria("Juliana", R.drawable.logo),
            Categoria("Murilo", R.drawable.logo),
            Categoria("Eduardo", R.drawable.logo),
        )

        recyclerView = findViewById(R.id.rv_categorias)
        adapter = CategoriaAdapter(listaCategoria)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter


    }
}