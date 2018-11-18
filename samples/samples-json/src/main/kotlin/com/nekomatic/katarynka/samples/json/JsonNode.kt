/*******************************************************************************
 * The MIT License
 *
 * Copyright (c)  2018.  nekomatic.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/

package com.nekomatic.katarynka.samples.json

import arrow.core.None
import arrow.core.Some
import com.nekomatic.katarynka.arrow.flatten
import com.nekomatic.katarynka.chars.CInput
import com.nekomatic.katarynka.chars.DIGIT
import com.nekomatic.katarynka.chars.combinators.token
import com.nekomatic.katarynka.chars.parsers.PChar
import com.nekomatic.katarynka.chars.parsers.PString
import com.nekomatic.katarynka.core.combinators.*
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.parserResult
import com.nekomatic.katarynka.core.parsers.AnyParser
import com.nekomatic.katarynka.core.parsers.ForceFailParser
import com.nekomatic.katarynka.core.parsers.Parser
import com.nekomatic.katarynka.core.parsers.RefParser
import com.nekomatic.katarynka.core.combinators.onlyIf


sealed class JsonNode
object NullNode : JsonNode()
sealed class BoolNode : JsonNode()
object TrueNode : BoolNode()
object FalseNode : BoolNode()
data class NumberNode(val value: Double) : JsonNode()
data class StringNode(val value: kotlin.String) : JsonNode()
data class ArrayNode(val values: List<JsonNode>) : JsonNode()
data class ObjectNode(val values: List<PropertyNode>) : JsonNode()
data class PropertyNode(val name: String, val value: JsonNode)

//TODO: complete json number implementation
@ExperimentalUnsignedTypes
object JsonParser : ForceFailParser<Char, CInput, JsonNode>("json") {
    private val JNode = RefParser<Char, CInput, JsonNode>()

    private val hexDigitAsInt = AnyParser<Char, CInput>("hex digit")
            .map { listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f').indexOf(it) }
            .onlyIf { it > -1 }
    private val fourDigitHex = hexDigitAsInt times 4u map { it[3] + it[2] * 16 + (it[1] + it[0] * 16) * 256 }

    //TODO: create tests
    internal val jNull by lazy { PString("null") map { NullNode } toNamedParser "null" }
    private val jTrue by lazy { PString("true") map { TrueNode } }
    private val jFalse by lazy { PString("false") map { FalseNode } }
    //TODO: create tests
    internal val jBool by lazy { jTrue.map { it as BoolNode } orElse jFalse.map { it as BoolNode } toNamedParser "bool" }

    private val lSqBr by lazy { PChar('[').token() }
    private val rSqBr by lazy { PChar(']').token() }
    private val lCrBr by lazy { PChar('{').token() }
    private val rCrBr by lazy { PChar('}').token() }
    private val coma by lazy { PChar(',').token() }
    private val colon by lazy { PChar(':').token() }
    private val quote by lazy { PChar('"') }
    private val backSl by lazy { PChar('\\') }

    private val strUTF = fourDigitHex map { it.toChar() } prefixedBy PString("\\u")
    private val strQ = quote prefixedBy backSl toConst ('"')
    private val strBS = PChar('\\') prefixedBy backSl toConst ('\\')
    private val strFS = PChar('/') prefixedBy backSl toConst ('/')
    private val strBSP = PChar('b') prefixedBy backSl toConst ('\b')
    private val strFF = PChar('f') prefixedBy backSl toConst (0x000C.toChar())
    private val strNL = PChar('n') prefixedBy backSl toConst ('\n')
    private val strRT = PChar('r') prefixedBy backSl toConst ('\r')
    private val strT = PChar('t') prefixedBy backSl toConst ('\t')
    private val zero = PChar('0')
    private val nonZero = DIGIT onlyIf { it - '0' > 0 }

    private val escaped by lazy { strUTF orElse strQ orElse strBS orElse strFS orElse strBSP orElse strFF orElse strNL orElse strRT orElse strT }
    private val jStringChar = escaped orElse (quote orElse backSl).not()

    //TODO: create tests
    internal val jString = (PString("\"\"").toConst(StringNode(""))) orElse
            (jStringChar.oneOrMore().surroundedBy(quote).map { StringNode(it.joinToString("")) }) toNamedParser "string"


    private val jNumDiscreete = zero toConst listOf('0') orElse nonZero.zeroOrMore()
    private val jNumDotpart = DIGIT.oneOrMore().prefixedBy(PChar('.')) map { listOf('.') + it }
    private val jNumEPart = (PChar('+') orElse PChar('-')).optional() then (PChar('e') orElse PChar('E')) then (DIGIT).oneOrMore() map { it.flatten() } map
            {
                (if (it.a is Some) listOf((it.a as Some<Char>).t)
                else listOf()) +
                        listOf(it.b) + it.c
            }

    private val doubleOption by lazy {
        (PChar('-').optional() then jNumDiscreete then jNumDotpart.optional() then jNumEPart.optional())
                .map {
                    val f = it.flatten()
                    val sign = if (f.a is Some) listOf((f.a as Some<Char>).t) else listOf()
                    val disc = f.b
                    val dec = if (f.c is Some) (f.c as Some<List<Char>>).t else listOf()
                    val e = if (f.d is Some) (f.d as Some<List<Char>>).t else listOf()
                    val value = (sign + disc + dec + e).joinToString("").toDoubleOrNull()
                    if (value == null) None else Some(value)
                }
    }

    //TODO: create tests
    internal val jNumber = doubleOption.onlyIfSome() map { NumberNode(it) } toNamedParser "number"

    private val emptyArray = (lSqBr then rSqBr).toConst(ArrayNode(listOf()) as JsonNode)
    private val nonEmptyArray = (JNode listWithSeparator coma) suffixedBy rSqBr prefixedBy lSqBr map { ArrayNode(it) as JsonNode }

    //TODO: create tests
    internal val jArrayParser = nonEmptyArray orElse emptyArray //toNamedParser "array"

    private val jProperty = (jString suffixedBy colon) then JNode map { PropertyNode(name = it.a.value, value = it.b) }
    private val emptyObject = (lCrBr then rCrBr).toConst(ObjectNode(listOf()) as JsonNode)
    private val nonEmptyObject = (jProperty listWithSeparator coma).suffixedBy(rCrBr) prefixedBy lCrBr map { ObjectNode(it) as JsonNode }

    //TODO: create tests
    internal val JObjectParser = nonEmptyObject //orElse emptyObject // toNamedParser "object"

    //TODO: create tests
    internal val JValue: Parser<Char, LineInput<Char>, JsonNode> =
            jBool.map { it as JsonNode } orElse jNull.map { it as JsonNode } orElse jString.map { it as JsonNode } //orElse jNumber.map { it as JsonNode }
    //TODO: create tests
    internal val jnode = JNode.set(JValue orElse jArrayParser orElse JObjectParser)

    fun parseObject(input: CInput): parserResult<Char, CInput, out JsonNode> {
        return JObjectParser.parse(input)
    }

    override fun parse(input: CInput): parserResult<Char, CInput, out JsonNode> {
        return jnode.parse(input)
    }

    override suspend fun parseAsync(input: CInput): parserResult<Char, CInput, out JsonNode> {
        return jnode.parseAsync(input)
    }
}