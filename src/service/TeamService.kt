package com.portfolio.footballpredictor.service

import com.portfolio.footballpredictor.model.Team
import com.portfolio.footballpredictor.repository.TeamRepository

class TeamService(private val repository: TeamRepository) {

    /** Mengambil semua tim untuk ditampilkan di API. */
    suspend fun getTeams(): List<Team> = repository.getAllTeams()
}