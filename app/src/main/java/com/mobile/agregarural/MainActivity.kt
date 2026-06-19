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

        // 1. Usar setupWithNavController para gerenciamento básico (animações, ícones)
        bottomNav.setupWithNavController(navController)

        // 2. Interceptar a seleção de abas para sempre limpar a pilha até a raiz da aba
        bottomNav.setOnItemSelectedListener { item ->
            // Se a aba já está selecionada, apenas limpe a pilha (mesmo efeito do reselect)
            if (navController.currentDestination?.id == item.itemId) {
                navController.popBackStack(item.itemId, inclusive = false)
                return@setOnItemSelectedListener true
            }

            // Se for outra aba, navegue limpando a pilha da aba atual
            navController.navigate(
                item.itemId,
                null,
                NavOptions.Builder()
                    .setPopUpTo(item.itemId, inclusive = false) // limpa tudo acima da aba destino
                    .setLaunchSingleTop(true)
                    .build()
            )
            true
        }

        // 3. Reselecionar a aba: volta à raiz da aba (caso o listener acima não capture)
        bottomNav.setOnItemReselectedListener { item ->
            navController.popBackStack(item.itemId, inclusive = false)
        }

        // 4. Manter o ícone da aba pai selecionado em telas secundárias
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val abas = setOf(
                R.id.homeFragment,
                R.id.carrinhoFragment,
                R.id.meusPedidosFragment,
                R.id.menuFragment
            )
            if (destination.id in abas) {
                bottomNav.selectedItemId = destination.id
            }
            // Em telas filhas, não altere o ícone (mantém o último)
        }

        // 5. Controle de visibilidade do menu (antes/depois do login)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destinoDeveMostrarMenu(destination.id)) {
                bottomNav.visibility = View.VISIBLE
            } else {
                bottomNav.visibility = View.GONE
            }
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
}