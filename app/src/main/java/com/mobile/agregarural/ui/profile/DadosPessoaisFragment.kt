package com.mobile.agregarural.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentDadosPessoaisBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DadosPessoaisFragment : Fragment() {

    private var _binding: FragmentDadosPessoaisBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val usuarioId: String?
        get() = auth.currentUser?.uid

    private val imgbbApiKey = "ac742aebcb5ef3bbef2489f934240205"

    private val selecionarFoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                enviarFotoParaImgBB(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDadosPessoaisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carregarDadosUsuario()
        setupClickListeners()
    }

    private fun carregarDadosUsuario() {
        val uid = usuarioId

        if (uid == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("Usuarios")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val nome = snapshot.child("nome").getValue(String::class.java) ?: ""
                val cpf = snapshot.child("cpf").getValue(String::class.java) ?: ""
                val matricula = snapshot.child("matricula").getValue(String::class.java) ?: ""
                val fotoPerfil = snapshot.child("fotoPerfil").getValue(String::class.java) ?: ""
                val coopUid = snapshot.child("coopUid").getValue(String::class.java) ?: ""

                binding.tvNome.text = nome.ifBlank { "Não informado" }
                binding.tvCpf.text = cpf.ifBlank { "Não informado" }

                binding.tvCodigoMembro.text = if (matricula.isBlank()) {
                    "Não possui matrícula"
                } else {
                    matricula
                }

                if (fotoPerfil.isNotBlank()) {
                    Glide.with(this)
                        .load(fotoPerfil)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .circleCrop()
                        .into(binding.imgPerfil)
                } else {
                    binding.imgPerfil.setImageResource(R.drawable.ic_avatar_placeholder)
                }

                if (coopUid.isNotBlank()) {
                    carregarNomeCooperativa(coopUid)
                } else {
                    binding.tvCooperativa.text = "Não vinculada"
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar dados do usuário",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun carregarNomeCooperativa(coopUid: String) {
        database.child("Cooperativas")
            .child(coopUid)
            .child("nome")
            .get()
            .addOnSuccessListener { snapshot ->
                val nomeCooperativa = snapshot.getValue(String::class.java)

                binding.tvCooperativa.text = if (!nomeCooperativa.isNullOrBlank()) {
                    nomeCooperativa
                } else {
                    "Cooperativa sem nome"
                }
            }
            .addOnFailureListener {
                binding.tvCooperativa.text = "Erro ao carregar"
            }
    }

    private fun setupClickListeners() {
        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardAlterarFoto.setOnClickListener {
            abrirSeletorDeFoto()
        }
    }

    private fun abrirSeletorDeFoto() {
        selecionarFoto.launch("image/*")
    }

    private fun enviarFotoParaImgBB(uri: Uri) {
        val uid = usuarioId

        if (uid == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(
            requireContext(),
            "Enviando foto...",
            Toast.LENGTH_SHORT
        ).show()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val urlFoto = withContext(Dispatchers.IO) {
                    uploadImagemParaImgBB(uri)
                }

                salvarFotoNoFirebase(uid, urlFoto)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Erro ao enviar foto: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadImagemParaImgBB(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
            ?: throw Exception("Não foi possível abrir a imagem")

        val bytes = inputStream.use { it.readBytes() }
        val imagemBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

        val parametros =
            "key=${URLEncoder.encode(imgbbApiKey, "UTF-8")}" +
                    "&image=${URLEncoder.encode(imagemBase64, "UTF-8")}"

        val url = URL("https://api.imgbb.com/1/upload")
        val conexao = url.openConnection() as HttpURLConnection

        conexao.requestMethod = "POST"
        conexao.doOutput = true
        conexao.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        conexao.outputStream.use { output ->
            output.write(parametros.toByteArray(Charsets.UTF_8))
        }

        val resposta = if (conexao.responseCode in 200..299) {
            conexao.inputStream.bufferedReader().readText()
        } else {
            conexao.errorStream?.bufferedReader()?.readText()
                ?: throw Exception("Erro HTTP ${conexao.responseCode}")
        }

        val json = JSONObject(resposta)

        val sucesso = json.optBoolean("success", false)

        if (!sucesso) {
            throw Exception("ImgBB recusou o upload")
        }

        return json
            .getJSONObject("data")
            .getString("url")
    }

    private fun salvarFotoNoFirebase(uid: String, urlFoto: String) {
        database.child("Usuarios")
            .child(uid)
            .child("fotoPerfil")
            .setValue(urlFoto)
            .addOnSuccessListener {
                if (_binding == null) return@addOnSuccessListener

                Glide.with(this)
                    .load(urlFoto)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_avatar_placeholder)
                    .circleCrop()
                    .into(binding.imgPerfil)

                Toast.makeText(
                    requireContext(),
                    "Foto atualizada com sucesso",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao salvar foto",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}