package com.mobile.agregarural

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Toast
import com.mobile.agregarural.databinding.ActivityMenuBinding

class Menu : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryClickListeners()
        setupMenuClickListeners()
        setupBottomNavigation()
    }


    private fun setupCategoryClickListeners() {
        binding.cardPromocao.setOnClickListener {
            navigateToCategory("Promoção")
        }
        binding.cardMaisVendidos.setOnClickListener {
            navigateToCategory("Mais Vendidos")
        }
        binding.cardBovinos.setOnClickListener {
            navigateToCategory("Bovinos")
        }
        binding.cardSuinos.setOnClickListener {
            navigateToCategory("Suínos")
        }
        binding.cardAves.setOnClickListener {
            navigateToCategory("Aves")
        }
        binding.cardEquinos.setOnClickListener {
            navigateToCategory("Equinos")
        }
        binding.cardCaprinos.setOnClickListener {
            navigateToCategory("Caprinos")
        }
        binding.cardSementes.setOnClickListener {
            navigateToCategory("Sementes")
        }
        binding.cardDefensivos.setOnClickListener {
            navigateToCategory("Defensivos")
        }
        binding.cardFertilizantes.setOnClickListener {
            navigateToCategory("Fertilizantes")
        }
        binding.cardSacarias.setOnClickListener {
            navigateToCategory("Sacarias")
        }
        binding.cardCafeicultura.setOnClickListener {
            navigateToCategory("Cafeicultura")
        }
    }

    private fun setupMenuClickListeners() {
        binding.cardTrocarConta.setOnClickListener {
            Toast.makeText(this, "Trocar de conta", Toast.LENGTH_SHORT).show()
        }

        binding.cardDesconectar.setOnClickListener {
            showDesconectarDialog()
        }

        binding.cardSAC.setOnClickListener {
            Toast.makeText(this, "Atendimento ao cliente (SAC)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.navInicio.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        binding.navCarrinho.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        binding.navEntrega.setOnClickListener {
            startActivity(Intent(this, MeusPedidosActivity::class.java))
        }

        binding.navMenu.setOnClickListener {
            startActivity(Intent(this, Menu::class.java))

        }
    }

    private fun navigateToCategory(categoria: String) {
        val intent = Intent(this, TelaProduto::class.java)
        intent.putExtra("CATEGORIA", categoria)
        startActivity(intent)
        finish()
    }

    private fun showDesconectarDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Desconectar")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sim") { _, _ ->
                Toast.makeText(this, "Desconectado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}