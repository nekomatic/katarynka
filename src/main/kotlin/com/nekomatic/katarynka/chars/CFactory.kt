/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018. nekomatic.
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
 ******************************************************************************/

package com.nekomatic.katarynka.chars

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.combinators.*


open class CFactory(keepPayload: Boolean = true) : ParserFactory<Char, CInput>(keepPayload) {

    object eol

    fun char(c: Char) = this.item(c)
    val WS = this.match("whitespace") { it.isWhitespace() }
    val WSs = (this.WS.oneOrMore() toNamedParser "whitespaces")
    val DIGIT = this.match("digit") { it.isDigit() }
    val LETTER_OR_DIGIT = this.match("letter or digit") { it.isLetterOrDigit() }

    val LOWERCASE_CHAR = this.match("lowercase letter") { it.isLowerCase() }
    val UPPERCASE_CHAR = this.match("uppercase letter") { it.isUpperCase() }
    val LETTER = this.match("letter") { it.isLetter() }
    val EOL = ((char('\r') then char('\n')).toConst(eol)
            orElse (char('\n').toConst(eol))
            orElse (char('\r')).toConst(eol)) toNamedParser "end of line"

    fun string(value: String) = NonEmptyList.fromList(value.toList().map { char(it) })
        .map { it.sequence().sMap { it.joinToString("") } }
        .getOrElse { this.successful().toConst("") }
}

