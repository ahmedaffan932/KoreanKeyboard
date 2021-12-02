package com.example.koreankeyboard.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.inputmethodservice.Keyboard
import com.example.koreankeyboard.R

class KeyboardClass(context: Context?, i: Int) :
    Keyboard(context, i) {
    private var mEnterKey: Key? = null

    public override fun createKeyFromXml(
        resources: Resources,
        row: Row,
        i: Int,
        i2: Int,
        xmlResourceParser: XmlResourceParser
    ): Key {
        val latinKey = LatinKey(resources, row, i, i2, xmlResourceParser)
        if (latinKey.codes[0] == 10) {
            mEnterKey = latinKey
        }
        return latinKey
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setImeOptions(resources: Resources, i: Int) {
        val key = mEnterKey
        if (key != null) {
            val i2 = i and 1073742079
            when {
                i2 == 2 -> {
                    key.iconPreview = null
                    mEnterKey!!.icon = null
                    mEnterKey!!.label = resources.getText(R.string.label_go_key)
                }
                i2 == 3 -> {
                    key.icon = resources.getDrawable(R.drawable.sym_keyboard_search_holo_dark)
                    mEnterKey!!.label = null
                }
                i2 == 4 -> {
                    key.iconPreview = null
                    mEnterKey!!.icon = null
                    mEnterKey!!.label = resources.getText(R.string.label_send_key)
                }
                i2 != 5 -> {
                    key.icon = resources.getDrawable(R.drawable.next)
                    mEnterKey!!.label = null
                }
                else -> {
                    key.iconPreview = null
                    mEnterKey!!.icon = null
                    mEnterKey!!.label = resources.getText(R.string.label_next_key)
                }
            }
        }
    }

    internal class LatinKey(
        resources: Resources?,
        row: Row?,
        i: Int,
        i2: Int,
        xmlResourceParser: XmlResourceParser?
    ) :
        Key(resources, row, i, i2, xmlResourceParser) {
        override fun isInside(i: Int, i2: Int): Boolean {
            var i2 = i2
            if (codes[0] == -3) {
                i2 -= 10
            }
            return super.isInside(i, i2)
        }
    }
}
