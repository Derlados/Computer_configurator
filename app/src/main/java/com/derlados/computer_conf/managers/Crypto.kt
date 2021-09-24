package com.derlados.computer_conf.managers

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object Crypto {

    fun getHash(text: String): String {
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        md.update(text.toByteArray(StandardCharsets.UTF_16))
        val digest: ByteArray = md.digest()

        return java.lang.String.format("%064x", BigInteger(1, digest))
    }
}