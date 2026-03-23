package com.mobile.agregarural

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobile.agregarural.databinding.ActivityRecuperarsenhaBinding
import androidx.appcompat.app.AlertDialog

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
            val view = layoutInflater.inflate(R.layout.popup_recuperar_senha, null)

            val popup = AlertDialog.Builder(this)
                .setView(view)
                .create()


            popup.show()
            popup.window?.setDimAmount(0.7f)

            val btFechar = view.findViewById<Button>(R.id.btFechar)

            btFechar.setOnClickListener {
                popup.dismiss()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}