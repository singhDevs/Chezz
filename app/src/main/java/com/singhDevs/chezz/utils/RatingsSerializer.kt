package com.singhDevs.chezz.utils

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.singhDevs.chezz.UserRatingsOuterClass.UserRatings
import java.io.InputStream
import java.io.OutputStream

object RatingsSerializer : Serializer<UserRatings> {
    override val defaultValue: UserRatings = UserRatings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserRatings {
        try {
            return UserRatings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: UserRatings,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.userRatingsDataStore: DataStore<UserRatings> by dataStore(
    fileName = "user_ratings.pb",
    serializer = RatingsSerializer
)