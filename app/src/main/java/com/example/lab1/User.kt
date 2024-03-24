package com.example.lab1

import android.os.Parcel
import android.os.Parcelable

class User(
    val name: String? = null,
    val surname: String? = null,
    val gender: Boolean? = null,
    val email:String? = null,
    val birthdate: String? = null,
    val fav_recepie: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val vegeterian: Boolean? = null,
    val skill: Int? = null)
    :Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(surname)
        parcel.writeByte(if (gender == true) 1 else 0)
        parcel.writeString(email)
        parcel.writeString(birthdate)
        parcel.writeString(fav_recepie)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeByte(if (vegeterian == true) 1 else 0)
        if (skill != null) {
            parcel.writeInt(skill)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}