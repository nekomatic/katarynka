package com.nekomatic.katarynka.core.parsers

import arrow.core.Either
import com.nekomatic.katarynka.core.IParser
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class ForceFailParserTest {


    private val emptyText = ""
    private val text = "a"
    private val factory = ParserFactory<Char, LineInput<Char>>()
    private val parser: IParser<Char, LineInput<Char>, Unit> = factory.failing("test failure")

    @DisplayName("Empty input")
    @Test
    fun emptyInput() {
        val input = LineInput.of(emptyText.iterator())
        val result = parser.parse(input)

        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("test failure", (result as Either.Left).a.head.expected) },
                { assertEquals(1, (result as Either.Left).a.size) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) }
        )
    }

    @DisplayName("Non-empty input")
    @Test
    fun nonEmptyInput() {
        val input = LineInput.of(text.iterator())
        val result = parser.parse(input)
        assertAll(
                { assert(result is Either.Left) },
                { assertEquals("test failure", (result as Either.Left).a.head.expected) },
                { assertEquals(1, (result as Either.Left).a.size) },
                { assertEquals(input.position, (result as Either.Left).a.head.failedAtInput.position) }
        )

    }
}