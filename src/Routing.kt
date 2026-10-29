package com.portfolio.footballpredictor

import com.portfolio.footballpredictor.repository.MockTeamRepository
import com.portfolio.footballpredictor.service.TeamService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.io.FileNotFoundException

@Serializable
data class ErrorResponse(val error: String)


fun Route.teamRoutes(teamService: TeamService) {
    get("/api/teams") {
        try {
            call.respond(teamService.getTeams())
        } catch (e: FileNotFoundException) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Team data not found"))
        } catch (e: Exception) {
            call.application.log.error("Failed to load teams", e)
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Internal server error"))
        }
    }
}

fun Application.configureRouting() {
    val teamService = TeamService(MockTeamRepository())

    routing {
        get("/") {
            call.respondText("Football Predictor API is running!")
        }
        teamRoutes(teamService)
    }
}