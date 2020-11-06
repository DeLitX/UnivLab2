package com.delitx.univlab2.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.delitx.univlab2.parsers.ParseStrategy
import com.delitx.univlab2.data.Item
import com.delitx.univlab2.ui.adapters.SettingsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.lang.IllegalArgumentException

class MainViewModel : ViewModel() {
    val liveData = MutableLiveData<List<Item>>()
    val settingsParams = MutableLiveData<List<SettingsAdapter.SettingsPair>>()
    val error = MutableLiveData<Boolean>()
    private var mAllNodesList: MutableList<Item> = mutableListOf()
    var currentStrategy: ParseStrategy? = null
        set(value) {
            field = value
            search(listOf())
        }
    private var mCurrentFile: File? = null
    fun search(list: List<SettingsAdapter.SettingsPair>) {
        CoroutineScope(Default).launch {
            val result = mutableListOf<Item>()
            for (i in mAllNodesList) {
                try {
                    var isFit = true
                    for (t in list) {
                        if (!(i.values[t.key.toLowerCase()]
                                ?: throw IllegalArgumentException()).toLowerCase()
                                .contains(t.value.toLowerCase())
                        ) {
                            isFit = false
                            break
                        }
                    }
                    if (isFit) {
                        result.add(i)
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            liveData.postValue(result)
        }
    }

    fun onFileChoose(inputStream: InputStream) {
        if (currentStrategy != null) {
            try {
                mAllNodesList = currentStrategy!!.parse(inputStream).toMutableList()
                settingsParams.postValue(currentStrategy!!.getParams())
                liveData.postValue(mAllNodesList)
            } catch (e: Exception) {
                error.postValue(true)
            }
        }
    }
}