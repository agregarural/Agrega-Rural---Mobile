package com.mobile.agregarural

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityMainBinding
import android.content.Intent
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.btEntrar.setOnClickListener {
            navController.navigate(R.id.telaLoginFragment)
        }

        binding.btCadastro.setOnClickListener {
            navController.navigate(R.id.telaCadastro1Fragment)
        }
    }
}