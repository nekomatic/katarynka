package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ForceSuccessParserTest {

    private val emptyText = ""
    private val text = "a"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser = factory.successful()

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(emptyText.iterator())
        val result = parser.parse(input)

        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(Unit, (result as Either.Right).b.value) },
                { assertEquals(input.position, (result as Either.Right).b.remainingInput.position) }
        )
    }


    @DisplayName("Non-empty input")
    @Test
    fun nonEmptyInput() {
        val input = LineInput.of(text.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Right) },
                { assertEquals(Unit, (result as Either.Right).b.value) },
                { assertEquals(input.position, (result as Either.Right).b.remainingInput.position) }
        )

    }
}