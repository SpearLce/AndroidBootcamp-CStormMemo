package com.illidancstormrage.utils.database.room.condition

data class Condition(
    val column: String,
    val operator: String,
    val values: List<Any>,
    val conjunction: String = AbstractWrapper.CONJUNCTION_AND
)
