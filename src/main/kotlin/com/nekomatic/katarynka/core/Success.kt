package com.nekomatic.katarynka.core

data class Success<TItem : Any, TIn, A : Any>(
        val value: A,
        val startingInput: TIn,
        val remainingInput: TIn,
        val payload: () -> List<TItem>
) where TIn : IInput<TItem, TIn>