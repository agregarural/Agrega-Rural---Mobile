package com.mobile.agregarural.data.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.data.model.ItemCarrinhos
import com.mobile.agregarural.data.model.Produto

object CarrinhoManager {

    val itens = mutableListOf<ItemCarrinhos>()

    fun adicionarProduto(
        produto: Produto,
        quantidade: Int,
        usuarioEhCooperado: Boolean
    ) {
        val precoFinal = PrecoUsuarioManager.precoFinal(produto, usuarioEhCooperado)

        val itemExistente = itens.find {
            it.produto.nome == produto.nome
        }

        if (itemExistente != null) {
            val novaQuantidade = itemExistente.quantidade + quantidade

            itemExistente.quantidade = if (novaQuantidade > produto.estoque) {
                produto.estoque
            } else {
                novaQuantidade
            }

            itemExistente.precoUnitario = precoFinal
            itemExistente.usuarioCooperado = usuarioEhCooperado

            salvarNoFirebase(
                produto = produto,
                quantidade = itemExistente.quantidade,
                precoUnitario = precoFinal,
                usuarioEhCooperado = usuarioEhCooperado
            )

        } else {
            val novoItem = ItemCarrinhos(
                produto = produto,
                quantidade = quantidade,
                precoUnitario = precoFinal,
                usuarioCooperado = usuarioEhCooperado
            )

            itens.add(novoItem)

            salvarNoFirebase(
                produto = produto,
                quantidade = quantidade,
                precoUnitario = precoFinal,
                usuarioEhCooperado = usuarioEhCooperado
            )
        }
    }

    fun comprarAgora(
        produto: Produto,
        quantidade: Int,
        usuarioEhCooperado: Boolean
    ) {
        val precoFinal = PrecoUsuarioManager.precoFinal(produto, usuarioEhCooperado)

        itens.clear()

        itens.add(
            ItemCarrinhos(
                produto = produto,
                quantidade = quantidade,
                selecionado = true,
                precoUnitario = precoFinal,
                usuarioCooperado = usuarioEhCooperado
            )
        )
    }

    fun carregarCarrinho(onComplete: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            itens.clear()
            onComplete()
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .get()
            .addOnSuccessListener { snapshot ->

                itens.clear()

                for (itemSnapshot in snapshot.children) {
                    val nome = itemSnapshot.child("nome").getValue(String::class.java) ?: ""
                    val categoria = itemSnapshot.child("categoria").getValue(String::class.java) ?: ""
                    val descricao = itemSnapshot.child("descricao").getValue(String::class.java) ?: ""
                    val imagem = itemSnapshot.child("imagem").getValue(String::class.java) ?: ""

                    val precoNormal = getDouble(itemSnapshot, "precoNormal")
                        .takeIf { it > 0.0 }
                        ?: getDouble(itemSnapshot, "preco")

                    val precoCooperado = getDouble(itemSnapshot, "precoCooperado")
                    val descontoCooperado = getDouble(itemSnapshot, "descontoCooperado")

                    val precoUnitario = getDouble(itemSnapshot, "precoUnitario")
                        .takeIf { it > 0.0 }
                        ?: precoNormal

                    val custo = getDouble(itemSnapshot, "custo")
                    val estoque = getInt(itemSnapshot, "estoque")

                    val quantidade = getInt(itemSnapshot, "quantidade")
                        .takeIf { it > 0 }
                        ?: 1

                    val usuarioCooperado = itemSnapshot.child("usuarioCooperado")
                        .getValue(Boolean::class.java) ?: false

                    val produto = Produto(
                        nome = nome,
                        categoria = categoria,
                        preco = precoNormal,
                        precoCooperado = precoCooperado,
                        descontoCooperado = descontoCooperado,
                        custo = custo,
                        estoque = estoque,
                        descricao = descricao,
                        imagem = imagem
                    )

                    itens.add(
                        ItemCarrinhos(
                            produto = produto,
                            quantidade = quantidade,
                            precoUnitario = precoUnitario,
                            usuarioCooperado = usuarioCooperado
                        )
                    )
                }

                onComplete()
            }
            .addOnFailureListener {
                itens.clear()
                onComplete()
            }
    }

    fun atualizarQuantidade(item: ItemCarrinhos) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val total = item.precoUnitario * item.quantidade

        val updates = mapOf<String, Any>(
            "quantidade" to item.quantidade,
            "precoUnitario" to item.precoUnitario,
            "total" to total
        )

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .child(gerarChaveProduto(item.produto.nome))
            .updateChildren(updates)
    }

    fun removerProduto(item: ItemCarrinhos) {
        itens.remove(item)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .child(gerarChaveProduto(item.produto.nome))
            .removeValue()
    }

    fun itensSelecionados(): List<ItemCarrinhos> {
        return itens.filter { it.selecionado }
    }

    fun removerItensSelecionadosDoCarrinhoFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().reference

        val selecionados = itensSelecionados().toList()

        for (item in selecionados) {
            database.child("Usuarios")
                .child(uid)
                .child("Carrinho")
                .child(gerarChaveProduto(item.produto.nome))
                .removeValue()

            itens.remove(item)
        }
    }

    private fun salvarNoFirebase(
        produto: Produto,
        quantidade: Int,
        precoUnitario: Double,
        usuarioEhCooperado: Boolean
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val total = precoUnitario * quantidade

        val dadosCarrinho = hashMapOf<String, Any>(
            "nome" to produto.nome,
            "categoria" to produto.categoria,
            "descricao" to produto.descricao,
            "imagem" to produto.imagem,
            "estoque" to produto.estoque,
            "custo" to produto.custo,

            "preco" to precoUnitario,
            "precoUnitario" to precoUnitario,
            "precoNormal" to produto.preco,
            "precoCooperado" to produto.precoCooperado,
            "descontoCooperado" to produto.descontoCooperado,
            "usuarioCooperado" to usuarioEhCooperado,

            "quantidade" to quantidade,
            "total" to total
        )

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .child(gerarChaveProduto(produto.nome))
            .setValue(dadosCarrinho)
    }

    private fun gerarChaveProduto(nome: String): String {
        return nome
            .trim()
            .replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
            .replace("/", "_")
    }

    private fun getDouble(snapshot: DataSnapshot, campo: String): Double {
        val valor = snapshot.child(campo).value

        return when (valor) {
            is Double -> valor
            is Long -> valor.toDouble()
            is Int -> valor.toDouble()
            is Float -> valor.toDouble()
            is String -> valor.replace(",", ".").toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun getInt(snapshot: DataSnapshot, campo: String): Int {
        val valor = snapshot.child(campo).value

        return when (valor) {
            is Int -> valor
            is Long -> valor.toInt()
            is Double -> valor.toInt()
            is Float -> valor.toInt()
            is String -> valor.toIntOrNull() ?: 0
            else -> 0
        }
    }
}