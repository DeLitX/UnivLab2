package com.delitx.univlab2.parsers

import com.delitx.univlab2.data.Item
import com.delitx.univlab2.ui.adapters.SettingsAdapter
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.InputStream
import java.lang.IllegalArgumentException
import javax.xml.parsers.DocumentBuilderFactory

class DOMParser : ParseStrategy {
    private val mParams = mutableSetOf<String>()
    override fun parse(inputStream: InputStream): List<Item> {
        val result = mutableListOf<Item>()
        val builderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = builderFactory.newDocumentBuilder()
        val doc = docBuilder.parse(inputStream)
        val nList = doc.getElementsByTagName("item")
        for (i in 0 until nList.length) {
            if (nList.item(i).nodeType == Node.ELEMENT_NODE) {
                val element = nList.item(i) as Element
                val value = element.firstChild.nodeValue
                if (value != null) {
                    val temp = decodeParams(value)
                    if (temp.isNotEmpty()) {
                        result.add(Item(temp.toMutableMap()))
                    }
                }
            }
        }
        return result
    }

    override fun getParams(): List<SettingsAdapter.SettingsPair> {
        val result = mutableListOf<SettingsAdapter.SettingsPair>()
        for (i in mParams) {
            result.add(SettingsAdapter.SettingsPair(i, ""))
        }
        return result
    }

    private fun decodeParams(text: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val list = text.split('\n')
        for (i in list) {
            if (i.trim().isEmpty()) {
                continue
            }
            val temp = i.split('=')
            if (temp.size != 2) {
                throw IllegalArgumentException()
            }
            result[temp[0].toLowerCase()] = temp[1]
            mParams.add(temp[0].toLowerCase())
        }
        return result
    }
}