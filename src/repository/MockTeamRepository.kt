package com.portfolio.footballpredictor.repository

import com.portfolio.footballpredictor.model.Team
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

/**
 * Implementasi [TeamRepository] yang memuat data tim dari file JSON mock di classpath.
 *
 * @param resourcePath Path ke file JSON sumber di dalam classpath.
 * @param jsonContent Konten JSON eksplisit (opsional, terutama untuk pengujian).
 */
class MockTeamRepository(
    private val resourcePath: String = "mock_epl_stats.json",
    jsonContent: String? = null
) : TeamRepository {

    private val teams: List<Team> = parseAndValidate(
        jsonContent ?: readResourceText(resourcePath)
    )

    override suspend fun getAllTeams(): List<Team> = teams

    companion object {
        /**
         * Instance Json ketat (tanpa isLenient, tanpa mengabaikan kunci tak dikenal).
         */
        private val strictJson = Json {
            isLenient = false
            ignoreUnknownKeys = false
        }

        /**
         * Membaca isi file teks dari classpath.
         */
        private fun readResourceText(path: String): String {
            val cleanPath = path.removePrefix("/")
            val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(cleanPath)
                ?: MockTeamRepository::class.java.classLoader?.getResourceAsStream(cleanPath)
                ?: MockTeamRepository::class.java.getResourceAsStream("/$cleanPath")
                ?: throw FileNotFoundException("Resource data mock '$path' tidak ditemukan di classpath.")

            return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        }

        /**
         * Melakukan deserialisasi JSON ketat dan memvalidasi integritas data tim.
         */
        fun parseAndValidate(jsonString: String): List<Team> {
            val decoded = strictJson.decodeFromString<List<Team>>(jsonString)
            validate(decoded)
            return decoded
        }

        /**
         * Memvalidasi aturan bisnis data tim: ID unik dan nilai kekuatan >= 0.
         */
        fun validate(teams: List<Team>) {
            val seenIds = mutableSetOf<String>()
            for (team in teams) {
                require(seenIds.add(team.id)) {
                    "Ditemukan duplikasi ID tim: '${team.id}' (${team.name})"
                }
                require(team.attackStrength >= 0f) {
                    "Nilai attackStrength tim '${team.id}' tidak boleh negatif: ${team.attackStrength}"
                }
                require(team.defenseStrength >= 0f) {
                    "Nilai defenseStrength tim '${team.id}' tidak boleh negatif: ${team.defenseStrength}"
                }
            }
        }
    }
}
