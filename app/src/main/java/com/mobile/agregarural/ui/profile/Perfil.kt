package com.mobile.agregarural.ui.profile


import com.google.firebase.auth.FirebaseAuth
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
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.R
import com.mobile.agregarural.databinding.FragmentPerfilBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val imgbbApiKey = "ac742aebcb5ef3bbef2489f934240205"

    private val database = FirebaseDatabase.getInstance()

    // Por enquanto, use um ID fixo para testar.
    // Depois o ideal é trocar pelo ID real do usuário logado.
    private val usuarioId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    private val selecionarImagem =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                enviarFotoPerfil(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carregarFotoPerfil()
        carregarNomeUsuario()

        binding.btnEditarFoto.setOnClickListener {
            selecionarImagem.launch("image/*")
        }

        binding.btnVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardDadosPessoais.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_dadosPessoaisFragment)
        }

        binding.cardEnderecos.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_meusEnderecosFragment)
        }

        binding.cardSAC.setOnClickListener {
            findNavController().navigate(R.id.TelaSacFragment)
        }

        binding.cardMeusPedidos.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_meusPedidosFragment)
        }

        binding.cardMinhaCooperativa.setOnClickListener {
            findNavController().navigate(R.id.MinhaCooperativaFragment)
        }
    }

    private fun carregarNomeUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)

        ref.child("nome").get()
            .addOnSuccessListener { snapshot ->
                val nome = snapshot.getValue(String::class.java)
                if (!nome.isNullOrEmpty()) {
                    binding.tvNomeUsuario.text = "$nome"
                }
            }

    }

    private fun enviarFotoPerfil(uri: Uri) {
        Toast.makeText(requireContext(), "Enviando foto...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                val urlImagem = withContext(Dispatchers.IO) {
                    enviarImagemParaImgBB(uri)
                }

                salvarUrlFotoNoFirebase(urlImagem)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Erro ao enviar foto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enviarImagemParaImgBB(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
            ?: throw Exception("Não foi possível abrir a imagem")

        val bytes = inputStream.readBytes()
        inputStream.close()

        val imagemBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("key", imgbbApiKey)
            .add("image", imagemBase64)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Erro na resposta do ImgBB")
        }

        val responseBody = response.body?.string()
            ?: throw Exception("Resposta vazia do ImgBB")

        val json = JSONObject(responseBody)

        val sucesso = json.getBoolean("success")

        if (!sucesso) {
            throw Exception("ImgBB não aceitou a imagem")
        }

        return json
            .getJSONObject("data")
            .getString("url")
    }

    private fun salvarUrlFotoNoFirebase(urlImagem: String) {
        val uid = usuarioId

        if (uid == null) {
            Toast.makeText(requireContext(), "Usuário não está logado", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("fotoPerfil")
            .setValue(urlImagem)
            .addOnSuccessListener {
                Glide.with(this)
                    .load(urlImagem)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .into(binding.imgPerfil)

                Toast.makeText(requireContext(), "Foto atualizada", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarFotoPerfil() {
        val uid = usuarioId

        if (uid == null) return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("fotoPerfil")
            .get()
            .addOnSuccessListener { snapshot ->
                val urlImagem = snapshot.getValue(String::class.java)

                if (!urlImagem.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(urlImagem)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .into(binding.imgPerfil)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}