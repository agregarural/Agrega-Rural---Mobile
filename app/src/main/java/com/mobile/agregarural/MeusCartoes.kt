package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityMeusCartoesBinding

class MeusCartoes : AppCompatActivity() {

    private lateinit var binding: ActivityMeusCartoesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialização do ViewBinding
        binding = ActivityMeusCartoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCartoes()
        setupClickListeners()
    }

    private fun setupCartoes() {
        // Dados estáticos para teste
        binding.tvCartao1Info.text = "Número: **** 4012, Nome:\nMURILO GOMES, Validade: 01/2,\nCVV: ***"
        binding.tvCartao2Info.text = "Número: **** 8907, Nome:\nMURILO G C, Validade: 10/12,\nCVV: ***"
    }

    private fun setupClickListeners() {
        // Voltar para a tela anterior (Perfil)
        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
            finish()
        }

        // Ações de Edição
        binding.btnEditarCartao1.setOnClickListener {
            Toast.makeText(this, "Editar cartão 1", Toast.LENGTH_SHORT).show()
        }

        binding.btnEditarCartao2.setOnClickListener {
            Toast.makeText(this, "Editar cartão 2", Toast.LENGTH_SHORT).show()
        }

        // Clique nos Cards
        binding.cardCartao1.setOnClickListener {
            Toast.makeText(this, "Cartão 1 selecionado", Toast.LENGTH_SHORT).show()
        }

        binding.cardCartao2.setOnClickListener {
            Toast.makeText(this, "Cartão 2 selecionado", Toast.LENGTH_SHORT).show()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.navInicio.setOnClickListener {
            // Certifique-se de que MainActivity existe
            val intent = Intent(this, Home::class.java)
            // Limpa o empilhamento para voltar ao início real
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        binding.navCarrinho.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        binding.navEntrega.setOnClickListener {
            val intent = Intent(this, MeusPedidosActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.navMenu.setOnClickListener {
            // Certifique-se de que a classe Menu existe
            startActivity(Intent(this, Menu::class.java))
            finish()
        }
    }
}