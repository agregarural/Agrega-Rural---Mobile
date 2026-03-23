package com.mobile.agregarural


import android.os.Parcelable
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView


data class ProdutoItem(

    var nome: String,
    var preco: Double,
    var especificacao: String,
    var imagemResId: Int
)


class ProdutoItemAdapter(private val list: List<ProdutoItem>) : RecyclerView.Adapter<ProdutoItemAdapter.ViewHolder>()

{

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
        holder.preco.text = item.preco.toString()
    }

    override fun getItemCount() = list.size






}