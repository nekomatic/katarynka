package com.nekomatic.katarynka.bytes

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import com.nekomatic.katarynka.core.ParserFactory
import com.nekomatic.katarynka.core.combinators.sMap
import com.nekomatic.katarynka.core.combinators.sequence
import com.nekomatic.katarynka.core.combinators.toConst

class BFactory : ParserFactory<Byte, BInput>(keepPayload = false) {
    fun byte(b: Byte) = this.item(b)
    fun bytes(value: ByteArray) = NonEmptyList.fromList(value.toList().map { byte(it) })
        .map { it.sequence().sMap { it.toByteArray() } }
        .getOrElse { this.successful().toConst(ByteArray(0)) }
}