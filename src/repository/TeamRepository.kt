package com.portfolio.footballpredictor.repository

import com.portfolio.footballpredictor.model.Team

/**
 * Interface repositori data tim.
 */
interface TeamRepository {
    /**
     * Mengambil seluruh daftar tim yang tersedia.
     */
    suspend fun getAllTeams(): List<Team>
}
