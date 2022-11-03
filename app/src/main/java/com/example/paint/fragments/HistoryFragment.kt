package com.example.paint.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.MutableLiveData
import com.example.paint.DataWrapper
import com.example.paint.R
import com.example.paint.ReceivedData
import com.example.paint.mainActivity.MainActivityViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.LinkedList
import kotlin.collections.ArrayList

class HistoryFragment : Fragment() {


    val fetchedData: MutableLiveData<ArrayList<ReceivedData>> = MutableLiveData(ArrayList())
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fetchedData.observe(viewLifecycleOwner) { list ->
            val mLayout = view.findViewById<LinearLayout>(R.id.layout)
            list.forEach {
                val button = Button(activity)
                button.setBackgroundColor(Color.LTGRAY)
                button.text = it.id
                mLayout.addView(button)
                mLayout.addView(TextView(activity))
                button.setOnClickListener { view ->
                    viewModel.clear()
                    viewModel.loadData(it.data)
                    fragmentManager?.commit {
                        replace<com.example.paint.fragments.DrawingBoard>(R.id.fragment)
                        setReorderingAllowed(true)
                    }
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val limit = 100
        Firebase.firestore.collection("History")
            .get()
            .addOnSuccessListener { result ->
                val list = result.take(limit).map {
                    val content = it.data as HashMap<*, *>
                    val list = content["data"]
                    val objects = LinkedList<DataWrapper>()
                    if (list is ArrayList<*>) {
                        list.forEach { obj ->
                            if (obj is HashMap<*, *>) {
                                val color = obj["color"] as Long
                                objects.push(
                                    DataWrapper(
                                        path = obj["path"] as ArrayList<Pair<Float, Float>>,
                                        color = color.toInt(),
                                        size = obj["size"] as Double,
                                    )
                                )
                            }
                        }
                    }
                    ReceivedData(it.id, objects)
                }
                 fetchedData.postValue(list as ArrayList)
            }

        return inflater.inflate(R.layout.fragment_history, container, false)
    }

}