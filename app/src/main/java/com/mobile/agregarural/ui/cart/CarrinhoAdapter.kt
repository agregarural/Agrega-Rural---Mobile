package com.mobile.agregarural.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.agregarural.data.manager.CarrinhoManager
import com.mobile.agregarural.data.model.ItemCarrinhos
import com.mobile.agregarural.databinding.ItemCarrinhoBinding

class CarrinhoAdapter(
    private val itens: MutableList<ItemCarrinhos>,
    private val onExcluirClick: (ItemCarrinhos) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    inner class CarrinhoViewHolder(val binding: ItemCarrinhoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCarrinhoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val item = itens[position]
        val produto = item.produto

        fun atualizarValores() {
            val total = item.precoUnitario * item.quantidade

            holder.binding.quantidadeProduto.text = item.quantidade.toString()
            holder.binding.txtPrecoProduto.text = "Unidade: R$ %.2f".format(item.precoUnitario)
            holder.binding.txtTotalProduto.text = "Total: R$ %.2f".format(total)

            holder.binding.checkboxCarrinho.isChecked = item.selecionado
        }

        with(holder.binding) {
            txtNomeProduto.text = produto.nome

            Glide.with(holder.itemView.context)
                .load(produto.imagem)
                .into(imgProdutoCarrinho)

            atualizarValores()

            checkboxCarrinho.setOnCheckedChangeListener(null)
            checkboxCarrinho.isChecked = item.selecionado

            checkboxCarrinho.setOnCheckedChangeListener { _, isChecked ->
                item.selecionado = isChecked
            }

            btnMais.setOnClickListener {
                if (item.quantidade < produto.estoque) {
                    item.quantidade++
                    CarrinhoManager.atualizarQuantidade(item)
                    atualizarValores()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Quantidade máxima em estoque: ${produto.estoque}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnMenos.setOnClickListener {
                if (item.quantidade > 1) {
                    item.quantidade--
                    CarrinhoManager.atualizarQuantidade(item)
                    atualizarValores()
                }
            }

            btnExcluir.setOnClickListener {
                onExcluirClick(item)
            }
        }
    }

    override fun getItemCount() = itens.size
}