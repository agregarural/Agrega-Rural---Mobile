package com.mobile.agregarural

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoriaMenuAdapter: CategoriaMenuAdapter
    private val listaCategoriasMenu = mutableListOf<Categoria>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNavigation()
        setupMenuClickListeners()
        configurarRecyclerCategoriasMenu()
        carregarCategoriasFirebase()
    }

    private fun configurarRecyclerCategoriasMenu() {
        categoriaMenuAdapter = CategoriaMenuAdapter(listaCategoriasMenu) { categoria ->
            navigateToCategory(categoria.categoria)
        }

        binding.rvCategoriasMenu.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvCategoriasMenu.adapter = categoriaMenuAdapter
    }

    private fun carregarCategoriasFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("coopUid")
            .get()
            .addOnSuccessListener { snapshot ->
                val idCooperativa = snapshot.getValue(String::class.java)

                if (!idCooperativa.isNullOrEmpty()) {
                    buscarCategorias(idCooperativa)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao buscar cooperativa.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarCategorias(idCooperativa: String) {
        FirebaseDatabase.getInstance()
            .getReference("Cooperativas")
            .child(idCooperativa)
            .child("Categorias")
            .get()
            .addOnSuccessListener { snapshot ->
                listaCategoriasMenu.clear()

                for (categoriaSnapshot in snapshot.children) {
                    val categoria = categoriaSnapshot.getValue(Categoria::class.java)

                    if (categoria != null) {
                        listaCategoriasMenu.add(categoria)
                    }
                }

                categoriaMenuAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao carregar categorias.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupMenuClickListeners() {
        binding.cardDesconectar.setOnClickListener {
            showDesconectarDialog()
        }

        binding.cardSAC.setOnClickListener {
            Toast.makeText(requireContext(), "Atendimento ao cliente (SAC)", Toast.LENGTH_SHORT).show()
        }

        binding.cardPromocao.setOnClickListener {
            Toast.makeText(requireContext(), "Promoções", Toast.LENGTH_SHORT).show()
        }

        binding.cardMaisVendidos.setOnClickListener {
            Toast.makeText(requireContext(), "Mais vendidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {

        binding.cardatendimento.setOnClickListener {
            findNavController().navigate(R.id.TelaSacFragment)
        }

        binding.btnSair.setOnClickListener {
            showDesconectarDialog()
        }
    }

    private fun navigateToCategory(categoria: String) {
        val bundle = Bundle()
        bundle.putString("CATEGORIA", categoria)
        findNavController().navigate(R.id.telaProdutoFragment, bundle)
    }

    private fun showDesconectarDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Desconectar")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sim") { _, _ ->
                efetuarLogout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun efetuarLogout() {
        FirebaseAuth.getInstance().signOut()

        Toast.makeText(requireContext(), "Desconectado com sucesso!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_homeFragment_to_telaInicialFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}