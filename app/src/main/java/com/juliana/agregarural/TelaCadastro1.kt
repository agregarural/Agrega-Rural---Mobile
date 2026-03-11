package com.juliana.agregarural

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.juliana.agregarural.databinding.ActivityTelaCadastro1Binding

class TelaCadastro1 : AppCompatActivity() {

    private lateinit var binding: ActivityTelaCadastro1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTelaCadastro1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btContinuar.setOnClickListener {
            val intent = Intent(this, TelaCadastro2::class.java)
            startActivity(intent)
            finish()
        }
    }
}