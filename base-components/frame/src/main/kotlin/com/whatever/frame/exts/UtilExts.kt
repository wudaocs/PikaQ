package com.whatever.frame.exts

import android.text.TextUtils

fun <T> Any.doSomething(todo: () -> Unit): Unit = todo()

fun <T> Any?.todo(emptyDo: () -> T?, unEmptyDo: () -> T?): T? =
        if (this == null) {
            emptyDo()
        } else {
            unEmptyDo()
        }

@Suppress("UNCHECKED_CAST")
fun <T> T?.nullTo(emptyDo: () -> T): T =
    this ?: emptyDo()

@Suppress("UNCHECKED_CAST")
fun <T> CharSequence?.notnullTo(toDo: () -> T?): T? =
    if (!TextUtils.isEmpty(this)) {
        toDo()
    } else {
        this as T
    }