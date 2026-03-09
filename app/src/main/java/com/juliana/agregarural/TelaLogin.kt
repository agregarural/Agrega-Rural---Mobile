package com.juliana.agregarural

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.juliana.agregarural.databinding.ActivityTelaLoginBinding

class TelaLogin : AppCompatActivity() {
    private lateinit var binding: ActivityTelaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTelaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btVoltar.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
    }
}