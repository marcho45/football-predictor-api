package com.portfolio.footballpredictor

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerTest {

    @Test
    fun `root endpoint returns running message`() = testApplication {
        configure()
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Football Predictor API is running!", response.bodyAsText())
    }

    @Test
    fun `app wiring from application yaml serves teams`() = testApplication {
        configure()
        val response = client.get("/api/teams")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"id\""))
    }
}