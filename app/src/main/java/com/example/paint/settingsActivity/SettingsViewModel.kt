package com.example.paint.settingsActivity

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.paint.ColorDTO
import kotlinx.parcelize.Parcelize

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    var currentColor: MutableLiveData<ColorDTO> = MutableLiveData<ColorDTO>(ColorDTO(0, 0, 0))

    fun setColor(color: ColorDTO) {
        currentColor.postValue(
            ColorDTO(
                color.red ?: currentColor.value!!.red,
                color.green ?: currentColor.value!!.green,
                color.blue ?: currentColor.value!!.blue,
                )
        )
    }


}