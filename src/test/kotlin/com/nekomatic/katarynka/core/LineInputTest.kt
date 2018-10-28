package com.nekomatic.katarynka.core

import arrow.core.None
import arrow.core.some
import com.nekomatic.katarynka.combinators.then
import com.nekomatic.katarynka.combinators.map
import com.nekomatic.katarynka.parsers.ItemParser
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals


@Suppress("LocalVariableName")
internal class LineInputTest {


    private val text0 = "".toList()
    private val text1 = "a".toList()
    private val text2 = "ab".toList()
    private val textEol1 = "\r\nb".toList()

    private val eolParser =
            ItemParser<Char, Input<Char>>({ "r" }, '\r') then ItemParser({ "n" }, '\n') map { _ -> 1L }

    @DisplayName("LineInput.next")
    @Test
    fun getNext() {
        val input0 = LineInput.create(text0.iterator(), eolParser).next
        val input1 = LineInput.create(text1.iterator(), eolParser).next
        val input2 = LineInput.create(text2.iterator(), eolParser).next

        assertAll(
                { assertEquals(None, input0.item, "Next of an empty LineInput should None item (should be empty)") },
                { assertEquals(None, input1.item, "Next of last a non-empty LineInput should have None item (should be empty)") },
                { assertEquals('b'.some(), input2.item, "Next of a LineInput with successors should have Some item (should not be empty)") }
        )
    }

    @DisplayName("LineInput.item")
    @Test
    fun getItem() {
        val input0 = LineInput.create(text0.iterator(), eolParser)
        val input1 = LineInput.create(text1.iterator(), eolParser)
        val input2 = LineInput.create(text2.iterator(), eolParser)

        assertAll(
                { assertEquals(None, input0.item, "Item of an empty LineInput should be None") },
                { assertEquals('a'.some(), input1.item, "Item of non-empty single-element LineInput should be Some") },
                { assertEquals('a'.some(), input2.item, "Item of non-empty LineInput with successors should be Some") }
        )
    }

    @DisplayName("LineInput.position")
    @Test
    fun getPosition() {
        val input0 = LineInput.create(text0.iterator(), eolParser)
        val input0next = input0.next
        val input1 = LineInput.create(text1.iterator(), eolParser)
        val input1next = input1.next
        val input1nextnext = input1next.next
        val input2 = LineInput.create(text2.iterator(), eolParser)
        val input2next = input2.next

        assertAll(
                { assertEquals(0, input0.position, "Start position of an empty LineInput should be 0") },
                { assertEquals(0, input0next.position, "Position of the next to an empty LineInput should be 0") },
                { assertEquals(0, input1.position, "Start position of a single-element LineInput should be 0") },
                { assertEquals(1, input1next.position, "Position of the next to a single-element LineInput should be 1") },
                { assertEquals(input1next.position, input1nextnext.position, "Position of a LineInput beyond the last one should be the same as the last one") },
                { assertEquals(0, input2.position, "Start position of a single-element LineInput should be 0") },
                { assertEquals(1, input2next.position, "Position of the next to a LineInput with successors should be 1") }
        )
    }


    @DisplayName("LineInput.line")
    @Test
    fun getLine() {
        val input0 = LineInput.create(text0.iterator(), eolParser)
        val input1 = LineInput.create(text1.iterator(), eolParser)
        val input2 = LineInput.create(text2.iterator(), eolParser)

        val inputEol1_l0a = LineInput.create(textEol1.iterator(), eolParser)
        val inputEol1_l0b = inputEol1_l0a.next
        val inputEol1_l1 = inputEol1_l0b.next

        assertAll(
                { assertEquals(1, input0.line, "Line of an empty LineInput should be 1") },
                { assertEquals(1, input1.line, "Line of the first of a single-nonEol-element LineInput should be 1") },
                { assertEquals(1, input2.line, "Line of the first of a multi-nonEol-element LineInput should be 1") },
                { assertEquals(1, inputEol1_l0a.line, "Line of the element before the last item of the first EOL sequence should be 1") },
                { assertEquals(1, inputEol1_l0b.line, "Line of the element at the end of the first EOL sequence should be 1") },
                { assertEquals(2, inputEol1_l1.line, "Line of the first element after the end of the first EOL sequence should be 2") }
        )
    }

    @DisplayName("LineInput.column")
    @Test
    fun getColumn() {
        val input0 = LineInput.create(text0.iterator(), eolParser)
        val input1 = LineInput.create(text1.iterator(), eolParser)
        val input2 = LineInput.create(text2.iterator(), eolParser)

        val inputEol2_l1a = LineInput.create(textEol1.iterator(), eolParser)
        val inputEol2_l1b = inputEol2_l1a.next
        val inputEol2_l2 = inputEol2_l1b.next

        assertAll(
                { assertEquals(1, input0.column, "Column of an empty LineInput should be 1") },
                { assertEquals(1, input1.column, "Column of the first of a single-nonEol-element LineInput should be 1") },
                { assertEquals(1, input2.column, "Column of the first of a multi-nonEol-element LineInput should be 1") },
                { assertEquals(1, inputEol2_l1a.column, "Column of the first line element should be 1") },
                { assertEquals(2, inputEol2_l1b.column, "Column of the second line element before the last item of the nearest next EOL sequence should be 2") },
                { assertEquals(1, inputEol2_l2.column, "Column of the first element after the end of the an EOL sequence should be 1") }
        )
    }
}