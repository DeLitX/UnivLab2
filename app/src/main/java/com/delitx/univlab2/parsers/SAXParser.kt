package com.delitx.univlab2.parsers

import com.delitx.univlab2.data.Item
import com.delitx.univlab2.ui.adapters.SettingsAdapter
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.util.*
import javax.xml.parsers.SAXParserFactory

class SAXParser : ParseStrategy {
    private val mParams = mutableSetOf<String>()
    override fun parse(inputStream: InputStream): List<Item> {
        val result = mutableListOf<Item>()
        val parserFactory = SAXParserFactory.newInstance()
        val parser = parserFactory.newSAXParser()
        val itemsStack = Stack<String>()
        val handler = object : DefaultHandler() {
            var currentValue = ""
            var currentElement: Item? = null
            override fun startElement(
                uri: String?,
                localName: String?,
                qName: String?,
                attributes: Attributes?
            ) {
                currentElement = Item(mutableMapOf())
                currentValue = ""
                itemsStack.push(localName)
            }

            override fun endElement(uri: String?, localName: String?, qName: String?) {
                if (localName != itemsStack.pop()) {
                    throw IllegalArgumentException()
                }
                if (currentElement?.values?.isEmpty() == false) {
                    result.add(currentElement!!)
                }
                currentElement = null
            }

            override fun characters(ch: CharArray?, start: Int, length: Int) {
                val text = ch?.let { String(it, start, length) }
                if (text != null && text.trim() != "") {
                    val temp = text.trim().split("=")
                    mParams.add(temp[0].toLowerCase())
                    currentElement?.values?.set(temp[0].toLowerCase(), temp[1])
                }
            }

            override fun endDocument() {
                if (!itemsStack.empty()) {
                    throw IllegalArgumentException()
                }
            }
        }
        parser.parse(inputStream, handler)
        return result
    }

    override fun getParams(): List<SettingsAdapter.SettingsPair> {
        val result = mutableListOf<SettingsAdapter.SettingsPair>()
        for (i in mParams) {
            result.add(SettingsAdapter.SettingsPair(i, ""))
        }
        return result
    }


}