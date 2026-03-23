package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.agregarural.databinding.ActivityPerfilBinding

class Perfil : AppCompatActivity() {


    class MainActivity : AppCompatActivity() {

        // ViewBinding — gerado automaticamente a partir de activity_meu_perfil.xml
        private lateinit var binding: ActivityPerfilBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()

            // Inflar o layout via binding
            binding = ActivityPerfilBinding.inflate(layoutInflater)
            setContentView(binding.root)

            setupUserData()
            setupClickListeners()
        }

        /**
         * Preenche os dados do usuário na tela.
         * Substitua pelos dados reais vindos de ViewModel / SharedPreferences / API.
         */
        private fun setupUserData() {
            binding.tvNomeUsuario.text = "Murilo Gomes Carvalho Góes"

            // Para carregar imagem de URL, use Glide ou Picasso:
            // Glide.with(this).load(usuario.fotoUrl).into(binding.imgPerfil)
        }

        private fun setupClickListeners() {
            setupHeader()
            setupActionCards()
            setupMenuItems()
            setupBottomNavigation()
        }

        // -------------------------------------------------------------------------
        // Header
        // -------------------------------------------------------------------------

        private fun setupHeader() {
            binding.btnVoltar.setOnClickListener {
                finish() // Volta para a tela anterior
            }

            binding.btnEditarFoto.setOnClickListener {
                abrirSeletorDeFoto()
            }
        }

        // -------------------------------------------------------------------------
        // Cards de ação (Meus Pedidos / Minha Cooperativa)
        // -------------------------------------------------------------------------

        private fun setupActionCards() {
            binding.cardMeusPedidos.setOnClickListener {
                // TODO: navegar para PedidosActivity
                // startActivity(Intent(this, PedidosActivity::class.java))
                Toast.makeText(this, "Meus Pedidos", Toast.LENGTH_SHORT).show()
            }

            binding.cardMinhaCooperativa.setOnClickListener {
                // TODO: navegar para CooperativaActivity
                // startActivity(Intent(this, CooperativaActivity::class.java))
                Toast.makeText(this, "Minha Cooperativa", Toast.LENGTH_SHORT).show()
            }
        }

        // -------------------------------------------------------------------------
        // Itens de menu
        // -------------------------------------------------------------------------

        private fun setupMenuItems() {
            binding.cardDadosPessoais.setOnClickListener {
                startActivity(Intent(this, DadosPessoais::class.java))
                finish()
            }

            binding.cardEnderecos.setOnClickListener {
                startActivity(Intent(this, MeusEnderecos::class.java))
                finish()
            }

            binding.cardCartoes.setOnClickListener {
                startActivity(Intent(this, MeusCartoes::class.java))
                finish()
            }

            binding.cardSAC.setOnClickListener {
                // TODO: navegar para SACActivity ou abrir WhatsApp / telefone
                // startActivity(Intent(this, SacActivity::class.java))
                Toast.makeText(this, "Atendimento ao cliente (SAC)", Toast.LENGTH_SHORT).show()
            }
        }

        // -------------------------------------------------------------------------
        // Bottom Navigation
        // -------------------------------------------------------------------------

        private fun setupBottomNavigation() {
            binding.navInicio.setOnClickListener {
                // TODO: startActivity(Intent(this, InicioActivity::class.java))
                startActivity(Intent(this, MainActivity::class.java))
                finish()
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
                // TODO: startActivity(Intent(this, CategoriasActivity::class.java))
                startActivity(Intent(this, Menu::class.java))
                finish()
            }
        }

        // -------------------------------------------------------------------------
        // Foto de perfil
        // -------------------------------------------------------------------------

        private fun abrirSeletorDeFoto() {
            // TODO: abrir galeria ou câmera para atualizar foto
            // Exemplo com ActivityResultLauncher:
            //
            // val selecionarImagem = registerForActivityResult(
            //     ActivityResultContracts.GetContent()
            // ) { uri ->
            //     uri?.let { binding.imgPerfil.setImageURI(it) }
            // }
            // selecionarImagem.launch("image/*")

            Toast.makeText(this, "Alterar foto de perfil", Toast.LENGTH_SHORT).show()
        }

        // -------------------------------------------------------------------------
        // Liberar binding ao destruir a Activity
        // -------------------------------------------------------------------------

        override fun onDestroy() {
            super.onDestroy()
            // Não é necessário nullar binding em Activity (apenas em Fragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

    }
}