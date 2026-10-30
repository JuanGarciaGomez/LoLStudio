package com.project.lolstudio.game.data.liveclient

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private const val RIOT_ROOT_CERTIFICATE = "/riotgames.pem"

fun createLiveClientHttpClient(): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 2_000
    }
    engine {
        config {
            val trustManager = riotTrustManager()
            val sslContext = SSLContext.getInstance("TLS").apply { init(null, arrayOf(trustManager), null) }
            sslSocketFactory(sslContext.socketFactory, trustManager)
            // Riot's certificate is issued to "rclient", so the loopback IP never matches its hostname.
            hostnameVerifier { hostname, _ -> hostname == LIVE_CLIENT_HOST }
        }
    }
}

private fun riotTrustManager(): X509TrustManager {
    val certificate = LiveClientGameRepository::class.java.getResourceAsStream(RIOT_ROOT_CERTIFICATE)
        .let(::requireNotNull)
        .use { CertificateFactory.getInstance("X.509").generateCertificate(it) }
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null)
        setCertificateEntry("riotgames", certificate)
    }
    return TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        .apply { init(keyStore) }
        .trustManagers
        .filterIsInstance<X509TrustManager>()
        .single()
}
