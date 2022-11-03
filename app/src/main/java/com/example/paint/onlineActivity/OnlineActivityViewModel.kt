package com.example.paint.onlineActivity

import android.app.Application
import android.graphics.Color
import android.graphics.Path
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.paint.DataWrapper
import com.example.paint.PathWrapper
import com.example.paint.blackColor
import com.example.paint.defaultColor
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OnlineActivityViewModel(application: Application) : AndroidViewModel(application) {

    fun concludePath() {
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

    fun loadData(data: List<DataWrapper>) {
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
        }
        concludePath()
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