package com.nekomatic.katarynka.chars

import arrow.core.None
import arrow.core.some
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

internal class CInputTest {

    private val text0 = ""
    private val text1 = "a"
    private val text2 = "ab"
    private val textEol1 = "\r\nb"

    private val eolParser = CInputEolParser

    @DisplayName("CInput.next")
    @Test
    fun getNext() {
        val input0 = CInput.of(text0.iterator(), eolParser).next
        val input1 = CInput.of(text1.iterator(), eolParser).next
        val input2 = CInput.of(text2.iterator(), eolParser).next

        assertAll(
                { assertEquals(None, input0.item, "Next of an empty CInput should None item (should be empty)") },
                { assertEquals(None, input1.item, "Next of last non-empty CInput should have None item (should be empty)") },
                { assertEquals('b'.some(), input2.item, "Next of a CInput with successors should have Some item (should not be empty)") }
        )
    }

    @DisplayName("CInput.item")
    @Test
    fun getItem() {
        val input0 = CInput.of(text0.iterator(), eolParser)
        val input1 = CInput.of(text1.iterator(), eolParser)
        val input2 = CInput.of(text2.iterator(), eolParser)

        assertAll(
                { assertEquals(None, input0.item, "Item of an empty CInput should be None") },
                { assertEquals('a'.some(), input1.item, "Item of non-empty single-element CInput should be Some") },
                { assertEquals('a'.some(), input2.item, "Item of non-empty CInput with successors should be Some") }
        )
    }

    @DisplayName("CInput.position")
    @Test
    fun getPosition() {
        val input0 = CInput.of(text0.iterator(), eolParser)
        val input0next = input0.next
        val input1 = CInput.of(text1.iterator(), eolParser)
        val input1next = input1.next
        val input1nextnext = input1next.next
        val input2 = CInput.of(text2.iterator(), eolParser)
        val input2next = input2.next

        assertAll(
                { assertEquals(0, input0.position, "Start position of an empty CInput should be 0") },
                { assertEquals(0, input0next.position, "Position of the next to an empty CInput should be 0") },
                { assertEquals(0, input1.position, "Start position of a single-element CInput should be 0") },
                { assertEquals(1, input1next.position, "Position of the next to a single-element CInput should be 1") },
                { assertEquals(input1next.position, input1nextnext.position, "Position of a CInput beyond the last one should be the same as the last one") },
                { assertEquals(0, input2.position, "Start position of a single-element CInput should be 0") },
                { assertEquals(1, input2next.position, "Position of the next to a CInput with successors should be 1") }
        )
    }


    @DisplayName("CInput.line with eol detection")
    @Test
    fun getLineEol() {
        val input0 = CInput.of(text0.iterator(), eolParser)
        val input1 = CInput.of(text1.iterator(), eolParser)
        val input2 = CInput.of(text2.iterator(), eolParser)

        val inputEol1_l0a = CInput.of(textEol1.iterator(), eolParser)
        val inputEol1_l0b = inputEol1_l0a.next
        val inputEol1_l1 = inputEol1_l0b.next

        assertAll(
                { assertEquals(1, input0.line, "Line of an empty CInput should be 1") },
                { assertEquals(1, input1.line, "Line of the first of a single-nonEol-element CInput should be 1") },
                { assertEquals(1, input2.line, "Line of the first of a multi-nonEol-element CInput should be 1") },
                { assertEquals(1, inputEol1_l0a.line, "Line of the element before the last item of the first EOL sequence should be 1") },
                { assertEquals(1, inputEol1_l0b.line, "Line of the element at the end of the first EOL sequence should be 1") },
                { assertEquals(2, inputEol1_l1.line, "Line of the first element after the end of the first EOL sequence should be 2") }
        )
    }

    @DisplayName("CInput.column with eol detection")
    @Test
    fun getColumnEol() {
        val input0 = CInput.of(text0.iterator(), eolParser)
        val input1 = CInput.of(text1.iterator(), eolParser)
        val input2 = CInput.of(text2.iterator(), eolParser)

        val inputEol2_l1a = CInput.of(textEol1.iterator(), eolParser)
        val inputEol2_l1b = inputEol2_l1a.next
        val inputEol2_l2 = inputEol2_l1b.next

        assertAll(
                { assertEquals(1, input0.column, "Column of an empty CInput should be 1") },
                { assertEquals(1, input1.column, "Column of the first of a single-nonEol-element CInput should be 1") },
                { assertEquals(1, input2.column, "Column of the first of a multi-nonEol-element CInput should be 1") },
                { assertEquals(1, inputEol2_l1a.column, "Column of the first line element should be 1") },
                { assertEquals(2, inputEol2_l1b.column, "Column of the second line element before the last item of the nearest next EOL sequence should be 2") },
                { assertEquals(1, inputEol2_l2.column, "Column of the first element after the end of the an EOL sequence should be 1") }
        )
    }

    @DisplayName("CInput.line with no eol detection")
    @Test
    fun getLineNoEol() {
        val input0 = CInput.of(text0.iterator())
        val input1 = CInput.of(text1.iterator())
        val input2 = CInput.of(text2.iterator())

        val inputEol1_l0a = CInput.of(textEol1.iterator())
        val inputEol1_l0b = inputEol1_l0a.next
        val inputEol1_l1 = inputEol1_l0b.next

        assertAll(
                { assertEquals(1, input0.line, "Line of an empty CInput should be 1") },
                { assertEquals(1, input1.line, "Line of the first of a single-nonEol-element CInput should be 1") },
                { assertEquals(1, input2.line, "Line of the first of a multi-nonEol-element CInput should be 1") },
                { assertEquals(1, inputEol1_l0a.line, "Line of the first should be 1") },
                { assertEquals(1, inputEol1_l0b.line, "Line of the second 1") },
                { assertEquals(1, inputEol1_l1.line, "Line of the third element should be 1") }
        )
    }

    @DisplayName("CInput.column with no eol detection")
    @Test
    fun getColumn() {
        val input0 = CInput.of(text0.iterator())
        val input1 = CInput.of(text1.iterator())
        val input2 = CInput.of(text2.iterator())

        val inputEol2_l1a = CInput.of(textEol1.iterator())
        val inputEol2_l1b = inputEol2_l1a.next
        val inputEol2_l2 = inputEol2_l1b.next

        assertAll(
                { assertEquals(1, input0.column, "Column of an empty CInput should be 1") },
                { assertEquals(1, input1.column, "Column of the first of a single-nonEol-element CInput should be 1") },
                { assertEquals(1, input2.column, "Column of the first of a multi-nonEol-element CInput should be 1") },
                { assertEquals(1, inputEol2_l1a.column, "Column of the first element should be 1") },
                { assertEquals(2, inputEol2_l1b.column, "Column of the second element should be 2") },
                { assertEquals(3, inputEol2_l2.column, "Column of the third element should be 3") }
        )
    }

}