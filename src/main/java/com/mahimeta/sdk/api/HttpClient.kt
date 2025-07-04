package com.mahimeta.sdk.api

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpClient(private val baseUrl: String) {
    private val gson = Gson()
    private val executor = Executors.newSingleThreadExecutor()

    internal suspend inline fun <reified T> get(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap()
    ): T = suspendCoroutine { continuation ->
        executor.execute {
            try {
                val url = buildUrl(endpoint, queryParams)
                val connection = url.openConnection() as HttpURLConnection
                
                try {
                    connection.apply {
                        requestMethod = "GET"
                        setRequestProperty("Content-Type", "application/json")
                        connectTimeout = 30000
                        readTimeout = 30000
                    }

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().use { reader ->
                            val responseText = reader.readText()
                            gson.fromJson(responseText, T::class.java)
                        }
                        continuation.resume(response)
                    } else {
                        val error = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                        throw Exception("HTTP $responseCode: $error")
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun buildUrl(endpoint: String, params: Map<String, String>): URL {
        val url = "$baseUrl$endpoint"
        val queryString = params.entries.joinToString("&") { (key, value) ->
            "${key}=${java.net.URLEncoder.encode(value, "UTF-8")}"
        }
        return URL(if (params.isNotEmpty()) "$url?$queryString" else url)
    }

    fun shutdown() {
        executor.shutdown()
    }
}
