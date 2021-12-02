package com.example.koreankeyboard.views

import android.view.View
import android.content.Context
import android.widget.TextView
import android.widget.LinearLayout
import com.example.koreankeyboard.R
import com.example.koreankeyboard.interfaces.CandidateViewButtonOnClick
import com.example.koreankeyboard.services.CustomInputMethodService
import kotlinx.android.synthetic.main.layout_candidate.view.*

class CandidateView
    (context: Context, private val candidateViewButtonOnClick: CandidateViewButtonOnClick) :
    LinearLayout(context) {

    private var service: CustomInputMethodService? = null
    private var suggestions: List<String>? = null

    init {
        View.inflate(context, R.layout.layout_candidate, this)
        btnSetting.setOnClickListener { candidateViewButtonOnClick.onClickSettings() }
        btnSpeechInput.setOnClickListener { candidateViewButtonOnClick.onClickSpeechInput() }
        firstPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
        thirdPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
        secondPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
    }

    fun setService(listener: CustomInputMethodService) {
        service = listener
    }

    fun setSuggestions(suggestions: List<String>) {
        clear()
        updatePredictions(suggestions)
        invalidate()
        requestLayout()
    }

    private fun clear() {
        suggestions = emptyList()
        invalidate()
    }

    // TODO Refactor this method
    private fun updatePredictions(prediction: List<String>) {
        firstPrediction.text = ""
        firstPrediction.text = if (prediction.isNotEmpty()) {
            firstPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
            prediction[0]
        } else {
            firstPrediction.setOnClickListener {  }
            ""
        }

        secondPrediction.text = ""
        secondPrediction.text = if (prediction.size > 1) {
            secondPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
            prediction[1]
        } else {
            secondPrediction.setOnClickListener { }
            ""
        }

        thirdPrediction.text = ""
        thirdPrediction.text = if (prediction.size > 2) {
            thirdPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
            prediction[2]
        }else {
            thirdPrediction.setOnClickListener { }
            ""
        }
    }

    fun enableDisableSpeechAnim(res: Int, isActive: Boolean) {
        btnSpeechInput.setImageResource(res)
        if (isActive)
            animSpeak.visibility = View.VISIBLE
        else
            animSpeak.visibility = View.GONE
    }
}