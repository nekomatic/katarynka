@file:JvmName("CInput")

package com.nekomatic.katarynka.chars

import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.combinators.orElse
import com.nekomatic.katarynka.core.combinators.then
import com.nekomatic.katarynka.core.combinators.toConst
import com.nekomatic.katarynka.core.input.Input
import com.nekomatic.katarynka.core.input.LineInput
import com.nekomatic.katarynka.core.of

typealias CInput = LineInput<Char>

val CInputEolParser = ParserFactory<Char, Input<Char>>(keepPayload = false).of {
    (it.item('\r') then it.item('\n') toConst 1L)
            .orElse(it.item('\n') toConst 0L)
            .orElse(it.item('\r') toConst 0L)
}
