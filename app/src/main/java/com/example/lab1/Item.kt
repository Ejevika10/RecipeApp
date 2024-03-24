package com.example.lab1

import android.media.Rating
import android.os.Parcel
import android.os.Parcelable

class Item(val images: String? = null, val diff: String? = null, val id: String? = null, val name: String? = null, val img: String? = null, val desk: String? = null, val price: Int? = null, val time: String? = null, val avgRating: Float? = null, val numOfRatings: Int? = null): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(images)
        parcel.writeString(diff)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(img)
        parcel.writeString(desk)
        if (price != null) {
            parcel.writeInt(price)
        }
        parcel.writeString(time)
        if (avgRating != null) {
            parcel.writeFloat(avgRating)
        }
        if (numOfRatings != null) {
            parcel.writeInt(numOfRatings)
        }
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }


}