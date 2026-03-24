package com.mobile.agregarural

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.agregarural.databinding.FragmentTelaFinalizacaoPedidoBinding
import com.mobile.agregarural.databinding.FragmentTelaPagamentoBinding


data class ProdutoPedido(
    val nome: String,
    val precoUnitario: Double,
    val quantidade: Int
)

class TelaFinalizaoPedido: Fragment() {

    private  var _binding: FragmentTelaFinalizacaoPedidoBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTelaFinalizacaoPedidoBinding.inflate(layoutInflater)


        // Simulando JSON para exibir carrinho

        val jsonCarrinho =  """
            [
                {"nome": "Ração Zardo", "precoUnitario": 500.00, "quantidade": 3}
            ]
        """.trimIndent()

        val gson = Gson()
        val tipoItem = object: TypeToken<List<ProdutoPedido>>() {}.type
        val listaItens: List<ProdutoPedido> = gson.fromJson(jsonCarrinho, tipoItem)

        val resumo = StringBuilder()
        var precoFinal = 0.0

        listaItens.forEach { produto ->
            val precoItemTotal = produto.precoUnitario * produto.quantidade
            precoFinal+= precoItemTotal

            resumo.append("Produto: ${produto.nome}\n")
            resumo.append("Preço: R$${produto.precoUnitario}\n")
            resumo.append("Quantidade: ${produto.quantidade}\n")
            resumo.append("Subtotal: R$${precoItemTotal}\n")
            resumo.append("______________________________________\n\n")
        }

        binding.pedidos.text = resumo.toString()
        binding.totalPedidos.text = ("TOTAL R$${precoFinal}")

        //Mudando de pagina

        binding.btnConfirmar.setOnClickListener {
            val fragmentEndereçoEntrega = TelaPagamentoEndereco()

            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer,fragmentEndereçoEntrega)
                .addToBackStack(null).commit()


            
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
