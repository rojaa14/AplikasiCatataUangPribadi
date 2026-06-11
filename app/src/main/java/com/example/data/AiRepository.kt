package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AiRepository {
    private val client = OkHttpClient()

    suspend fun getAdvice(provider: String, apiKey: String, expenses: List<ExpenseEntity>): String = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext "API Key belum diatur. Silakan masukkan di Pengaturan."

        val prompt = buildString {
            append("Saya memiliki data keuangan bulan ini sebagai berikut:\n")
            expenses.forEach {
                append("- ${it.type} (${it.category}): Rp ${it.amount} on ${it.date}\n")
            }
            append("\nBantu saya menganalisis pengeluaran ini dan berikan tips cara hemat dan agar tidak 'boncos'. Berikan jawaban ringkas dan ramah dalam bahasa Indonesia.")
        }

        try {
            when (provider) {
                "Gemini" -> callGemini(apiKey, prompt)
                "OpenRouter" -> callOpenAIFormat(apiKey, prompt, "https://openrouter.ai/api/v1/chat/completions", "google/gemini-2.5-pro")
                "Groq" -> callOpenAIFormat(apiKey, prompt, "https://api.groq.com/openai/v1/chat/completions", "llama3-8b-8192")
                else -> "Provider tidak dikenal"
            }
        } catch (e: Exception) {
            "Gagal mengambil saran: ${e.message}"
        }
    }

    private fun callGemini(apiKey: String, prompt: String): String {
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
        }.toString()

        val request = Request.Builder().url(url).post(json.toRequestBody(jsonMediaType)).build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val raw = response.body?.string() ?: return "Kosong"
            val jsonObj = JSONObject(raw)
            return jsonObj.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: "Tidak dapat membaca respon"
        }
    }

    private fun callOpenAIFormat(apiKey: String, prompt: String, url: String, model: String): String {
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val json = JSONObject().apply {
            put("model", model)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }.toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(json.toRequestBody(jsonMediaType))
            .build()
            
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val raw = response.body?.string() ?: return "Kosong"
            val jsonObj = JSONObject(raw)
            return jsonObj.optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content") ?: "Tidak dapat membaca respon"
        }
    }
}
