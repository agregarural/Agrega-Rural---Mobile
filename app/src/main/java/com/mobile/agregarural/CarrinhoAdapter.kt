package com.mobile.agregarural

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.agregarural.databinding.ItemCarrinhoBinding

class CarrinhoAdapter(
    private val itens: List<ItemCarrinho>
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    inner class CarrinhoViewHolder(val binding: ItemCarrinhoBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCarrinhoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val item = itens[position]
        with(holder.binding) {
            txtNomeProduto.text = item.nome
            txtPrecoProduto.text = "R$ %.2f".format(item.preco)
            quantidadeProduto.text = item.quantidade.toString()
        }
    }

    override fun getItemCount() = itens.size
}
