package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityPerfilBinding

class Perfil : AppCompatActivity() {

    // ViewBinding configurado corretamente na classe principal
    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflar o layout
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUserData()
        setupClickListeners()
    }

    private fun setupUserData() {
        binding.tvNomeUsuario.text = "Murilo Gomes Carvalho Góes"
    }

    private fun setupClickListeners() {
        // Botão Voltar
        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        // Editar Foto
        binding.btnEditarFoto.setOnClickListener {
            Toast.makeText(this, "Alterar foto de perfil", Toast.LENGTH_SHORT).show()
        }

        // Cards de ação
        binding.cardMeusPedidos.setOnClickListener {
            val intent = Intent(this, MeusPedidosActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.cardMinhaCooperativa.setOnClickListener {
            Toast.makeText(this, "Minha Cooperativa", Toast.LENGTH_SHORT).show()
        }

        // --- ITENS DE MENU (Onde estava o erro) ---
        binding.cardDadosPessoais.setOnClickListener {
            startActivity(Intent(this, DadosPessoais::class.java))
        }

        binding.cardEnderecos.setOnClickListener {
            val intent = Intent(this, MeusEnderecos::class.java)
            startActivity(intent)
            // Removi o finish() para que o usuário possa voltar ao perfil depois
        }

        binding.cardCartoes.setOnClickListener {
            startActivity(Intent(this, MeusCartoes::class.java))
        }

        // Bottom Navigation
        binding.navInicio.setOnClickListener {
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
            startActivity(Intent(this, Menu::class.java))
        }
    }
}