package com.mehmetkaradana.loadtest

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

object LoadTest {
    private const val TOTAL_REQUESTS = 1000 // Toplam istek sayısı
    private const val CONCURRENT_REQUESTS = 100 // Aynı anda gönderilecek istek sayısı
    private const val URL = "http://localhost:8082/api/getRooms" // Test etmek istediğiniz endpoint

    private val client = HttpClient()

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val responseTimes = mutableListOf<Long>()
        val errorCount = AtomicInteger(0)

        for (i in 0 until TOTAL_REQUESTS step CONCURRENT_REQUESTS) {
            val jobs = List(CONCURRENT_REQUESTS) {
                launch {
                    val responseTime = sendRequestAndMeasureTime()
                    if (responseTime != null) {
                        synchronized(responseTimes) { // Eşzamanlı erişimi kontrol et
                            responseTimes.add(responseTime)
                        }
                    } else {
                        errorCount.incrementAndGet()
                    }
                }
            }

            // Her grup için tüm işlerin tamamlanmasını bekle
            jobs.forEach { it.join() }
        }

        analyzeResults(responseTimes, errorCount.get())
    }

    private suspend fun sendRequestAndMeasureTime(): Long? {
        return try {
            measureTimeMillis {
                val response: HttpResponse = client.get(URL) {
                    header(HttpHeaders.Authorization, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY3MWEyOTFiNzY0YThlMTEwNzlkMGYyZSIsImlhdCI6MTcyOTc2NzcwNywiZXhwIjoxNzMyMzU5NzA3fQ.ijMcqGw4OUOWG79jrXhmkAwGolVJUjNXp6Y15y-OY7Y")
                }

                if (response.status.isSuccess()) {
                    // Yanıt başarılıysa, zamanın uzunluğunu döndür
                    return@measureTimeMillis
                } else {
                    println("Error: ${response.status}")
                    return null
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null
        }
    }

    private fun analyzeResults(responseTimes: List<Long>, errorCount: Int) {
        val total = responseTimes.sum()
        val maxTime = responseTimes.maxOrNull() ?: 0
        val minTime = responseTimes.minOrNull() ?: 0

        println("Total Requests: ${responseTimes.size + errorCount}")
        println("Successful Requests: ${responseTimes.size}")
        println("Failed Requests: $errorCount")
        println("Average Response Time: ${if (responseTimes.isNotEmpty()) total / responseTimes.size else 0} ms") // Average response time
        println("Maximum Response Time: $maxTime ms")
        println("Minimum Response Time: $minTime ms")

    }
}
