package com.example.notes.utils

/**
 * This is generic click interface mainly used when adapter item clicked
 */
interface GenericClickListener<T> {

    /**
     * This method will invoke with clicked object.
     */
    fun onClick(item: T)
}