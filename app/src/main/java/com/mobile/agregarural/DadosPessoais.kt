package com.mobile.agregarural

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mobile.agregarural.databinding.ActivityDadosPessoaisBinding

class DadosPessoais : AppCompatActivity() {

    private lateinit var binding: ActivityDadosPessoaisBinding

    // Dados do usuário (futuramente virão do banco/API)
    private var nome = "Murilo Gomes Carvalho Góes"
    private var cpf = "000.000.000-00"
    private var cooperativa = "PEDROCOOP"
    private var codigo = "00.000-0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDadosPessoaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        atualizarTextos()
        setupClickListeners()
    }

    // -------------------------------------------------------------------------
    // Dados do usuário
    // -------------------------------------------------------------------------

    private fun atualizarTextos() {
        // TODO: Substitua pelos dados reais vindos de ViewModel / API
        binding.tvNome.text        = "Nome: $nome"
        binding.tvCpf.text         = "CPF: $cpf"
        binding.tvCooperativa.text = "Cooperativa: $cooperativa"
        binding.tvCodigoMembro.text      = "Código de Membro: $codigo"

        // Para carregar foto via Glide:
        // Glide.with(this).load(usuario.fotoUrl).into(binding.imgPerfil)
    }

    // -------------------------------------------------------------------------
    // Listeners
    // -------------------------------------------------------------------------

    private fun setupClickListeners() {
        // Voltar para MeuPerfilFragment
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Alterar foto de perfil
        binding.cardAlterarFoto.setOnClickListener {
            abrirSeletorDeFoto()
        }

        // Editar campos
        binding.cardNome.setOnClickListener {
            mostrarDialogEdicao("Nome", nome) { novoValor ->
                nome = novoValor
                atualizarTextos()
            }
        }

        binding.cardCpf.setOnClickListener {
            mostrarDialogEdicao("CPF", cpf) { novoValor ->
                cpf = novoValor
                atualizarTextos()
            }
        }

        binding.cardCooperativa.setOnClickListener {
            mostrarDialogEdicao("Cooperativa", cooperativa) { novoValor ->
                cooperativa = novoValor
                atualizarTextos()
            }
        }

        binding.cardCodigoMembro.setOnClickListener {
            mostrarDialogEdicao("Código de Membro", codigo) { novoValor ->
                codigo = novoValor
                atualizarTextos()
            }
        }

        setupBottomNavigation()
    }

    // -------------------------------------------------------------------------
    // Foto de perfil
    // -------------------------------------------------------------------------

    private fun abrirSeletorDeFoto() {
        // TODO: abrir galeria com ActivityResultLauncher
        // val selecionarImagem = registerForActivityResult(
        //     ActivityResultContracts.GetContent()
        // ) { uri -> uri?.let { binding.imgPerfil.setImageURI(it) } }
        // selecionarImagem.launch("image/*")
        Toast.makeText(this, "Alterar foto de perfil", Toast.LENGTH_SHORT).show()
    }

    // -------------------------------------------------------------------------
    // Bottom Navigation
    // -------------------------------------------------------------------------

    private fun setupBottomNavigation() {
        binding.navInicio.setOnClickListener {
            startActivity(Intent(this, HomeFragment::class.java))
            finish()
        }
        binding.navCarrinho.setOnClickListener {
            startActivity(Intent(this, HomeFragment::class.java))
            finish()
        }
        binding.navEntrega.setOnClickListener {
            startActivity(Intent(this, MeusPedidosActivity::class.java))
            finish()
        }
        binding.navMenu.setOnClickListener {
            startActivity(Intent(this, Menu::class.java))
            finish()
        }
    }

    private fun mostrarDialogEdicao(campo: String, valorAtual: String, onSalvar: (String) -> Unit) {
        val input = EditText(this)
        input.setText(valorAtual)

        AlertDialog.Builder(this)
            .setTitle("Editar $campo")
            .setView(input)
            .setPositiveButton("Salvar") { _, _ ->
                val novoValor = input.text.toString().trim()
                if (novoValor.isNotEmpty()) {
                    onSalvar(novoValor)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
