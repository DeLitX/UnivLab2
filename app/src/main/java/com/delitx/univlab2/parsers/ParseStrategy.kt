package com.delitx.univlab2.parsers

import com.delitx.univlab2.data.Item
import com.delitx.univlab2.ui.adapters.SettingsAdapter
import java.io.File
import java.io.InputStream

interface ParseStrategy {
    fun parse(inputStream:InputStream):List<Item>
    fun getParams():List<SettingsAdapter.SettingsPair>
}