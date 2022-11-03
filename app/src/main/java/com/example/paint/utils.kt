package com.example.paint

import android.graphics.Color
import android.graphics.Path
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.logging.Logger
import kotlin.collections.ArrayList

const val EMPTY_STRING = ""
const val DELAY_TIMER_ACTIVITY: Long = 2000
const val COLOR_TAG = "COLOR"

val logger: Logger = Logger.getLogger("LOGGER")

val blackColor: Int = Color.rgb(0, 0, 0)
val defaultColor: Int = Color.rgb(230, 230, 230)

@Parcelize
data class ColorDTO(
    val red: Int? = null,
    val green: Int? = null,
    val blue: Int? = null
) : Parcelable


data class PathWrapper(val path: Path, var color: Int, var size: Float)

@Parcelize
data class DataWrapper(val path: ArrayList<Pair<Float, Float>>, var color: Int, var size: Double, var key: String = "") :
    Parcelable

data class ReceivedData(val id: String, val data: List<DataWrapper>)
data class DataWrapperDTO(val id: String, val data: List<Any>)

