package se.umu.alro0113.thirtygame

import android.os.Parcel
import android.os.Parcelable


/* Stores state for value, selected for rethrowing (gray), and selected for scoring (red) */
class Die(var value : Int = 1) : Parcelable {
    var selectedRed: Boolean = false
    var selectedGray: Boolean = false

    // make sure I understand the lines parcel.readByte() != 0.toByte()
    constructor(parcel: Parcel) : this(parcel.readInt()) {
        selectedRed = parcel.readByte() != 0.toByte()
        selectedGray = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(value)
        parcel.writeByte(if (selectedRed) 1 else 0)
        parcel.writeByte(if (selectedGray) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Die> {
        override fun createFromParcel(parcel: Parcel): Die {
            return Die(parcel)
        }

        override fun newArray(size: Int): Array<Die?> {
            return arrayOfNulls(size)
        }
    }
}

