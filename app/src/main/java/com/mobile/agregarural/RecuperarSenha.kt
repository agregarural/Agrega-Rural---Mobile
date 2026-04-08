package com.mobile.agregarural

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mobile.agregarural.databinding.FragmentRecuperarsenhaBinding
import androidx.navigation.fragment.findNavController

class RecuperarSenhaFragment : Fragment() {

    private var _binding: FragmentRecuperarsenhaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecuperarsenhaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.btVoltar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btEnviar.setOnClickListener {

            val view = layoutInflater.inflate(R.layout.popup_recuperar_senha, null)

            val popup = AlertDialog.Builder(requireContext())
                .setView(view)
                .create()

            popup.show()
            popup.window?.setDimAmount(0.7f)

            val btFechar = view.findViewById<Button>(R.id.btFechar)

            btFechar.setOnClickListener {
                popup.dismiss()

                findNavController().navigate(R.id.telaLoginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}