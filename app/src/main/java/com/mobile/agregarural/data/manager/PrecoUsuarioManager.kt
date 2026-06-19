package com.mobile.agregarural.data.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mobile.agregarural.data.model.Produto

object PrecoUsuarioManager {

    fun verificarUsuarioEhCooperado(callback: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            callback(false)
            return
        }

        FirebaseDatabase.getInstance()
            .getReference("Usuarios")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val tipoUsuario = snapshot.child("tipoUsuario").getValue(String::class.java)
                    ?: snapshot.child("tipo").getValue(String::class.java)
                    ?: ""

                val matricula = snapshot.child("matricula").getValue(String::class.java) ?: ""

                val ehCliente = tipoUsuario.lowercase() == "cliente"
                val temMatricula = matricula.trim().isNotEmpty()

                callback(ehCliente && temMatricula)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun precoFinal(produto: Produto, usuarioEhCooperado: Boolean): Double {
        if (!usuarioEhCooperado) {
            return produto.preco
        }

        if (produto.precoCooperado > 0.0) {
            return produto.precoCooperado
        }

        if (produto.descontoCooperado > 0.0) {
            return produto.preco - (produto.preco * produto.descontoCooperado / 100.0)
        }

        return produto.preco
    }
}