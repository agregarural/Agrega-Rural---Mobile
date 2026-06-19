package com.mobile.agregarural.ui.home

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
import com.mobile.agregarural.data.model.Categoria
import com.mobile.agregarural.ui.product.CategoriaMenuAdapter
import com.mobile.agregarural.R
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
                Toast.makeText(requireContext(), getString(R.string.erro_ao_buscar_cooperativa), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), getString(R.string.erro_ao_carregar_categorias), Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupMenuClickListeners() {
        binding.cardDesconectar.setOnClickListener {
            showDesconectarDialog()
        }

        binding.cardSAC.setOnClickListener {
            Toast.makeText(requireContext(),
                getString(R.string.atendimento_ao_cliente_sac_menu), Toast.LENGTH_SHORT).show()
        }

        binding.cardPromocao.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.promocoes), Toast.LENGTH_SHORT).show()
        }

        binding.cardMaisVendidos.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.mais_vendidos), Toast.LENGTH_SHORT).show()
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
        val bundle = Bundle().apply {
            putString("CATEGORIA", categoria)
        }

        findNavController().navigate(
            R.id.categoriaProdutosFragment,
            bundle
        )
    }

    private fun showDesconectarDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.desconectar_menu))
            .setMessage(getString(R.string.deseja_realmente_sair_da_sua_conta))
            .setPositiveButton(getString(R.string.sim)) { _, _ ->
                efetuarLogout()
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    private fun efetuarLogout() {
        FirebaseAuth.getInstance().signOut()

        Toast.makeText(requireContext(),
            getString(R.string.desconectado_com_sucesso), Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_homeFragment_to_telaInicialFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}