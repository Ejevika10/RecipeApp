package com.example.lab1

import android.os.Parcel
import android.os.Parcelable
import java.security.Timestamp

class Comment(
    val id: String? = null,
    val itemId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val comment: String? = null,
    val timestamp: String? = null
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(id)
        parcel.writeString(itemId)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(comment)
        parcel.writeString(timestamp)
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}