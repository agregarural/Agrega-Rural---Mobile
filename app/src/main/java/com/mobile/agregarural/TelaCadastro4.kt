package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityTelaCadastro4Binding

class TelaCadastro4 : AppCompatActivity() {

    private lateinit var binding: ActivityTelaCadastro4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTelaCadastro4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVoltar.setOnClickListener {
            val intent = Intent(this, TelaCadastro3::class.java)
            startActivity(intent)
            finish()
        }

        binding.btCadastrar.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }
}