package com.example.koreankeyboard.views

import android.content.Context
import android.os.Build
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.view.inputmethod.ExtractedTextRequest
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.koreankeyboard.R
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.databinding.LayoutCandidateBinding
import com.example.koreankeyboard.interfaces.CandidateViewButtonOnClick
import com.example.koreankeyboard.interfaces.TranslateCallBack
import com.example.koreankeyboard.services.CustomInputMethodService
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Collections.emptyList

@RequiresApi(Build.VERSION_CODES.N)
class CandidateView
    (context: Context, private val candidateViewButtonOnClick: CandidateViewButtonOnClick) :
    LinearLayout(context) {

    private var binding: LayoutCandidateBinding
    private var service: CustomInputMethodService? = null
    private var suggestions: List<String>? = null

    init {

        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.white),
            ContextCompat.getColor(context, R.color.color2),
            ContextCompat.getColor(context, R.color.color3),
            ContextCompat.getColor(context, R.color.color4),
            ContextCompat.getColor(context, R.color.color5)
        )

        val heights = intArrayOf(20, 24, 18, 23, 16)

        val view = View.inflate(context, R.layout.layout_candidate, this)
        binding = LayoutCandidateBinding.bind(view)

        binding.animSpeak.setColors(colors)
        binding.animSpeak.setBarMaxHeightsInDp(heights)
        binding.animSpeak.setCircleRadiusInDp(2)
        binding.animSpeak.setSpacingInDp(2)
        binding.animSpeak.setIdleStateAmplitudeInDp(2)
        binding.animSpeak.setRotationRadiusInDp(10)
        binding.animSpeak.play()

        binding.btnSpeechInput.setOnClickListener {
            candidateViewButtonOnClick.onClickSpeechInput()
        }
        binding.firstPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
        binding.thirdPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
        binding.secondPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
        binding.btnTranslate.setOnClickListener {
            service?.translate(object : TranslateCallBack {
                override fun isNotDownloaded() {
                    binding.clDownload.visibility = View.VISIBLE
                }
            }
            )
        }

        binding.btnDownload.setOnClickListener {
            if (Misc.checkInternetConnection(context)){

                binding.pbDownload.visibility = View.VISIBLE
                binding.textDownload.visibility = View.INVISIBLE
                binding.btnDownload.visibility = View.INVISIBLE

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.SPANISH)
                    .build()
                val englishSpanishTranslator = Translation.getClient(options)

                englishSpanishTranslator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        Misc.setAToBDownloadStatus(context, true)
                        val optionsNew = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.SPANISH)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build()
                        val spanishToEnglishTranslator = Translation.getClient(optionsNew)
                        spanishToEnglishTranslator.downloadModelIfNeeded()
                            .addOnSuccessListener {
                                Misc.setBToADownloadStatus(context, true)
                                Log.d(Misc.logKey, "spanishToEnglishTranslator")
                                Toast.makeText(context, "Translation loaded, now enjoy.", Toast.LENGTH_SHORT).show()

                                binding.pbDownload.visibility = View.INVISIBLE
                                binding.textDownload.visibility = View.VISIBLE
                                binding.btnDownload.visibility = View.VISIBLE

                                binding.clDownload.visibility = View.GONE
                            }
                            .addOnFailureListener { exception ->
                                exception.printStackTrace()
                            }

                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }

            }else{
                if(!Misc.isAToBDownloaded(context) || !Misc.isAToBDownloaded(context)){
                    Toast.makeText(context, "Please check your internet.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun startVoiceAnim(speechRecognizer: SpeechRecognizer){
        binding.animSpeak.setSpeechRecognizer(speechRecognizer)

        binding.animSpeak.play()
    }

    fun getVoiceAnimView(): com.github.zagum.speechrecognitionview.RecognitionProgressView{
        return binding.animSpeak
    }


    fun setService(listener: CustomInputMethodService) {
        service = listener
    }

    fun changeTranslateIcon(isKoreanSelected: Boolean){
        if(isKoreanSelected){
            binding.btnTranslate.setImageResource(R.drawable.b_to_a)
        }else{
            binding.btnTranslate.setImageResource(R.drawable.a_to_b)
        }
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

    // TODO Refactor context method
    private fun updatePredictions(prediction: List<String>) {
        binding.firstPrediction.text = ""
        binding.firstPrediction.text = if (prediction.isNotEmpty()) {
            binding.firstPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
            prediction[0]
        } else {
            binding.firstPrediction.setOnClickListener {  }
            ""
        }

        binding.secondPrediction.text = ""
        binding.secondPrediction.text = if (prediction.size > 1) {
            binding.secondPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
            prediction[1]
        } else {
            binding.secondPrediction.setOnClickListener { }
            ""
        }

        binding.thirdPrediction.text = ""
        binding.thirdPrediction.text = if (prediction.size > 2) {
            binding.thirdPrediction.setOnClickListener { v -> service?.pickSuggestion((v as TextView).text.toString()) }
            prediction[2]
        }else {
            binding.thirdPrediction.setOnClickListener { }
            ""
        }
    }

    fun enableDisableSpeechAnim(res: Int, isActive: Boolean) {
        binding.btnSpeechInput.setImageResource(res)
        if (isActive) {
            binding.firstPrediction.visibility = View.INVISIBLE
            binding.secondPrediction.visibility = View.INVISIBLE
            binding.thirdPrediction.visibility = View.INVISIBLE
            binding.animSpeak.visibility = View.VISIBLE
        }
        else {
            binding.firstPrediction.visibility = View.VISIBLE
            binding.secondPrediction.visibility = View.VISIBLE
            binding.thirdPrediction.visibility = View.VISIBLE
            binding.animSpeak.visibility = View.GONE
        }
    }


}