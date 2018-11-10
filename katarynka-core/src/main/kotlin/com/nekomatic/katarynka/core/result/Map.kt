package com.nekomatic.katarynka.core.result

import com.nekomatic.katarynka.core.input.IInput

//TODO: create documentation
/**
 *
 * @receiver Success<TItem, TIn, A>
 * @param f (A) -> B
 * @return Success<TItem, TIn, B>
 */
fun <TItem : Any, TIn, A : Any, B : Any> Success<TItem, TIn, A>.map(f: (A) -> B) where TIn : IInput<TItem, TIn> =
        Success(
                value = f(this.value),
                startingInput = this.startingInput,
                remainingInput = this.remainingInput,
                payload = this.payload
        )