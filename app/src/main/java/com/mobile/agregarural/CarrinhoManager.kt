package com.mobile.agregarural

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object CarrinhoManager {

    val itens = mutableListOf<ItemCarrinhos>()

    fun adicionarProduto(produto: Produto, quantidade: Int) {
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

            salvarNoFirebase(produto, itemExistente.quantidade)

        } else {
            itens.add(
                ItemCarrinhos(
                    produto = produto,
                    quantidade = quantidade
                )
            )

            salvarNoFirebase(produto, quantidade)
        }
    }
    fun comprarAgora(produto: Produto, quantidade: Int) {
        itens.clear()

        itens.add(
            ItemCarrinhos(
                produto = produto,
                quantidade = quantidade,
                selecionado = true
            )
        )
    }

    fun carregarCarrinho(onComplete: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .get()
            .addOnSuccessListener { snapshot ->

                itens.clear()

                for (itemSnapshot in snapshot.children) {
                    val nome = itemSnapshot.child("nome").getValue(String::class.java) ?: ""
                    val preco = itemSnapshot.child("preco").getValue(Double::class.java) ?: 0.0
                    val imagem = itemSnapshot.child("imagem").getValue(String::class.java) ?: ""
                    val categoria = itemSnapshot.child("categoria").getValue(String::class.java) ?: ""
                    val descricao = itemSnapshot.child("descricao").getValue(String::class.java) ?: ""
                    val estoque = itemSnapshot.child("estoque").getValue(Int::class.java) ?: 0
                    val quantidade = itemSnapshot.child("quantidade").getValue(Int::class.java) ?: 1

                    val produto = Produto(
                        nome = nome,
                        preco = preco,
                        imagem = imagem,
                        categoria = categoria,
                        descricao = descricao,
                        estoque = estoque
                    )

                    itens.add(
                        ItemCarrinhos(
                            produto = produto,
                            quantidade = quantidade
                        )
                    )
                }

                onComplete()
            }
    }

    fun atualizarQuantidade(item: ItemCarrinhos) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val total = item.produto.preco * item.quantidade

        val updates = mapOf<String, Any>(
            "quantidade" to item.quantidade,
            "total" to total
        )

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .child(item.produto.nome)
            .updateChildren(updates)
    }

    fun removerProduto(item: ItemCarrinhos) {
        itens.remove(item)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .child(item.produto.nome)
            .removeValue()
    }

    fun itensSelecionados(): List<ItemCarrinhos> {
        return itens.filter { it.selecionado }
    }

    private fun salvarNoFirebase(produto: Produto, quantidade: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val total = produto.preco * quantidade

        val dadosCarrinho = hashMapOf<String, Any>(
            "nome" to produto.nome,
            "preco" to produto.preco,
            "imagem" to produto.imagem,
            "categoria" to produto.categoria,
            "descricao" to produto.descricao,
            "estoque" to produto.estoque,
            "quantidade" to quantidade,
            "total" to total
        )

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .child("Carrinho")
            .child(produto.nome)
            .setValue(dadosCarrinho)
    }
}