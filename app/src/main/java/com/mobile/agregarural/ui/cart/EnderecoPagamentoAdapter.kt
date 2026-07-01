package com.mobile.agregarural.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.agregarural.data.model.Endereco
import com.mobile.agregarural.databinding.ItemEnderecoPagamentoBinding

class EnderecoPagamentoAdapter(
    private val enderecos: List<Endereco>
) : RecyclerView.Adapter<EnderecoPagamentoAdapter.ViewHolder>() {

    var enderecoSelecionado: Endereco? = null
        private set

    private var selecionado = -1

    inner class ViewHolder(val binding: ItemEnderecoPagamentoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEnderecoPagamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = enderecos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val endereco = enderecos[position]

        holder.binding.tvNomeEndereco.text = endereco.nome
        holder.binding.tvDadosEndereco.text = endereco.formatado()
        holder.binding.radioEndereco.isChecked = position == selecionado

        holder.binding.radioEndereco.setOnClickListener {
            val pos = holder.adapterPosition

            if (pos != RecyclerView.NO_POSITION) {
                selecionado = pos
                enderecoSelecionado = enderecos[pos]
                notifyDataSetChanged()
            }
        }
    }
}