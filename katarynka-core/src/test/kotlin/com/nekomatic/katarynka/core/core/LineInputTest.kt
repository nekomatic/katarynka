package com.nekomatic.katarynka.core.core

import arrow.core.None
import arrow.core.some
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.combinators.sMap
import com.nekomatic.katarynka.core.combinators.then
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.input.LineInput
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals


@Suppress("LocalVariableName")
internal class LineInputTest {


    private val text0 = ""
    private val text1 = "a"
    private val text2 = "ab"
    private val textEol1 = "\r\nb"

    private val factory = ParserFactory<Char, Input<Char>>()
    private val eolParser =
            (factory.item("r", '\r') then factory.item("n", '\n') sMap { 1L })

    @DisplayName("LineInput.next")
    @Test
    fun getNext() {
        val input0 = LineInput.of(text0.iterator(), eolParser).next
        val input1 = LineInput.of(text1.iterator(), eolParser).next
        val input2 = LineInput.of(text2.iterator(), eolParser).next

        assertAll(
                { assertEquals(None, input0.item) },
                { assertEquals(None, input1.item) },
                { assertEquals('b'.some(), input2.item) }
        )
    }

    @DisplayName("LineInput.item")
    @Test
    fun getItem() {
        val input0 = LineInput.of(text0.iterator(), eolParser)
        val input1 = LineInput.of(text1.iterator(), eolParser)
        val input2 = LineInput.of(text2.iterator(), eolParser)

        assertAll(
                { assertEquals(None, input0.item) },
                { assertEquals('a'.some(), input1.item) },
                { assertEquals('a'.some(), input2.item) }
        )
    }

    @DisplayName("LineInput.position")
    @Test
    fun getPosition() {
        val input0 = LineInput.of(text0.iterator(), eolParser)
        val input0next = input0.next
        val input1 = LineInput.of(text1.iterator(), eolParser)
        val input1next = input1.next
        val input1nextnext = input1next.next
        val input2 = LineInput.of(text2.iterator(), eolParser)
        val input2next = input2.next

        assertAll(
                { assertEquals(0, input0.position) },
                { assertEquals(0, input0next.position) },
                { assertEquals(0, input1.position) },
                { assertEquals(1, input1next.position) },
                { assertEquals(input1next.position, input1nextnext.position) },
                { assertEquals(0, input2.position) },
                { assertEquals(1, input2next.position) }
        )
    }


    @DisplayName("LineInput.line")
    @Test
    fun getLine() {
        val input0 = LineInput.of(text0.iterator(), eolParser)
        val input1 = LineInput.of(text1.iterator(), eolParser)
        val input2 = LineInput.of(text2.iterator(), eolParser)

        val inputEol1_l0a = LineInput.of(textEol1.iterator(), eolParser)
        val inputEol1_l0b = inputEol1_l0a.next
        val inputEol1_l1 = inputEol1_l0b.next

        assertAll(
                { assertEquals(1, input0.line) },
                { assertEquals(1, input1.line) },
                { assertEquals(1, input2.line) },
                { assertEquals(1, inputEol1_l0a.line) },
                { assertEquals(1, inputEol1_l0b.line) },
                { assertEquals(2, inputEol1_l1.line) }
        )
    }

    @DisplayName("LineInput.column")
    @Test
    fun getColumn() {
        val input0 = LineInput.of(text0.iterator(), eolParser)
        val input1 = LineInput.of(text1.iterator(), eolParser)
        val input2 = LineInput.of(text2.iterator(), eolParser)

        val inputEol2_l1a = LineInput.of(textEol1.iterator(), eolParser)
        val inputEol2_l1b = inputEol2_l1a.next
        val inputEol2_l2 = inputEol2_l1b.next

        assertAll(
                { assertEquals(1, input0.column) },
                { assertEquals(1, input1.column) },
                { assertEquals(1, input2.column) },
                { assertEquals(1, inputEol2_l1a.column) },
                { assertEquals(2, inputEol2_l1b.column) },
                { assertEquals(1, inputEol2_l2.column) }
        )
    }
}