package io.github.review.core

fun bind(block: () -> Unit): Effect = Effect(block)
