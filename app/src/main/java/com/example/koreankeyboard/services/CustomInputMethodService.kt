package com.example.koreankeyboard.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.method.MetaKeyKeyListener
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.koreankeyboard.interfaces.CandidateViewButtonOnClick
import com.example.koreankeyboard.utils.KeyboardClass
import com.example.koreankeyboard.views.CandidateView
import com.example.koreankeyboard.views.CustomKeyboardView
import org.json.JSONObject
import java.io.InputStream
import kotlin.collections.ArrayList
import kotlin.text.isNotEmpty as isNotEmpty1
import android.graphics.BitmapFactory

import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import com.example.koreankeyboard.*
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.classes.SuggestionClass
import com.github.kimkevin.hangulparser.HangulParser
import com.github.kimkevin.hangulparser.HangulParserException
import android.view.inputmethod.ExtractedTextRequest
import android.inputmethodservice.Keyboard





class CustomInputMethodService : InputMethodService(), OnKeyboardActionListener,
    OnSharedPreferenceChangeListener, RecognitionListener {

    var isSpace = true
    var lastWord = ""
    var isFirstChar = true
    private var isCaps = true
    private var isSoundOn = false
    private var isVibrationOn = false
    private var isPredictionOn = false
    private var mMetaState: Long = 0
    private var mCompletionOn = false
    private var isKorean: Boolean = false
    private var vibrator: Vibrator? = null
    private val mComposing = StringBuilder()
    private var mWordSeparators: String? = null
    private var mDrawableTheme: Drawable? = null
    private lateinit var wordSeparators: Set<Char>
    private var arrSuggestion = ArrayList<String>()
    private lateinit var objSuggestions: JSONObject
    private var candidateView: CandidateView? = null
    private var mInputView: CustomKeyboardView? = null
    private val koreanWordArrayList = ArrayList<String>()
    private var completions: Array<CompletionInfo>? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var koreanWordPreviousLength = 1
    var isAgain = false

    private val themes = intArrayOf(
        R.drawable.ic_flag_a1,
        R.drawable.ic_flag_a2,
        R.drawable.ic_flag_a3,
        R.drawable.ic_flag_a4,
        R.drawable.ic_flag_a5,
        R.drawable.ic_flag_a6,
        R.drawable.ic_flag_a7,
        R.drawable.ic_flag_a8,
        R.drawable.ic_flag_a9,
        R.drawable.ic_flag_a10,
        R.drawable.ic_flag_a11,
        R.drawable.ic_flag_a12,
        R.drawable.ic_flag_a13,
        R.drawable.ic_flag_a14,
        R.drawable.ic_flag_a15,
        R.drawable.ic_flag_a16,
        R.drawable.ic_flag_a17,
        R.drawable.ic_flag_a18,
        R.drawable.ic_flag_a19,
        R.drawable.ic_flag_a20,
        R.drawable.ic_flag_a21,
    )

    override fun swipeUp() {}

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate() {
        super.onCreate()

        vibrator = getSystemService(getString(R.string.vibrator)) as Vibrator

        wordSeparators = resources.getString(R.string.word_separators).toSet()
        mWordSeparators = resources.getString(R.string.word_separators)
        mInputMethodManager =
            getSystemService(getString(R.string.input_method)) as InputMethodManager?
    }

    override fun onInitializeInterface() {

    }

    override fun onCreateInputView(): View {

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this)
        defaultSharedPreferences?.let { loadPreferences(it) }

        mInputView = layoutInflater.inflate(
            R.layout.keyboard_layout,
            null as ViewGroup?, true
        ) as CustomKeyboardView?

        mInputView?.setOnKeyboardActionListener(this)

        return mInputView!!
    }


    override fun onComputeInsets(insets: Insets) {
        super.onComputeInsets(insets)
        if (!isFullscreenMode) {
            insets.contentTopInsets = insets.visibleTopInsets
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setKbThemes() {
        try {

            val themeId = Misc.getTheme(this)
            if (themeId == 198) {
                val sharedPreferences =
                    getSharedPreferences(Misc.themeFromGallery, Context.MODE_PRIVATE)
                val str = sharedPreferences.getString(Misc.themeFromGallery, "")
                val decodedString: ByteArray = Base64.decode(str, Base64.DEFAULT)
                val decodedByte =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                val ob = BitmapDrawable(resources, decodedByte)
                mInputView?.background = ob

            } else {
                mDrawableTheme = resources.getDrawable(themes[themeId])
                mInputView?.background = mDrawableTheme
            }
        } catch (unused: OutOfMemoryError) {
            unused.printStackTrace()
        }
    }

    override fun onStartInput(editorInfo: EditorInfo, z: Boolean) {
        super.onStartInput(editorInfo, z)
        mComposing.setLength(0)
        mCompletionOn = false
        updateCandidates()

    }

    override fun onFinishInput() {
        super.onFinishInput()
        mComposing.setLength(0)
        setCandidatesViewShown(false)
        val e5Var: CustomKeyboardView? = mInputView
        e5Var?.closing()
    }

    override fun onStartInputView(editorInfo: EditorInfo, z: Boolean) {
        super.onStartInputView(editorInfo, z)
        isKorean = Misc.getIsKorean(this)
        if (isKorean)
            setKeyboard(R.xml.qwertz, true)
        else
            setKeyboard(R.xml.qwerty_caps, true)

        setKbThemes()
        mInputView?.closing()
        setCandidatesViewShown(true)
    }

    override fun onUpdateSelection(i: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int) {
        super.onUpdateSelection(i, i2, i3, i4, i5, i6)
        if (mComposing.isEmpty()) {
            return
        }
        if (i3 != i6 || i4 != i6) {
            mComposing.setLength(0)
            val currentInputConnection = currentInputConnection
            currentInputConnection?.finishComposingText()
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        val currentInputConnection = currentInputConnection
        if (mComposing.isNotEmpty1() && currentInputConnection != null) {
            currentInputConnection.commitText(mComposing, 1)
        }
        super.onConfigurationChanged(configuration)
    }

    override fun onKeyDown(i: Int, keyEvent: KeyEvent): Boolean {
        var e5Var: CustomKeyboardView? = null
        var currentInputConnection: InputConnection? = null
        if (i != 4) {
            if (i == 66) {
                return false
            }
            if (i != 67) {
                if (i == 62 && keyEvent.metaState and 2 != 0 && getCurrentInputConnection().also {
                        currentInputConnection = it
                    } != null) {
                    currentInputConnection?.clearMetaKeyStates(2)
                    keyDownUp(29)
                    keyDownUp(42)
                    keyDownUp(32)
                    keyDownUp(46)
                    keyDownUp(43)
                    keyDownUp(37)
                    keyDownUp(32)
                    return PROCESS_HARD_KEYS
                }
            } else if (mComposing.isNotEmpty1()) {
                onKey(-5, (null as IntArray?)!!)
                return PROCESS_HARD_KEYS
            }
        } else if (keyEvent.repeatCount == 0 && mInputView?.also {
                e5Var = it
            } != null && e5Var!!.handleBack()) {
            return PROCESS_HARD_KEYS
        }
        return super.onKeyDown(i, keyEvent)
    }

    override fun onKeyUp(i: Int, keyEvent: KeyEvent): Boolean {
        mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, i, keyEvent)
        return super.onKeyUp(i, keyEvent)
    }

    private fun isAlphabet(i: Int): Boolean {
        return if (Character.isLetter(i) || Character.isDigit(i)) {
            PROCESS_HARD_KEYS
        } else false
    }

    private fun keyDownUp(i: Int) {
        currentInputConnection.sendKeyEvent(KeyEvent(0, i))
        currentInputConnection.sendKeyEvent(KeyEvent(1, i))
    }

    override fun onKey(i: Int, iArr: IntArray) {
        when (i) {
            -5 -> {
                handleBackspace()
            }
            -134 -> {
                isKorean = false
                setKeyboard(R.xml.qwerty_caps, true)
            }
            -1340 -> {
                isKorean = true
                setKeyboard(R.xml.qwertz_caps, true)
            }
            -1 -> {
                setKeyboard(R.xml.symbols_two, false)
            }
            -123 -> {
                if (isKorean) {
                    isKorean = true
                    setKeyboard(R.xml.qwertz, false)
                } else {
                    isKorean = false
                    if (mComposing.isEmpty()) {
                        setKeyboard(R.xml.qwerty_caps, true)
                    } else
                        setKeyboard(R.xml.qwerty, false)
                }
            }
            -11 -> {
                isKorean = false
                setKeyboard(R.xml.qwerty_caps_lock, false)
            }
            -110 -> {
                isKorean = true
                setKeyboard(R.xml.qwertz_caps_lock, false)
            }
            -12 -> {
                isKorean = false
                setKeyboard(R.xml.qwerty, false)
            }
            -120 -> {
                isKorean = true
                setKeyboard(R.xml.qwertz, false)
            }
            -15 -> {
                if (isKorean) {
                    isKorean = true
                    if (isCaps)
                        setKeyboard(R.xml.qwertz_caps, isCaps)
                    else {
                        setKeyboard(R.xml.qwertz, isCaps)
                    }
                } else {
                    isKorean = false
                    if (isCaps)
                        setKeyboard(R.xml.qwerty_caps, isCaps)
                    else
                        setKeyboard(R.xml.qwerty, isCaps)
                }
            }
            -14 -> {
                if (isCaps) {
                    isKorean = true
                    setKeyboard(R.xml.qwertz_caps, true)
                } else {
                    isKorean = true
                    setKeyboard(R.xml.qwertz, false)
                }
            }
            -200 -> {
                setKeyboard(R.xml.symbols_one, false)
            }
            -300 -> {
                setKeyboard(R.xml.emoji, false)
            }
            -302 -> {
                setKeyboard(R.xml.emoji1, false)
            }
            -303 -> {
                setKeyboard(R.xml.emoji2, false)
            }
            -304 -> {
                setKeyboard(R.xml.emoji3, false)
            }
            -305 -> {
                setKeyboard(R.xml.emoji4, false)
            }
            -306 -> {
                setKeyboard(R.xml.emoji5, false)
            }
            -307 -> {
                setKeyboard(R.xml.emoji6, false)
            }
            -308 -> {
                setKeyboard(R.xml.emoji7, false)
            }
            -309 -> {
                setKeyboard(R.xml.emoji8, false)
            }
            -310 -> {
                setKeyboard(R.xml.emoji9, false)
            }
            -16 -> {
                setting()
                isCaps = false
            }
            -17 -> {
                val intent = Intent(this, ThemesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            -105 -> {
                handleBackspaceKorean()
            }
            12345 -> {
                isKorean = false
                if (isCaps) {
                    setKeyboard(R.xml.qwerty_caps, true)
                } else
                    setKeyboard(R.xml.qwerty, false)
            }
            10 -> {
                val options = currentInputEditorInfo.imeOptions
                when (options and EditorInfo.IME_MASK_ACTION) {
                    EditorInfo.IME_ACTION_SEARCH -> sendDefaultEditorAction(true)
                    EditorInfo.IME_ACTION_GO -> sendDefaultEditorAction(true)
                    EditorInfo.IME_ACTION_SEND -> sendDefaultEditorAction(true)
                    else -> handleCharacter(i)
                }
            }
            else -> {
                if (isCaps) {
                    if (isKorean) {
                        isKorean = true
                        setKeyboard(R.xml.qwertz, false)
                    } else {
                        isKorean = false
                        setKeyboard(R.xml.qwerty, false)
                    }
                }
                handleCharacter(i)

            }
        }
        if (isVibrationOn) {
            vibrateOnChars()
        }
        if (isSoundOn) {
            soundOnChars()
        }
    }

    private fun setKeyboard(keyboardId: Int, isCaps: Boolean) {
        mInputView?.keyboard = KeyboardClass(this, keyboardId)
        Misc.setIsKorean(this, isKorean)
        if (isKorean) {
            val `is`: InputStream = resources.openRawResource(R.raw.korean_suggestions)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            val json = String(buffer)
            objSuggestions = JSONObject(json)
            arrSuggestion = ArrayList()
            for (word in objSuggestions.keys()) {
                arrSuggestion.add(word)
            }
        } else {

            val `is`: InputStream = resources.openRawResource(R.raw.english_suggestions)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()

            val json = String(buffer)
            objSuggestions = JSONObject(json)
            arrSuggestion = ArrayList()
            for (word in objSuggestions.keys()) {
                arrSuggestion.add(word)
            }

        }

        this.isCaps = isCaps
    }

    private fun setting() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onText(charSequence: CharSequence) {
        val currentInputConnection = currentInputConnection
        if (currentInputConnection != null) {
            currentInputConnection.beginBatchEdit()
            currentInputConnection.commitText(charSequence, 0)
            currentInputConnection.endBatchEdit()
        }
    }

    private fun handleBackspaceKorean() {
        try {
            val currentText: CharSequence =
                currentInputConnection.getExtractedText(ExtractedTextRequest(), 0).text
            val space = 32
            if (currentText[currentText.lastIndex] == space.toChar()) {
                isSpace = true
            }
            if (isSpace) {
                handleBackspace()
                return
            }
            val char = HangulParser.disassemble(currentText[currentText.lastIndex])

            if (char.size == 2) {
                handleBackspaceFor()
                val ch = char[0]
                mComposing.append(ch)
                currentInputConnection.setComposingText(mComposing, 1)

                koreanWordArrayList.clear()
                koreanWordArrayList.add(ch.toString())
                isFirstChar = false
                updateCandidates()
                Log.d(Misc.logKey, "if  $ch")
            } else {
                handleBackspaceFor()
                koreanWordArrayList.clear()
                for (i in 0 until char.size - 1) {
                    koreanWordArrayList.add(char[i].toString())
                }
                val word = HangulParser.assemble(koreanWordArrayList)
                koreanWordPreviousLength = word.length
                mComposing.append(word)
                currentInputConnection.setComposingText(mComposing, 1)
                Log.d(Misc.logKey, "else $word")
            }

        } catch (e: java.lang.Exception) {
            handleBackspace()
        }
    }

    private fun handleBackspace() {
        val length = this.mComposing.length
        when {
            length > 1 -> {
                this.mComposing.delete(length - 1, length)
                currentInputConnection.setComposingText(mComposing, 1)
                updateCandidates()
            }
            length > 0 -> {
                this.mComposing.setLength(0)
                currentInputConnection.commitText("", 0)
                setSuggestions(emptyList())
                updateCandidates()
                isCaps = true
            }
            else -> {
                setSuggestions(emptyList())
                keyDownUp(67)
            }
        }
    }

    private fun handleBackspaceFor() {
        val length = this.mComposing.length
        when {
            length > 1 -> {
                this.mComposing.delete(length - 1, length)
                currentInputConnection.setComposingText(mComposing, 1)
            }
            length > 0 -> {
                this.mComposing.setLength(0)
                currentInputConnection.commitText("", 0)
                setSuggestions(emptyList())
                isCaps = true
            }
            else -> {
                setSuggestions(emptyList())
                keyDownUp(67)
            }
        }
    }

    private fun handleCharacter(ch: Int) {
        Log.d(Misc.logKey, ch.toString())
        if (!isKorean) {
            when {
                isAlphabet(ch) -> {
                    if (isSpace) {
                        isSpace = false
                        lastWord = ""
                    }
                    mComposing.append(ch.toChar())
                    currentInputConnection.setComposingText(mComposing, 1)
                    lastWord += ch.toChar().toString()
                }
                else -> {
                    currentInputConnection.commitText(mComposing, 1)
                    currentInputConnection.commitText(String(Character.toChars(ch)), 1)
                    isSpace = true
                }
            }
            updateCandidates()
        } else {
            when {
                isAlphabet(ch) -> {
                    isSpace = false
                    if (isFirstChar) {
                        mComposing.append(ch.toChar())
                        koreanWordArrayList.add(ch.toChar().toString())
                        currentInputConnection.setComposingText(mComposing, 1)
                        lastWord += ch.toChar().toString()
                        isFirstChar = false
                        updateCandidates()
                        return
                    }

                    try {
                        koreanWordArrayList.add(ch.toChar().toString())
                        val word = HangulParser.assemble(koreanWordArrayList)

                        for (i in 0 until koreanWordPreviousLength) {
                            handleBackspaceFor()
                        }

                        koreanWordPreviousLength = word.length
                        mComposing.append(word)
                        currentInputConnection.setComposingText(mComposing, 1)

                        getKoreanLastWord()
                        updateCandidates()
                    } catch (e: HangulParserException) {
                        koreanWordArrayList.clear()
                        koreanWordPreviousLength = 1
                        lastWord += ch.toChar()
                        mComposing.append(ch.toChar())
                        koreanWordArrayList.add(ch.toChar().toString())
                        currentInputConnection.setComposingText(mComposing, 1)
                        updateCandidates()
                    }
                }
                else -> {
                    currentInputConnection.commitText(mComposing, 1)
                    currentInputConnection.commitText(String(Character.toChars(ch)), 1)
                    getKoreanLastWord()
                    koreanWordPreviousLength = 1
                    isSpace = true
                    isFirstChar = true
                    koreanWordArrayList.clear()
                    updateCandidates()
                }
            }
        }

    }


    private fun isDisassembleAble(char: Char): Boolean {
        return try {
            HangulParser.disassemble(char)
            true
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun isAssembleAble(arr: MutableList<String>): Boolean {
        return try {
            HangulParser.assemble(arr)
            true
        } catch (e: java.lang.Exception) {
            false
        }

    }

    private fun soundOnChars() {
        (getSystemService(getString(R.string.audio)) as AudioManager).playSoundEffect(
            AudioManager.FX_KEY_CLICK,
            0.3f
        )
    }

    private fun vibrateOnChars() {
        vibrator!!.vibrate(10)
    }

    private fun handleClose() {
        requestHideSelf(0)
        mInputView?.closing()
    }

    override fun swipeRight() {}

    override fun swipeLeft() {
        handleBackspace()
    }

    override fun swipeDown() {
        handleClose()
    }

    override fun onPress(i: Int) {
        if (i == -1 || i == -2 || i == -11 || i == -12 || i == -14 || i == -17 || i == -15 || i == 10
            || i == 32 || i == -100 || i == -200 || i == -16 || i == -134 || i == 12345
        ) {
            mInputView?.isPreviewEnabled = false
            return
        }
        try {
            mInputView?.isPreviewEnabled = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRelease(i: Int) {
        mInputView?.isPreviewEnabled = false
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, str: String) {
        loadPreferences(sharedPreferences)
    }

    private fun loadPreferences(sharedPreferences: SharedPreferences) {
        isSoundOn = sharedPreferences.getBoolean("prefSound", PROCESS_HARD_KEYS)
        isVibrationOn = sharedPreferences.getBoolean("prefVibrate", PROCESS_HARD_KEYS)
        isPredictionOn = sharedPreferences.getBoolean("prefPrediction", PROCESS_HARD_KEYS)

        mInputView?.isPreviewEnabled = false
    }

    companion object {
        const val PROCESS_HARD_KEYS = true
    }

    fun pickSuggestion(suggestion: String) {
        val spacePos = mComposing.lastIndexOf(" ")
        if (spacePos > 0) {
            mComposing.delete(spacePos + 1, mComposing.length)
        } else {
            mComposing.setLength(0)
        }
        mComposing.append(suggestion)
        lastWord = suggestion
        isSpace = true
        currentInputConnection.setComposingText(mComposing, 1)

        currentInputConnection.commitText(mComposing, 1)
        currentInputConnection.commitText(String(Character.toChars(32)), 1)
        updateCandidates()
    }

    private fun setSuggestions(suggestions: List<String>) {
        if (suggestions.isNotEmpty()) {
            setCandidatesViewShown(true)
        } else if (isExtractViewShown) {
            setCandidatesViewShown(true)
        }
        if (candidateView != null) {
            candidateView!!.setSuggestions(suggestions)
        }
    }

    override fun onCreateCandidatesView(): View? {
        candidateView = CandidateView(this, object : CandidateViewButtonOnClick {
            override fun onClickSettings() {
                setting()
            }

            override fun onClickSpeechInput() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        val intent =
                            Intent(this@CustomInputMethodService, MicPermissionActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        startVoiceInput()
                    }
                } else {
                    startVoiceInput()
                }
            }
        }).also {
            it.setService(this)
        }

        return candidateView
    }

    override fun onDisplayCompletions(completions: Array<CompletionInfo>?) {
        this.completions = completions
        if (completions == null) {
            setSuggestions(emptyList())
            return
        }

        val stringList = ArrayList<String>()
        for (i in completions.indices) {
            val ci = completions[i]
            stringList.add(ci.text.toString())
        }
        setSuggestions(stringList)
    }

    private fun updateCandidates() {

        if (!isPredictionOn) {
            setSuggestions(emptyList())
            return
        }
        if (mComposing.isNotEmpty1()) {
            val list = getPredictions(mComposing.toString())
            setSuggestions(list)
        } else {
            try {
                val str =
                    if (isKorean) {
                        lastWord
                    } else {
                        lastWord.toLowerCase()
                    }
                val searchedJson = objSuggestions.getJSONObject(str)
                Log.d(Misc.logKey, searchedJson.toString())

                val arrNextSuggestion = ArrayList<SuggestionClass>()
                for (key in searchedJson.keys()) {
                    arrNextSuggestion.add(SuggestionClass(key, searchedJson.getInt(key)))
                }

                arrNextSuggestion.sortByDescending { it.count }
                val arr = ArrayList<String>()
                if (arrNextSuggestion.size > 0)
                    arr.add(arrNextSuggestion[0].word)
                if (arrNextSuggestion.size > 1)
                    arr.add(arrNextSuggestion[1].word)
                if (arrNextSuggestion.size > 2)
                    arr.add(arrNextSuggestion[2].word)
                setSuggestions(arr)
            } catch (e: java.lang.Exception) {
                setSuggestions(emptyList())
                e.printStackTrace()
            }
        }
    }

    private fun getPredictions(str: String): List<String> {
        return arrSuggestion.filter { it.startsWith(str, true) }
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(Misc.logKey, "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(Misc.logKey, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {

    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(Misc.logKey, "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        candidateView?.enableDisableSpeechAnim(R.drawable.ic_baseline_mic_none_24, false)
    }

    override fun onError(error: Int) {
        if (isKorean)
            Toast.makeText(this, "죄송합니다. 감지된 텍스트가 없습니다.", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Sorry, No text detected.", Toast.LENGTH_SHORT).show()
        candidateView?.enableDisableSpeechAnim(R.drawable.ic_baseline_mic_none_24, false)
    }

    override fun onResults(results: Bundle?) {
        val data: ArrayList<String> =
            results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
        mComposing.append(data[0])
        currentInputConnection.setComposingText(mComposing, 1)

        currentInputConnection.commitText(mComposing, 1)
        currentInputConnection.commitText(String(Character.toChars(32)), 1)
        Log.d(Misc.logKey, data[0])
        candidateView?.enableDisableSpeechAnim(R.drawable.ic_baseline_mic_none_24, false)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(Misc.logKey, "partialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(Misc.logKey, "onEvent")
    }

    private fun startVoiceInput() {
        val speechRecognizer: SpeechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(this@CustomInputMethodService)
        speechRecognizer.setRecognitionListener(this@CustomInputMethodService)
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        if (isKorean)
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko")

        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        speechRecognizer.startListening(speechIntent)
        candidateView?.enableDisableSpeechAnim(R.drawable.ic_baseline_mic_active_24, true)
    }

    private fun getKoreanLastWord() {
        val arr =
            currentInputConnection.getExtractedText(ExtractedTextRequest(), 0).text
        val space = 32
        lastWord = ""
        for (i in arr.indices - arr.lastIndex) {
            lastWord += arr[i]
            Log.d(Misc.logKey, "${arr[i]} arr[$i]")
            if (arr[i] == space.toChar()) {
                lastWord = ""
            }
        }
    }

}
