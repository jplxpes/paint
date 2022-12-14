package com.example.paint.mainActivity

import android.app.Application
import android.graphics.Color
import android.graphics.Path
import android.provider.Settings
import android.provider.Settings.Secure
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.paint.*
import com.example.paint.onlineActivity.ID
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var online = false

    fun concludePath(forceDisable: Boolean = true) {
        drawingHistory.value!!.add(current)
        drawingDTO.value!!.add(
            DataWrapper(
                currentLineDTO, brushColor.value!!,
                brushSize.value!!.toDouble()
            )
        )
        currentLineDTO = ArrayList()
        current = PathWrapper(Path(), brushColor.value!!, brushSize.value!!)
        drawingHistory.postValue(drawingHistory.value!!)
        drawingDTO.postValue(drawingDTO.value!!)
        if (online && forceDisable) {
            updateDrawState()
        }
    }

    fun switchMode(turnOnLightMode: Boolean) {

        var linesColor = Color.BLACK
        var backgroundColor = Color.WHITE

        if (!turnOnLightMode) {
            linesColor = Color.WHITE
            backgroundColor = Color.BLACK
        }

        canvasColor.postValue(backgroundColor)

        currentColor.postValue(linesColor)
        brushColor.postValue(linesColor)

        drawingHistory.value?.forEach {
            it.color = linesColor
        }
        removedHistory.value?.forEach {
            it.color = linesColor
        }

    }

    fun loadData(data: List<DataWrapper>, forceDisable: Boolean = true) {
        var started = false
        data.forEach {
            current.color = it.color
            current.size = it.size.toFloat()
            val arr = it.path as ArrayList<HashMap<String, Float>>
            var path = Pair(arr.first()["first"], arr.first()["second"])

            current.path.moveTo(path.first!!, path.second!!)
            currentLineDTO.add(Pair(path.first!!, path.second!!))
            removedHistory.postValue(LinkedList())
            removedHistoryDTO.postValue(LinkedList())
            arr.removeFirst()
            arr.forEach { map ->

                path = Pair(map["first"], map["second"])
                if (!started) {
                    started = true

                } else {
                    current.path.lineTo(
                        path.first!!,
                        path.second!!
                    )
                    currentLineDTO.add(
                        Pair(
                            path.first!!,
                            path.second!!
                        )
                    )
                }
            }
            concludePath(forceDisable)
        }
    }

    fun clear() {
        removedHistory.postValue(LinkedList())
        drawingHistory.postValue(LinkedList())
        drawingDTO.postValue(ArrayList())
        removedHistoryDTO.postValue(LinkedList())
        currentLineDTO = ArrayList()
        currentColor.postValue(Color.BLACK)
        brushColor.postValue(Color.BLACK)
        brushSize.postValue(10F)
        current = PathWrapper(Path(), brushColor.value!!, brushSize.value!!)
    }

    fun enableOnline() {
        online = true
    }


    private fun updateDrawState() {
        val curr: ArrayList<DataWrapper>? = drawingDTO.value

        if (curr != null) {
            Firebase.firestore.collection("Online")
                .document("Ref").set(
                    DataWrapperDTO(
                        ID, curr
                    )
                )
        }

    }

    var currentLineDTO: ArrayList<Pair<Float, Float>> = ArrayList()

    var drawingDTO: MutableLiveData<ArrayList<DataWrapper>> =
        MutableLiveData(ArrayList<DataWrapper>())

    var removedHistoryDTO: MutableLiveData<LinkedList<DataWrapper>> = MutableLiveData(LinkedList())

    var canvasColor: MutableLiveData<Int> = MutableLiveData(Color.WHITE)

    var currentColor: MutableLiveData<Int> = MutableLiveData(defaultColor)

    var brushColor: MutableLiveData<Int> = MutableLiveData(blackColor)

    var brushSize: MutableLiveData<Float> = MutableLiveData(10F)

    var drawingHistory: MutableLiveData<LinkedList<PathWrapper>> = MutableLiveData(LinkedList())

    var removedHistory: MutableLiveData<LinkedList<PathWrapper>> = MutableLiveData(LinkedList())

    var current: PathWrapper =
        PathWrapper(Path(), brushColor.value!!, brushSize.value!!)

}