package dev.jamiecraane.betterrecyclerview.extensions

fun <T> List<T>.hasItemFor(index: Int): Boolean = index in 0 until size