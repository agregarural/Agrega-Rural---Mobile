package com.mobile.agregarural.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.agregarural.data.manager.PrecoUsuarioManager
import com.mobile.agregarural.data.model.Produto
import com.mobile.agregarural.R

class ProdutoAdapter(
    private val lista: List<Produto>,
    private val usuarioEhCooperado: Boolean = false
) : RecyclerView.Adapter<ProdutoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.txtNomeProduto)
        val preco: TextView = view.findViewById(R.id.txtPrecoProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto_horizontal, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = lista[position]
        val precoFinal = PrecoUsuarioManager.precoFinal(p, usuarioEhCooperado)

        holder.nome.text = p.nome
        holder.preco.text = "R$ %.2f".format(precoFinal)
    }

    override fun getItemCount() = lista.size
}