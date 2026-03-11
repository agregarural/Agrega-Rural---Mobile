package com.juliana.agregarural

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.juliana.agregarural.databinding.ActivityMainBinding
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btEntrar.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, TelaLogin::class.java)
            startActivity(intent)
            finish()
        })
        binding.btCadastro.setOnClickListener {
            val intent = Intent(this, TelaCadastro1::class.java)
            startActivity(intent)
            finish()
        }

    }
}