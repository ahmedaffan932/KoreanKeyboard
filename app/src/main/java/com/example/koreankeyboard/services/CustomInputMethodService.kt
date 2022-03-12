package com.example.koreankeyboard.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.media.AudioManager
import android.os.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.method.MetaKeyKeyListener
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.koreankeyboard.*
import com.example.koreankeyboard.classes.Misc
import com.example.koreankeyboard.classes.SuggestionClass
import com.example.koreankeyboard.interfaces.CandidateViewButtonOnClick
import com.example.koreankeyboard.interfaces.TranslateCallBack
import com.example.koreankeyboard.utils.KeyboardClass
import com.example.koreankeyboard.views.CandidateView
import com.example.koreankeyboard.views.CustomKeyboardView
import com.github.kimkevin.hangulparser.HangulParser
import com.github.kimkevin.hangulparser.HangulParserException
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.InputStream
import kotlin.text.isNotEmpty as isNotEmpty1


@RequiresApi(Build.VERSION_CODES.N)
class CustomInputMethodService : InputMethodService(), OnKeyboardActionListener {

    var isSpace = true
    var lastWord = ""
    private var isCaps = true
    var isFirstChar = true
    private var isSoundOn = false
    private var mMetaState: Long = 0
    private var mCompletionOn = false
    private var isVibrationOn = false
    private var isPredictionOn = false
    private var isKorean: Boolean = false
    private var isLarge: Boolean = false
    private var vibrator: Vibrator? = null
    private val mComposing = StringBuilder()
    private var mWordSeparators: String? = null
    private var mDrawableTheme: Drawable? = null
    private lateinit var wordSeparators: Set<Char>
    private var arrSuggestion = ArrayList<String>()
    private lateinit var objSuggestions: JSONObject
    private var candidateView: CandidateView? = null
    private var mInputView: CustomKeyboardView? = null
    private var completions: Array<CompletionInfo>? = null
    private var mInputMethodManager: InputMethodManager? = null
    lateinit var speechRecognizer: SpeechRecognizer
    private val koreanWordArrayList = ArrayList<String>()
    private var koreanWordPreviousLength = 1

    var previousKeyboardLayout: Int = 0
    var temp = 0

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
        R.drawable.bg_kb_gradient_1,
        R.drawable.bg_kb_gradient_2,
        R.drawable.bg_kb_gradient_3,
        R.drawable.bg_kb_gradient_4,
        R.drawable.bg_kb_gradient_5,
        R.drawable.bg_kb_gradient_6,
        R.drawable.bg_kb_gradient_7,
        R.drawable.bg_kb_gradient_8,
        R.drawable.bg_kb_gradient_9,
        R.drawable.bg_kb_gradient_10,
        R.drawable.bg_kb_gradient_11,
        R.drawable.bg_kb_gradient_12,
        R.drawable.bg_kb_gradient_13,
        R.drawable.bg_kb_gradient_14,
        R.drawable.bg_kb_gradient_15,
        R.drawable.bg_kb_gradient_16,
        R.drawable.bg_kb_gradient_17,
        R.drawable.bg_kb_gradient_18,
        R.drawable.bg_kb_gradient_19,
        R.drawable.bg_kb_gradient_20,
        R.drawable.bg_kb_gradient_21
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

        mInputView =
            if (Misc.getIsKeyboardSizeLarge(this)) {
                layoutInflater.inflate(
                    R.layout.keyboard_layout,
                    null as ViewGroup?, true
                ) as CustomKeyboardView?
            } else {
                layoutInflater.inflate(
                    R.layout.keyboard_layout_s,
                    null as ViewGroup?, true
                ) as CustomKeyboardView?
            }

        mInputView?.setOnKeyboardActionListener(this)
        candidateView?.visibility = View.VISIBLE

        loadPreferences()
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
            if (themeId == 0) {
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
            loadPreferences()
        } catch (unused: OutOfMemoryError) {
            unused.printStackTrace()
            loadPreferences()
        }
    }

    override fun onStartInput(editorInfo: EditorInfo, z: Boolean) {
        super.onStartInput(editorInfo, z)
        mComposing.setLength(0)
        mCompletionOn = false
        candidateView?.visibility = View.VISIBLE
        candidateView?.hideExtendedSuggestion()
        updateCandidates()

    }

    override fun onFinishInput() {
        super.onFinishInput()
        mComposing.setLength(0)
        setCandidatesViewShown(false)
        candidateView?.visibility = View.GONE
        candidateView?.hideExtendedSuggestion()
        val e5Var: CustomKeyboardView? = mInputView
        e5Var?.closing()
    }

    override fun onStartInputView(editorInfo: EditorInfo, z: Boolean) {
        super.onStartInputView(editorInfo, z)
        isKorean = Misc.getIskorean(this)
        isLarge = Misc.getIsKeyboardSizeLarge(this)
        if (isKorean) {
            if (isLarge)
                setKeyboard(R.xml.qwertz, true)
            else
                setKeyboard(R.xml.qwertz_s, true)
        } else {
            if (isLarge)
                setKeyboard(R.xml.qwerty_caps, true)
            else
                setKeyboard(R.xml.qwerty_caps_s, true)
        }

        setKbThemes()
        mInputView?.closing()
        loadSuggestions()
        setCandidatesViewShown(true)
        candidateView?.visibility = View.VISIBLE
        candidateView?.hideExtendedSuggestion()

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
        object : Thread() {
            override fun run() {
                val l = Looper.getMainLooper()
                val h = Handler(l)
                h.post {
                    if (isAlphabet(i)) {
                        handleCharacter(i)
                        if (isCaps) {
                            if (isKorean) {
                                if (isLarge)
                                    setKeyboard(R.xml.qwertz, false)
                                else
                                    setKeyboard(R.xml.qwertz_s, false)
                            } else {
                                if (isLarge)
                                    setKeyboard(R.xml.qwerty, false)
                                else
                                    setKeyboard(R.xml.qwerty_s, false)
                            }
                        }
                    } else {
                        when (i) {
                            -5 -> {
                                handleBackspace()
                            }
                            -134 -> {
                                isKorean = false
                                if (isLarge)
                                    setKeyboard(R.xml.qwerty_caps, true)
                                else
                                    setKeyboard(R.xml.qwerty_caps_s, true)

                                loadSuggestions()

                            }
                            -1340 -> {
                                isKorean = true
                                if (isLarge)
                                    setKeyboard(R.xml.qwertz_caps, true)
                                else
                                    setKeyboard(R.xml.qwertz_caps_s, true)
                                loadSuggestions()

                            }
                            -1 -> {
                                setKeyboard(R.xml.symbols_two, false)
                            }
                            -123 -> {
                                if (isKorean) {
                                    isKorean = true
                                    if (isLarge)
                                        setKeyboard(R.xml.qwertz, false)
                                    else
                                        setKeyboard(R.xml.qwertz_s, false)
                                } else {
                                    isKorean = false
                                    if (mComposing.isEmpty()) {
                                        if (isLarge)
                                            setKeyboard(R.xml.qwerty_caps, true)
                                        else
                                            setKeyboard(R.xml.qwerty_caps_s, true)
                                    } else
                                        if (isLarge)
                                            setKeyboard(R.xml.qwerty, false)
                                        else
                                            setKeyboard(R.xml.qwerty_s, false)
                                }
                                loadSuggestions()
                            }
                            -11 -> {
                                isKorean = false
                                if (isLarge)
                                    setKeyboard(R.xml.qwerty_caps_lock, false)
                                else
                                    setKeyboard(R.xml.qwerty_caps_lock_s, false)
                                loadSuggestions()
                            }
                            -110 -> {
                                isKorean = true
                                if (isLarge)
                                    setKeyboard(R.xml.qwertz_caps_lock, false)
                                else
                                    setKeyboard(R.xml.qwertz_caps_lock_s, false)
                                loadSuggestions()
                            }
                            -12 -> {
                                isKorean = false
                                if (isLarge)
                                    setKeyboard(R.xml.qwerty, false)
                                else
                                    setKeyboard(R.xml.qwerty_s, false)
                                loadSuggestions()
                            }
                            -120 -> {
                                isKorean = true
                                if (isLarge)
                                    setKeyboard(R.xml.qwertz, false)
                                else
                                    setKeyboard(R.xml.qwertz_s, false)
                                loadSuggestions()
                            }
                            -15 -> {
                                if (isKorean) {
                                    isKorean = true
                                    if (isCaps)
                                        if (isLarge)
                                            setKeyboard(R.xml.qwertz_caps, isCaps)
                                        else
                                            setKeyboard(R.xml.qwertz_caps_s, isCaps)
                                    else {
                                        if (isLarge)
                                            setKeyboard(R.xml.qwertz, isCaps)
                                        else
                                            setKeyboard(R.xml.qwertz_s, isCaps)
                                    }
                                } else {
                                    isKorean = false
                                    if (isCaps)
                                        if (isLarge)
                                            setKeyboard(R.xml.qwerty_caps, isCaps)
                                        else
                                            setKeyboard(R.xml.qwerty_caps_s, isCaps)
                                    else {
                                        if (isLarge)
                                            setKeyboard(R.xml.qwerty, isCaps)
                                        else
                                            setKeyboard(R.xml.qwerty_s, isCaps)
                                    }
                                }
                                loadSuggestions()
                            }
                            -14 -> {
                                if (isCaps) {
                                    isKorean = true
                                    if (isLarge)
                                        setKeyboard(R.xml.qwertz_caps, true)
                                    else
                                        setKeyboard(R.xml.qwertz_caps_s, true)
                                } else {
                                    isKorean = true
                                    if (isLarge)
                                        setKeyboard(R.xml.qwertz, false)
                                    else
                                        setKeyboard(R.xml.qwertz_s, false)

                                }
                                loadSuggestions()
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
                                val intent = Intent(this@CustomInputMethodService, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                            -105 -> {
                                handleBackspaceKorean()
                            }
                            12345 -> {
                                isKorean = false
                                if (isCaps) {
                                    if (isLarge)
                                        setKeyboard(R.xml.qwerty_caps, true)
                                    else
                                        setKeyboard(R.xml.qwerty_caps_s, true)
                                } else {
                                    if (isLarge)
                                        setKeyboard(R.xml.qwerty, false)
                                    else
                                        setKeyboard(R.xml.qwerty_s, false)
                                }
                                loadSuggestions()
                            }
                            10 -> {
                                val options = currentInputEditorInfo.imeOptions
                                when (options and EditorInfo.IME_MASK_ACTION) {
                                    EditorInfo.IME_ACTION_SEARCH -> sendDefaultEditorAction(true)
                                    EditorInfo.IME_ACTION_GO -> sendDefaultEditorAction(true)
//                                    EditorInfo.IME_ACTION_SEND -> sendDefaultEditorAction(true)
                                    else -> handleCharacter(i)
                                }
                            }
                            else -> {
                                if (isCaps) {
                                    if (isKorean) {
                                        isKorean = true
                                        if (isLarge)
                                            setKeyboard(R.xml.qwertz, false)
                                        else
                                            setKeyboard(R.xml.qwertz_s, false)
                                    } else {
                                        isKorean = false
                                        if (isLarge)
                                            setKeyboard(R.xml.qwerty, false)
                                        else
                                            setKeyboard(R.xml.qwerty_s, false)
                                    }
                                }
                                handleCharacter(i)

                            }
                        }
                    }
                }
            }
        }.start()
        if (isVibrationOn) {
            vibrateOnChars()
        }
        if (isSoundOn) {
            soundOnChars()
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

    private fun setKeyboard(keyboardId: Int, isCaps: Boolean) {
        previousKeyboardLayout = keyboardId

        mInputView?.keyboard = KeyboardClass(this, keyboardId)
        Misc.setIskorean(this, isKorean)

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


    private fun soundOnChars() {
        (getSystemService(getString(R.string.audio)) as AudioManager).playSoundEffect(
            AudioManager.FX_KEYPRESS_STANDARD,
            0.7f
        )
    }

    private fun vibrateOnChars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)); // repeat at index 0
        } else {
            vibrator!!.vibrate(10)
        }
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

    private fun loadPreferences() {
        isSoundOn = Misc.getIsSettingEnable(this, Misc.sound)
        isVibrationOn = Misc.getIsSettingEnable(this, Misc.vibrate)
        isPredictionOn = Misc.getIsSettingEnable(this, Misc.suggestions)

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

    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
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

    private fun startVoiceInput() {
        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(this@CustomInputMethodService)
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
        candidateView?.startVoiceAnim(speechRecognizer)

        candidateView?.getVoiceAnimView()?.setSpeechRecognizer(speechRecognizer)
        candidateView?.getVoiceAnimView()
            ?.setRecognitionListener(object : RecognitionListenerAdapter() {
                override fun onResults(results: Bundle) {
                    val data: ArrayList<String> =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>
                    mComposing.append(data[0])
                    currentInputConnection.setComposingText(mComposing, 1)

                    currentInputConnection.commitText(mComposing, 1)
                    currentInputConnection.commitText(String(Character.toChars(32)), 1)
                    Log.d(Misc.logKey, data[0])
                    candidateView?.enableDisableSpeechAnim(
                        R.drawable.ic_baseline_mic_none_24,
                        false
                    )
                }

                override fun onEndOfSpeech() {
                    candidateView?.enableDisableSpeechAnim(
                        R.drawable.ic_baseline_mic_none_24,
                        false
                    )
                }

                override fun onError(error: Int) {
                    if (isKorean)
                        Toast.makeText(
                            this@CustomInputMethodService,
                            "죄송합니다. 감지된 텍스트가 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(
                            this@CustomInputMethodService,
                            "Sorry, No text detected.",
                            Toast.LENGTH_SHORT
                        ).show()
                    candidateView?.enableDisableSpeechAnim(
                        R.drawable.ic_baseline_mic_none_24,
                        false
                    )
                }
            })
    }


    override fun onDestroy() {
        speechRecognizer.destroy()
        super.onDestroy()
    }
    private fun loadSuggestions() {
        candidateView?.changeTranslateIcon(isKorean)

        object : Thread() {
            override fun run() {
                val l = Looper.getMainLooper()
                val h = Handler(l)
                h.post {
                    if (isKorean) {
                        val `is`: InputStream =
                            resources.openRawResource(R.raw.korean_suggestions)
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
                }
            }
        }.start()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun translate(translateCallBack: TranslateCallBack) {
        try {
            val text =
                currentInputConnection.getExtractedText(ExtractedTextRequest(), 0).text.toString()
            val textList = text.split("\n")
            var isFirstLine = true
            var completeTranslatedText = ""

            for (txt in textList) {
                Log.d(Misc.logKey, txt)
                if (isKorean) {
                    if (Misc.isBToADownloaded(this)) {
                        val optionsNew = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.KOREAN)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build()
                        val spanishToEnglishTranslator = Translation.getClient(optionsNew)
                        spanishToEnglishTranslator.translate(txt)
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Some Error occurred in translation.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnSuccessListener { translatedText ->
                                if (isFirstLine) {
                                    completeTranslatedText = translatedText
                                    isFirstLine = false
                                } else {
                                    completeTranslatedText += "\n" + translatedText
                                }
                            }.await()
                    } else {
                        translateCallBack.isNotDownloaded()
                        Toast.makeText(
                            this,
                            "Translation model is not downloaded, Please download it.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    if (Misc.isAToBDownloaded(this)) {
                        val options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.ENGLISH)
                            .setTargetLanguage(TranslateLanguage.KOREAN)
                            .build()
                        val englishSpanishTranslator = Translation.getClient(options)
                        englishSpanishTranslator.translate(txt)
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this,
                                    "Some Error occurred in translation.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnSuccessListener { translatedText ->
                                if (isFirstLine) {
                                    completeTranslatedText = translatedText
                                    isFirstLine = false
                                } else {
                                    completeTranslatedText += "\n" + translatedText
                                }
                            }.await()
                    } else {
                        translateCallBack.isNotDownloaded()
                        Toast.makeText(
                            this,
                            "Translation model is not downloaded, Please go to app and download it.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            currentInputConnection.deleteSurroundingTextInCodePoints(text.length, 0)
            currentInputConnection.commitText(completeTranslatedText, 1)
            Log.d(Misc.logKey, completeTranslatedText)

        }catch (e: Exception){
            e.printStackTrace()
            Log.d(Misc.logKey, "Input Filed not connected.")
        }
    }

    fun hideKeyboardForSuggestion(bool: Boolean) {
        if (bool) {
            temp = previousKeyboardLayout
            setKeyboard(R.xml.suggestion, false)
        } else
            setKeyboard(temp, isCaps)

    }

}
