package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobile.agregarural.databinding.ActivityTelaCadastro3Binding

class TelaCadastro3 : AppCompatActivity() {

    private lateinit var binding: ActivityTelaCadastro3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTelaCadastro3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVoltar.setOnClickListener {
            val intent = Intent(this, TelaCadastro2::class.java)
            startActivity(intent)
            finish()
        }

        binding.btContinuar.setOnClickListener {
            val intent = Intent(this, TelaCadastro4::class.java)
            startActivity(intent)
            finish()
        }
    }
}