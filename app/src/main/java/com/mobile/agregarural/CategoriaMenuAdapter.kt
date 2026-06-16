package com.mobile.agregarural

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobile.agregarural.databinding.ItemCategoriaMenuBinding

class CategoriaMenuAdapter(
    private val lista: List<Categoria>,
    private val onClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriaMenuAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: ItemCategoriaMenuBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriaMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = lista[position]

        holder.binding.textCategoriaMenu.text = categoria.categoria

        Glide.with(holder.itemView.context)
            .load(categoria.imagem)
            .placeholder(R.drawable.perfil)
            .error(R.drawable.perfil)
            .into(holder.binding.imgCategoriaMenu)

        holder.itemView.setOnClickListener {
            onClick(categoria)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}