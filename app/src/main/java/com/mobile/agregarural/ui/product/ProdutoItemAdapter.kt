package com.mobile.agregarural.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.agregarural.data.manager.PrecoUsuarioManager
import com.mobile.agregarural.data.model.Produto
import com.mobile.agregarural.R

class ProdutoItemAdapter(
    private val list: List<Produto>,
    private val usuarioEhCooperado: Boolean,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nome: TextView = view.findViewById(R.id.nomeItem)
        var preco: TextView = view.findViewById(R.id.precoItem)
        var precoNormal: TextView = view.findViewById(R.id.precoNormalItem)
        var desconto: TextView = view.findViewById(R.id.txtDescontoCooperadoItem)
        var especificacao: TextView = view.findViewById(R.id.especificacaoItem)
        var imagem: ImageView = view.findViewById(R.id.imgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_produto, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        val precoFinal = PrecoUsuarioManager.precoFinal(item, usuarioEhCooperado)

        holder.nome.text = item.nome
        holder.especificacao.text = item.categoria
        holder.preco.text = "R$ %.2f".format(precoFinal)

        if (usuarioEhCooperado && item.descontoCooperado > 0.0) {
            holder.precoNormal.visibility = View.VISIBLE
            holder.desconto.visibility = View.VISIBLE

            holder.precoNormal.text = "Cliente normal: R$ %.2f".format(item.preco)
            holder.desconto.text = "Desconto cooperado: %.0f%%".format(item.descontoCooperado)
        } else {
            holder.precoNormal.visibility = View.GONE
            holder.desconto.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(item.imagem)
            .into(holder.imagem)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = list.size
}