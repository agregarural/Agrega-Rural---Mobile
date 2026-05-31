package com.mobile.agregarural

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.agregarural.databinding.ItemEnderecoBinding

class EnderecoAdapter(
    private val listaEnderecos: MutableList<Endereco>,
    private val onEnderecoClick: (Endereco) -> Unit,
    private val onEditarClick: (Endereco) -> Unit
) : RecyclerView.Adapter<EnderecoAdapter.EnderecoViewHolder>() {

    inner class EnderecoViewHolder(
        private val binding: ItemEnderecoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(endereco: Endereco) {
            binding.tvEndereco.text = endereco.formatado()

            binding.cardEndereco.setOnClickListener {
                onEnderecoClick(endereco)
            }

            binding.btnEditarEndereco.setOnClickListener {
                onEditarClick(endereco)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnderecoViewHolder {
        val binding = ItemEnderecoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return EnderecoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EnderecoViewHolder, position: Int) {
        holder.bind(listaEnderecos[position])
    }

    override fun getItemCount(): Int {
        return listaEnderecos.size
    }

    fun atualizarLista(novaLista: List<Endereco>) {
        listaEnderecos.clear()
        listaEnderecos.addAll(novaLista)
        notifyDataSetChanged()
    }
}