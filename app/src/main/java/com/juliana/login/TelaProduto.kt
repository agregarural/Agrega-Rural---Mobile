package com.juliana.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juliana.login.databinding.FragmentTelaProdutoBinding


class TelaProduto : Fragment() {

    private lateinit var FragmentTelaProdutoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tela_produto, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}