package com.mobile.agregarural.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.Normalizer
import java.util.Locale

object PixUtils {

    private fun campo(id: String, valor: String): String {
        val tamanho = valor.toByteArray(Charsets.UTF_8).size
            .toString()
            .padStart(2, '0')

        return id + tamanho + valor
    }

    private fun removerAcentos(texto: String): String {
        val normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return normalizado.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
    }

    private fun limparTextoPix(texto: String, limite: Int): String {
        return removerAcentos(texto)
            .uppercase(Locale.ROOT)
            .replace("[^A-Z0-9 ]".toRegex(), "")
            .take(limite)
    }

    fun limparTxid(txid: String): String {
        val txidLimpo = removerAcentos(txid)
            .uppercase(Locale.ROOT)
            .replace("[^A-Z0-9]".toRegex(), "")
            .take(25)

        return if (txidLimpo.isBlank()) {
            "TESTE1"
        } else {
            txidLimpo
        }
    }

    private fun crc16(payload: String): String {
        var crc = 0xFFFF
        val polinomio = 0x1021

        val bytes = payload.toByteArray(Charsets.UTF_8)

        for (byte in bytes) {
            crc = crc xor ((byte.toInt() and 0xFF) shl 8)

            for (i in 0 until 8) {
                crc = if ((crc and 0x8000) != 0) {
                    (crc shl 1) xor polinomio
                } else {
                    crc shl 1
                }

                crc = crc and 0xFFFF
            }
        }

        return crc.toString(16)
            .uppercase(Locale.ROOT)
            .padStart(4, '0')
    }

    fun gerarPixCopiaECola(
        chavePix: String,
        nomeRecebedor: String,
        cidadeRecebedor: String,
        valor: Double,
        txid: String
    ): String {

        val chave = chavePix.trim()

        val nome = limparTextoPix(
            texto = nomeRecebedor,
            limite = 25
        )

        val cidade = limparTextoPix(
            texto = cidadeRecebedor,
            limite = 15
        )

        val txidFinal = limparTxid(txid)

        val valorFormatado = String.format(
            Locale.US,
            "%.2f",
            valor
        )

        val merchantAccountInfo =
            campo("00", "BR.GOV.BCB.PIX") +
                    campo("01", chave)

        val dadosAdicionais =
            campo("05", txidFinal)

        val payloadSemCRC =
            campo("00", "01") +
                    campo("01", "11") +
                    campo("26", merchantAccountInfo) +
                    campo("52", "0000") +
                    campo("53", "986") +
                    campo("54", valorFormatado) +
                    campo("58", "BR") +
                    campo("59", nome) +
                    campo("60", cidade) +
                    campo("62", dadosAdicionais) +
                    "6304"

        val crc = crc16(payloadSemCRC)

        return payloadSemCRC + crc
    }

    fun gerarQrCode(
        texto: String,
        tamanho: Int = 800
    ): Bitmap {
        val writer = QRCodeWriter()

        val matrix = writer.encode(
            texto,
            BarcodeFormat.QR_CODE,
            tamanho,
            tamanho
        )

        val bitmap = Bitmap.createBitmap(
            tamanho,
            tamanho,
            Bitmap.Config.RGB_565
        )

        for (x in 0 until tamanho) {
            for (y in 0 until tamanho) {
                bitmap.setPixel(
                    x,
                    y,
                    if (matrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }

        return bitmap
    }
}