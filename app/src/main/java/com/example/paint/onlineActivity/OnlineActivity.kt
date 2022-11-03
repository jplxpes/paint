package com.example.paint.onlineActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.example.paint.DataWrapper
import com.example.paint.DataWrapperDTO
import com.example.paint.R
import com.example.paint.ReceivedData
import com.example.paint.databinding.ActivityMainBinding
import com.example.paint.databinding.ActivityOnlineBinding
import com.example.paint.mainActivity.MainActivityViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val ID = UUID.randomUUID().toString()

class OnlineActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private val binding by lazy {
        ActivityOnlineBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.enableOnline()
        subscribeToDrawChanges()
        setContentView(binding.root)

        binding.clearOnline.setOnClickListener {
            Firebase.firestore.collection("Online")
                .document("Ref").delete()
        }

        Firebase.firestore.collection("Online").document("Ref")
            .get()
            .addOnSuccessListener { result ->
                if (result.data != null) {
                    val content = result.data as HashMap<*, *>
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
                    viewModel.clear()
                    viewModel.loadData(objects, false)
                }
            }


    }


    private fun subscribeToDrawChanges(): ListenerRegistration {

        return Firebase.firestore
            .collection("Online")
            .document("Ref")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot?.exists() == true) {

                    if (snapshot.id == ID) return@addSnapshotListener

                    if (snapshot.data is HashMap<*, *>) {
                        val content = snapshot.data as HashMap<*, *>
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
                        viewModel.clear()
                        viewModel.loadData(objects, false)
                    }
                }
            }

    }
}
