package com.mobile.agregarural.ui.profile

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.data.model.PedidoUsuario
import com.mobile.agregarural.R

class PedidoAdapter(
    private val pedidos: MutableList<PedidoUsuario>,
    private val onPagarClick: (PedidoUsuario) -> Unit,
    private val onPedidoAlterado: () -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusHeader: LinearLayout = view.findViewById(R.id.statusHeader)
        val txtStatusName: TextView = view.findViewById(R.id.txtStatusName)
        val txtPedidoId: TextView = view.findViewById(R.id.txtPedidoId)
        val imgPedidoProduto: ImageView = view.findViewById(R.id.imgPedidoProduto)
        val txtPedidoDetalhes: TextView = view.findViewById(R.id.txtPedidoDetalhes)
        val btnAcao: Button = view.findViewById(R.id.btnAcao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)

        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        val primeiroItem = pedido.itens.firstOrNull()

        holder.txtPedidoId.text = "Pedido #${pedido.pedidoId.takeLast(6)}"

        holder.txtPedidoDetalhes.text = if (primeiroItem != null) {
            "${primeiroItem.nome}\nQuantidade: ${primeiroItem.quantidade}\nTotal: R$ %.2f".format(
                pedido.valorTotal
            )
        } else {
            "Detalhes do pedido\nTotal: R$ %.2f".format(pedido.valorTotal)
        }

        if (primeiroItem != null && primeiroItem.imagem.isNotBlank()) {
            Glide.with(holder.itemView.context)
                .load(primeiroItem.imagem)
                .placeholder(R.drawable.porco)
                .into(holder.imgPedidoProduto)
        } else {
            holder.imgPedidoProduto.setImageResource(R.drawable.porco)
        }

        when (pedido.status.lowercase()) {

            "concluido" -> {
                holder.statusHeader.setBackgroundColor(Color.parseColor("#2E7D32"))
                holder.txtStatusName.text = "Entregue"

                holder.btnAcao.visibility = View.VISIBLE
                holder.btnAcao.text = "EXCLUIR"
                holder.btnAcao.setBackgroundColor(Color.parseColor("#2E7D32"))

                holder.btnAcao.setOnClickListener {
                    excluirPedidoSomenteDaTelaDoUsuario(pedido)
                }
            }

            "em andamento" -> {
                holder.statusHeader.setBackgroundColor(Color.parseColor("#FBC02D"))
                holder.txtStatusName.text = "Em andamento"

                holder.btnAcao.visibility = View.VISIBLE
                holder.btnAcao.text = "CONFIRMAR"
                holder.btnAcao.setBackgroundColor(Color.parseColor("#2E7D32"))

                holder.btnAcao.setOnClickListener {
                    confirmarEntrega(pedido)
                }
            }

            "pendente" -> {
                holder.statusHeader.setBackgroundColor(Color.parseColor("#D32F2F"))
                holder.txtStatusName.text = "Pagamento pendente"

                holder.btnAcao.visibility = View.VISIBLE
                holder.btnAcao.text = "PAGAR"
                holder.btnAcao.setBackgroundColor(Color.parseColor("#2E7D32"))

                holder.btnAcao.setOnClickListener {
                    onPagarClick(pedido)
                }
            }

            else -> {
                holder.statusHeader.setBackgroundColor(Color.parseColor("#757575"))
                holder.txtStatusName.text = pedido.status
                holder.btnAcao.visibility = View.GONE
            }
        }
    }

    private fun confirmarEntrega(pedido: PedidoUsuario) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val pedidoId = pedido.pedidoId

        val updates = hashMapOf<String, Any>(
            "/Pedidos/$pedidoId/status" to "concluido",
            "/Usuarios/$uid/Pedidos/$pedidoId/status" to "concluido"
        )

        FirebaseDatabase.getInstance()
            .reference
            .updateChildren(updates)
            .addOnSuccessListener {
                onPedidoAlterado()
            }
    }

    private fun excluirPedidoSomenteDaTelaDoUsuario(pedido: PedidoUsuario) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val pedidoId = pedido.pedidoId

        /*
         IMPORTANTE:
         Aqui NÃO apagamos:
         /Cooperativas/{coopUid}/Vendas/{pedidoId}
         /Vendas/{pedidoId}

         Assim o usuário pode apagar o pedido da tela dele,
         mas a venda continua salva para o financeiro.
        */

        val updates = hashMapOf<String, Any?>(
            "/Pedidos/$pedidoId" to null,
            "/Usuarios/$uid/Pedidos/$pedidoId" to null
        )

        FirebaseDatabase.getInstance()
            .reference
            .updateChildren(updates)
            .addOnSuccessListener {
                onPedidoAlterado()
            }
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }
}