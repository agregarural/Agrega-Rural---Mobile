package com.mobile.agregarural

import retrofit2.http.GET
import retrofit2.http.Path

data class ViaCepResponse(
    val cep: String?,
    val erro: Boolean? // Retorna true se CEP não existe
)

interface ViaCepService {
    @GET("ws/{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): ViaCepResponse
}