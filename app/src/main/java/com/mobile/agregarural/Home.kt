package com.mobile.agregarural

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardProduto.setOnClickListener {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TelaFinalizaoPedido())
                .addToBackStack(null)
                .commit()
        }





    }
}