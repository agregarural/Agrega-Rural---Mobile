package com.mobile.agregarural

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobile.agregarural.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)




        // 4. Manter o ícone da aba pai selecionado em telas secundárias
        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destinoDeveMostrarMenu(destination.id)) {
                bottomNav.visibility = View.VISIBLE
            } else {
                bottomNav.visibility = View.GONE
            }

            val parentTab = getParentTab(destination.id)

            if (parentTab != null) {

                if (bottomNav.selectedItemId != parentTab) {

                    bottomNav.menu.findItem(parentTab).isChecked = true
                }
            }
        }

        bottomNav.setOnItemSelectedListener { item ->

            val destinoAtual = navController.currentDestination?.id

            if (destinoAtual == item.itemId) {
                return@setOnItemSelectedListener true
            }

            navController.navigate(
                item.itemId,
                null,
                NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )

            true
        }

        bottomNav.setOnItemReselectedListener { item ->

            navController.popBackStack(
                item.itemId,
                false
            )
        }
    }

    private fun destinoDeveMostrarMenu(destinoId: Int): Boolean {
        val telasSemMenu = setOf(
            R.id.telaInicialFragment,
            R.id.telaLoginFragment,
            R.id.recuperarSenhaFragment,
            R.id.telaCadastro1Fragment,
            R.id.telaCadastro2Fragment,
            R.id.telaCadastro3Fragment,
            R.id.telaCadastro4Fragment
        )
        return destinoId !in telasSemMenu
    }

    private fun getParentTab(destinationId: Int): Int? {

        return when(destinationId) {

            // HOME

            R.id.homeFragment,
            R.id.telaProdutoFragment,
            R.id.telaFinalizacaoPedidoFragment,
            R.id.telaPagamentoEnderecoFragment,
            R.id.telaPagamentoFragment ->

                R.id.homeFragment

            // CARRINHO

            R.id.carrinhoFragment ->

                R.id.carrinhoFragment

            // PEDIDOS

            R.id.meusPedidosFragment ->

                R.id.meusPedidosFragment

            // MENU

            R.id.menuFragment,
            R.id.perfilFragment,
            R.id.meusEnderecosFragment,
            R.id.dadosPessoaisFragment ->

                R.id.menuFragment

            else -> null
        }
    }
}