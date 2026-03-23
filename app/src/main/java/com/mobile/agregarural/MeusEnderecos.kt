package com.mobile.agregarural

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Toast
import com.mobile.agregarural.databinding.ActivityMeusEnderecosBinding


class MeusEnderecos : AppCompatActivity() {

    private lateinit var binding: ActivityMeusEnderecosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMeusEnderecosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEnderecos()
        setupClickListeners()
    }

    // -------------------------------------------------------------------------
    // Dados dos endereços
    // -------------------------------------------------------------------------

    private fun setupEnderecos() {
        // TODO: Substitua pelos dados reais vindos de ViewModel / API
        binding.tvEndereco1.text = "Rua das Hortências, 228 – Centro\nMuniz Freire – ES, 29380-000"
        binding.tvEndereco2.text = "Avenida Rio Verde, 951 – Nova\nBrasilia Xique-Xique – BA,\n47400-000"
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    private fun setupClickListeners() {
        // Voltar para MeuPerfilActivity
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Editar endereço 1
        binding.btnEditarEndereco1.setOnClickListener {
            Toast.makeText(this, "Editar endereço 1", Toast.LENGTH_SHORT).show()
            // TODO: abrir tela ou dialog de edição do endereço 1
        }

        // Editar endereço 2
        binding.btnEditarEndereco2.setOnClickListener {
            Toast.makeText(this, "Editar endereço 2", Toast.LENGTH_SHORT).show()
            // TODO: abrir tela ou dialog de edição do endereço 2
        }

        // Clique no card completo (opcional)
        binding.cardEndereco1.setOnClickListener {
            Toast.makeText(this, "Endereço 1 selecionado", Toast.LENGTH_SHORT).show()
        }

        binding.cardEndereco2.setOnClickListener {
            Toast.makeText(this, "Endereço 2 selecionado", Toast.LENGTH_SHORT).show()
        }

        setupBottomNavigation()
    }

    // -------------------------------------------------------------------------
    // Bottom Navigation
    // -------------------------------------------------------------------------

    private fun setupBottomNavigation() {
        binding.navInicio.setOnClickListener {
            // TODO: startActivity(Intent(this, InicioActivity::class.java))
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            Toast.makeText(this, "Início", Toast.LENGTH_SHORT).show()
        }
        binding.navCarrinho.setOnClickListener {
            // TODO: startActivity(Intent(this, CarrinhoActivity::class.java))
            Toast.makeText(this, "Carrinho", Toast.LENGTH_SHORT).show()
        }
        binding.navEntrega.setOnClickListener {
            // TODO: startActivity(Intent(this, EntregaActivity::class.java))
            Toast.makeText(this, "Entrega", Toast.LENGTH_SHORT).show()
        }
        binding.navMenu.setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }
    }
}
