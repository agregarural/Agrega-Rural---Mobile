package com.mobile.agregarural

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
            val total = produto.preco * item.quantidade

            holder.binding.quantidadeProduto.text = item.quantidade.toString()
            holder.binding.txtPrecoProduto.text = "Unidade: R$ %.2f".format(produto.preco)
            holder.binding.txtTotalProduto.text = "Total: R$ %.2f".format(total)
        }

        with(holder.binding) {
            txtNomeProduto.text = produto.nome

            checkboxCarrinho.setOnCheckedChangeListener(null)
            checkboxCarrinho.isChecked = item.selecionado

            Glide.with(holder.itemView.context)
                .load(produto.imagem)
                .into(imgProdutoCarrinho)

            atualizarValores()

            checkboxCarrinho.setOnCheckedChangeListener { _, isChecked ->
                item.selecionado = isChecked
            }

            btnMais.setOnClickListener {
                if (item.quantidade < produto.estoque) {
                    item.quantidade++
                    atualizarValores()
                    CarrinhoManager.atualizarQuantidade(item)
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
                    atualizarValores()
                    CarrinhoManager.atualizarQuantidade(item)
                }
            }

            btnExcluir.setOnClickListener {
                onExcluirClick(item)
            }
        }
    }

    override fun getItemCount(): Int = itens.size
}