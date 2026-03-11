package com.juliana.agregarural

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.juliana.agregarural.databinding.ActivityTelaCadastro2Binding

class TelaCadastro2 : AppCompatActivity() {

    private lateinit var binding: ActivityTelaCadastro2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTelaCadastro2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVoltar.setOnClickListener {
            val intent = Intent(this, TelaCadastro1::class.java)
            startActivity(intent)
            finish()
        }

        binding.btContinuar2.setOnClickListener {
            val intent = Intent(this, TelaCadastro3::class.java)
            startActivity(intent)
            finish()
        }
    }
}