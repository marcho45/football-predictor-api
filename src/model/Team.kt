package com.portfolio.footballpredictor.model

import kotlinx.serialization.Serializable

/**
 * Model data statistik kekuatan tim EPL.
 */
@Serializable
data class Team(
    val id: String,
    val name: String,
    val attackStrength: Float,
    val defenseStrength: Float
)
