package com.mobile.agregarural

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PedidoAdapter(private val lista: List<Pedido>) :
    RecyclerView.Adapter<PedidoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val header: LinearLayout = view.findViewById(R.id.statusHeader)
        val statusTxt: TextView = view.findViewById(R.id.txtStatusName)
        val idPedido: TextView = view.findViewById(R.id.txtPedidoId)
        val btnAcao: Button = view.findViewById(R.id.btnAcao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.idPedido.text = "Pedido ${item.id}"

        when (item.status) {
            PedidoStatus.ENTREGUE -> {
                holder.header.setBackgroundColor(Color.parseColor("#2E7D32"))
                holder.statusTxt.text = "ENTREGUE"
                holder.btnAcao.visibility = View.GONE
            }

            PedidoStatus.EM_ANDAMENTO -> {
                holder.header.setBackgroundColor(Color.parseColor("#FBC02D"))
                holder.statusTxt.text = "Em andamento"
                holder.btnAcao.text = "ACOMPANHAR"
                holder.btnAcao.visibility = View.VISIBLE
            }

            PedidoStatus.CANCELADO -> {
                holder.header.setBackgroundColor(Color.parseColor("#D32F2F"))
                holder.statusTxt.text = "Cancelado"
                holder.btnAcao.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = lista.size
}