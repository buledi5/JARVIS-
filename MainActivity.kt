package com.example.jarvisai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView
    private var ttsReady = false
    private val micRequest = 201

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildUi()
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micRequest)
        }
    }

    private fun buildUi() {
        val base = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 36, 28, 24)
            setBackgroundColor(0xFF07111F.toInt())
        }
        val scroll = ScrollView(this)
        val title = TextView(this).apply {
            text = "J.A.R.V.I.S"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(0xFF58D8FF.toInt())
        }
        val subtitle = TextView(this).apply {
            text = "PERSONAL AI ASSISTANT"
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(0xFFB9D7E5.toInt())
            setPadding(0, 4, 0, 26)
        }
        status = TextView(this).apply {
            text = "Ready, sir. Tap Speak and give a command."
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(8, 18, 8, 24)
        }
        base.addView(title, matchWrap())
        base.addView(subtitle, matchWrap())
        base.addView(status, matchWrap())
        addButton(base, "🎙  SPEAK TO JARVIS") { listen() }
        addButton(base, "▶  YOUTUBE MUSIC") { openYoutube("popular songs") }
        addButton(base, "⌨  CODING HELP") { openUrl("https://chatgpt.com/") }
        addButton(base, "⚙  PHONE SETTINGS") {
            runCatching { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
                .onFailure { say("I could not open Settings.") }
        }
        addButton(base, "ℹ  HELP / COMMANDS") {
            status.text = "Try: YouTube kholo; Atif Aslam songs chalao; coding help; Settings kholo; ChatGPT kholo."
            say("You can ask me to open YouTube, search for a song, open Settings, or get coding help.")
        }
        scroll.addView(base)
        setContentView(scroll)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
    )

    private fun addButton(parent: LinearLayout, label: String, action: () -> Unit) {
        val b = Button(this).apply {
            text = label
            textSize = 14f
            isAllCaps = false
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF123B57.toInt())
            setPadding(10, 16, 10, 16)
            setOnClickListener { action() }
        }
        val lp = matchWrap()
        lp.setMargins(0, 7, 0, 7)
        parent.addView(b, lp)
    }

    private fun listen() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), micRequest)
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-PK")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-PK")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS")
        }
        try {
            startActivityForResult(intent, 101)
        } catch (_: Exception) {
            status.text = "Speech recognition is unavailable. Check Google app and internet."
            say("Speech recognition is not available on this phone.")
        }
    }

    @Deprecated("Deprecated by Android, kept for simple starter compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            val phrase = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            status.text = "You: $phrase"
            handleCommand(phrase)
        }
    }

    private fun handleCommand(raw: String) {
        val cmd = raw.lowercase(Locale.ROOT)
        when {
            cmd.contains("youtube") && (cmd.contains("song") || cmd.contains("music") || cmd.contains("gana") || cmd.contains("gaana") || cmd.contains("chala")) -> {
                val query = raw.replace(Regex("(?i)jarvis|youtube|par|per|gana|gaana|chalao|chala|play|music|song|songs"), " ").trim()
                    .ifBlank { "popular songs" }
                openYoutube(query)
                say("Opening YouTube search for $query, sir.")
            }
            cmd.contains("youtube") || cmd.contains("gana") || cmd.contains("gaana") -> {
                openYoutube("popular songs")
                say("Opening YouTube, sir.")
            }
            cmd.contains("coding") || cmd.contains("code") || cmd.contains("program") -> {
                openUrl("https://chatgpt.com/")
                say("Opening coding help, sir.")
            }
            cmd.contains("chatgpt") || cmd.contains("ai assistant") -> {
                openUrl("https://chatgpt.com/")
                say("Opening ChatGPT, sir.")
            }
            cmd.contains("setting") -> {
                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                say("Opening Settings, sir.")
            }
            cmd.contains("hello") || cmd.contains("salam") -> say("Hello, sir. I am ready.")
            else -> {
                status.text = "I heard: $raw\nTry: YouTube song, coding help, or Settings."
                say("I heard you, sir. Please try YouTube, coding help, or Settings.")
            }
        }
    }

    private fun openYoutube(query: String) {
        val uri = Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query))
        openUrl(uri.toString())
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {
            status.text = "Could not open the requested app or link."
        }
    }

    override fun onInit(statusCode: Int) {
        if (statusCode == TextToSpeech.SUCCESS) {
            ttsReady = true
            val result = tts.setLanguage(Locale("en", "US"))
            // Select an installed male voice in Android's Text-to-speech settings if one is available.
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.language = Locale.getDefault()
            }
        }
    }

    private fun say(message: String) {
        if (ttsReady) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "jarvis-utterance")
        status.text = message
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
