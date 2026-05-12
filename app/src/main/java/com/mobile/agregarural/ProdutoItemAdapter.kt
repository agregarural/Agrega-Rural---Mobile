package com.mobile.agregarural

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide


class ProdutoItemAdapter(
    private val list: List<Produto>,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoItemAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var nome: TextView = view.findViewById(R.id.nomeItem)
        var preco: TextView = view.findViewById(R.id.precoItem)
        var especificacao: TextView = view.findViewById(R.id.especificacaoItem)
        var imagem: ImageView = view.findViewById(R.id.imgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_produto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nome.text = item.nome
        holder.preco.text = "R$ %.2f".format(item.preco)
        holder.especificacao.text = item.categoria
        Glide.with(holder.itemView.context)
            .load(item.imagem)
            .into(holder.imagem)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = list.size
}
