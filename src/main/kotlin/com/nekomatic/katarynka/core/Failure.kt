package com.nekomatic.katarynka.core

data class Failure<TItem : Any, TIn>(
        val expected: () -> String,
        val startingInput: TIn,
        val remainingInput: TIn
) where TIn : IInput<TItem, TIn>