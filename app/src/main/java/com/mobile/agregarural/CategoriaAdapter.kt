package com.mobile.agregarural

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView

import android.media.Image

data class Categoria(

    val nome: String,
    val imagemResId: Int

)

class CategoriaAdapter(private val list: List<Categoria>) : RecyclerView.Adapter<CategoriaAdapter.ViewHolder>(){



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.textItem)
        val imagem: ImageView = view.findViewById(R.id.imgItem)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nome.text = item.nome
        holder.imagem.setImageResource(item.imagemResId)

    }

    override fun getItemCount() = list.size
}