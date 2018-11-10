@file:JvmName("Token")

package com.nekomatic.katarynka.chars.combinators

import com.nekomatic.katarynka.chars.CParser
import com.nekomatic.katarynka.chars.WHITESPACES
import com.nekomatic.katarynka.core.combinators.optional
import com.nekomatic.katarynka.core.combinators.surroundedBy

//TODO: create documentation
/**
 *
 * @receiver CParser<A>
 * @return CParser<A>
 */
fun <A : Any> CParser<A>.token(): CParser<A> = this surroundedBy WHITESPACES.optional()
