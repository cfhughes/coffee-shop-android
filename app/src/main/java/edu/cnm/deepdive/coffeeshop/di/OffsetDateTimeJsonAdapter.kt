package edu.cnm.deepdive.coffeeshop.di

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.OffsetDateTime

class OffsetDateTimeJsonAdapter {

    @ToJson
    fun toJson(value: OffsetDateTime): String = value.toString()

    @FromJson
    fun fromJson(value: String): OffsetDateTime = OffsetDateTime.parse(value)
}
