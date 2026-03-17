package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.agregarural.databinding.ActivityRecuperarsenhaBinding
import com.mobile.agregarural.databinding.ActivityTelaCadastro1Binding

class RecuperarSenha : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperarsenhaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecuperarsenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btEnviar.setOnClickListener {
            val intent = Intent(this, TelaCadastro2::class.java)
            startActivity(intent)
            finish()
        }
    }
}