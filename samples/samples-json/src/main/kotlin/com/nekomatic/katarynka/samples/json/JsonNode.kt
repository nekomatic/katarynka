package com.nekomatic.katarynka.samples.json

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


@ExperimentalUnsignedTypes
object JsonParser : ForceFailParser<Char, CInput, JsonNode>({ "json" }) {
    private val JNode = RefParser<Char, CInput, JsonNode>()

    private val hexDigitAsInt = AnyParser<Char, CInput> { "hex digit" }
            .map { listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f').indexOf(it) }
            .onlyIf { it > -1 }
    private val fourDigitHex = hexDigitAsInt times 4u map { it[3] + it[2] * 16 + (it[1] + it[0] * 16) * 256 }


    private val jNull by lazy { PString("null") map { NullNode } }
    private val jTrue by lazy { PString("true") map { TrueNode } }
    private val jFalse by lazy { PString("false") map { FalseNode } }
    private val jBool by lazy { jTrue.map { it as BoolNode } orElse jFalse.map { it as BoolNode } }

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

    private val jString = (PString("\"\"").toConst(StringNode(""))) orElse
            (jStringChar.oneOrMore().surroundedBy(quote).map { StringNode(it.joinToString("")) })

    private val jNumDiscreete = zero toConst listOf('0') orElse nonZero.zeroOrMore()
    private val jNumDotpart = DIGIT.oneOrMore().prefixedBy(PChar('.')) map { listOf('.') + it }


    private val emptyArray = (lSqBr then rSqBr).toConst(ArrayNode(listOf()) as JsonNode)
    private val nonEmptyArray = (JNode listWithSeparator coma) suffixedBy rSqBr prefixedBy lSqBr map { ArrayNode(it) as JsonNode }


    private val jArrayParser = nonEmptyArray orElse emptyArray

    private val jProperty = (jString suffixedBy colon) then JNode map { PropertyNode(name = it.a.value, value = it.b) }
    private val emptyObject = (lCrBr then rCrBr).toConst(ObjectNode(listOf()) as JsonNode)
    private val nonEmptyObject = (jProperty listWithSeparator coma).suffixedBy(rCrBr) prefixedBy lCrBr map { ObjectNode(it) as JsonNode }
    private val JObjectParser = nonEmptyObject orElse emptyObject

    private val JValue: Parser<Char, LineInput<Char>, JsonNode> = jBool.map { it as JsonNode } orElse jNull.map { it as JsonNode } orElse jString.map { it as JsonNode }

    val jnode = JNode.set(JValue orElse jArrayParser orElse JObjectParser)

    override fun parse(input: CInput): parserResult<Char, CInput, out JsonNode> {
        return jnode.parse(input)
    }

    override suspend fun parseAsync(input: CInput): parserResult<Char, CInput, out JsonNode> {
        return jnode.parseAsync(input)
    }
}