package com.mobile.agregarural

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityMeusCartoesBinding
import android.content.Intent
import android.widget.Toast

class MeusCartoes : AppCompatActivity() {

    private lateinit var binding: ActivityMeusCartoesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeusCartoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCartoes()
        setupClickListeners()
    }

    // -------------------------------------------------------------------------
    // Dados dos cartões
    // -------------------------------------------------------------------------

    private fun setupCartoes() {
        // TODO: Substitua pelos dados reais vindos de ViewModel / API
        binding.tvCartao1Info.text = "Número: **** 4012, Nome:\nMURILO GOMES, Validade: 01/2,\nCVV: ***"
        binding.tvCartao2Info.text = "Número: **** 8907, Nome:\nMURILO G C, Validade: 10/12,\nCVV: ***"
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    private fun setupClickListeners() {
        // Voltar para MeuPerfilActivity
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Editar cartão 1
        binding.btnEditarCartao1.setOnClickListener {
            Toast.makeText(this, "Editar cartão 1", Toast.LENGTH_SHORT).show()
            // TODO: abrir tela ou dialog de edição do cartão 1
        }

        // Editar cartão 2
        binding.btnEditarCartao2.setOnClickListener {
            Toast.makeText(this, "Editar cartão 2", Toast.LENGTH_SHORT).show()
            // TODO: abrir tela ou dialog de edição do cartão 2
        }

        // Clique no card completo (opcional)
        binding.cardCartao1.setOnClickListener {
            Toast.makeText(this, "Cartão 1 selecionado", Toast.LENGTH_SHORT).show()
        }

        binding.cardCartao2.setOnClickListener {
            Toast.makeText(this, "Cartão 2 selecionado", Toast.LENGTH_SHORT).show()
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
