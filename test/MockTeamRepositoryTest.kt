package com.portfolio.footballpredictor

import com.portfolio.footballpredictor.repository.MockTeamRepository
import kotlinx.serialization.SerializationException
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MockTeamRepositoryTest {

    private fun team(id: String, atk: String = "1.0", def: String = "1.0") =
        """{"id":"$id","name":"Team $id","attackStrength":$atk,"defenseStrength":$def}"""

    private fun list(vararg items: String) = "[" + items.joinToString(",") + "]"

    @Test
    fun `valid data is parsed`() {
        val teams = MockTeamRepository.parseAndValidate(list(team("AAA"), team("BBB")))
        assertEquals(2, teams.size)
    }

    @Test
    fun `duplicate id is rejected and message names the id`() {
        val error = assertFailsWith<IllegalArgumentException> {
            MockTeamRepository.parseAndValidate(list(team("AAA"), team("AAA")))
        }
        assertTrue(error.message!!.contains("AAA"))
    }

    @Test
    fun `negative defenseStrength is rejected`() {
        val error = assertFailsWith<IllegalArgumentException> {
            MockTeamRepository.parseAndValidate(list(team("AAA", def = "-1.0")))
        }
        assertTrue(error.message!!.contains("AAA"))
    }

    @Test
    fun `negative defenseStrength is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            MockTeamRepository.parseAndValidate(list(team("AAA", def = "-1.0")))
        }
    }

    @Test
    fun `unknown key is rejected by strict json`() {
        val withExtra = """[{"id":"AAA","name":"A","attackStrength":1.0,"defenseStrength":1.0,"extra":1}]"""
        assertFailsWith<SerializationException> { MockTeamRepository.parseAndValidate(withExtra) }
    }

    @Test
    fun `malformed json is rejected`() {
        assertFailsWith<SerializationException> { MockTeamRepository.parseAndValidate("not json") }
    }

    @Test
    fun `missing resource throws FileNotFoundException`() {
        assertFailsWith<FileNotFoundException> { MockTeamRepository(resourcePath = "does_not_exist.json") }
    }

    @Test
    fun `real mock file loads and passes validation`() {
        MockTeamRepository() // tidak boleh melempar exception
    }
}