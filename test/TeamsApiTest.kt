package com.portfolio.footballpredictor

import com.portfolio.footballpredictor.model.Team
import com.portfolio.footballpredictor.repository.MockTeamRepository
import com.portfolio.footballpredictor.repository.TeamRepository
import com.portfolio.footballpredictor.service.TeamService
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


private class FailingRepository(private val error: Exception) : TeamRepository {
    override suspend fun getAllTeams(): List<Team> = throw error
}


private fun testWith(
    service: TeamService,
    block: suspend ApplicationTestBuilder.() -> Unit
) = testApplication {
    environment { config = MapApplicationConfig() }
    application {
        configureSerialization()
        routing { teamRoutes(service) }
    }
    block()
}

class TeamsApiTest {

    private val twoTeamsJson = """
        [
          {"id":"AAA","name":"Alpha FC","attackStrength":1.40,"defenseStrength":0.65},
          {"id":"BBB","name":"Beta FC","attackStrength":0.80,"defenseStrength":1.30}
        ]
    """.trimIndent()

    @Test
    fun `GET api teams returns 200 with JSON list`() =
        testWith(TeamService(MockTeamRepository(jsonContent = twoTeamsJson))) {
            val response = client.get("/api/teams")
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters())

            val teams = Json.decodeFromString<List<Team>>(response.bodyAsText())
            assertEquals(listOf("AAA", "BBB"), teams.map { it.id })
            assertEquals(1.40f, teams[0].attackStrength)
            assertEquals(0.65f, teams[0].defenseStrength)
        }

    @Test
    fun `GET api teams with real mock file returns valid teams`() =
        testWith(TeamService(MockTeamRepository())) {
            val response = client.get("/api/teams")
            assertEquals(HttpStatusCode.OK, response.status)

            val teams = Json.decodeFromString<List<Team>>(response.bodyAsText())
            assertTrue(teams.size >= 6, "Minimal 6 tim, ditemukan ${teams.size}")
            assertEquals(teams.size, teams.map { it.id }.toSet().size, "ID harus unik")
            assertTrue(teams.all { it.attackStrength in 0.4f..2.0f })
            assertTrue(teams.all { it.defenseStrength in 0.4f..2.0f })
        }

    @Test
    fun `FileNotFoundException is mapped to 404 with error body`() =
        testWith(TeamService(FailingRepository(FileNotFoundException("missing")))) {
            val response = client.get("/api/teams")
            assertEquals(HttpStatusCode.NotFound, response.status)
            val body = Json.decodeFromString<ErrorResponse>(response.bodyAsText())
            assertEquals("Team data not found", body.error)
        }

    @Test
    fun `unexpected exception is mapped to 500 without leaking details`() =
        testWith(TeamService(FailingRepository(IllegalStateException("secret detail")))) {
            val response = client.get("/api/teams")
            assertEquals(HttpStatusCode.InternalServerError, response.status)
            val text = response.bodyAsText()
            assertEquals("Internal server error", Json.decodeFromString<ErrorResponse>(text).error)
            assertFalse(text.contains("secret detail"))
        }
}