package com.jordanhamel.simpleemomtimer

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*
import android.media.ToneGenerator
import android.media.AudioManager
import android.view.View.INVISIBLE
import android.view.View.VISIBLE


class TimerActivity : AppCompatActivity(), View.OnClickListener {

    val APP_NAME = "SimpleEMOMTimer"
    val START_TIME = "start_time"
    var DEFAULT_START_TIME : Long = -1
    var startTime : Long = DEFAULT_START_TIME
    var handler = Handler()
    var grey : Int? = null
    var black : Int? = null
    var tg : ToneGenerator? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        volumeControlStream = AudioManager.STREAM_MUSIC
        button.setOnClickListener(this)
        grey = resources.getColor(R.color.grey)
        black = resources.getColor(android.R.color.black)
    }

    override fun onResume() {
        super.onResume()
        startTime = getSavedStartTime()
        updateUi()
        tg = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    }

    override fun onPause() {
        super.onPause()
        saveStartTime()
        handler.removeCallbacks(null)
        tg?.release()
        tg = null
    }

    override fun onClick(v: View) {
        if (v.id == button.id) {
            if (startTime == DEFAULT_START_TIME) startTimer() else stopTimer()
        }
    }

    private fun startTimer() {
        startTime = Date().time
        updateUi()
    }

    private fun stopTimer() {
        startTime = DEFAULT_START_TIME
        handler.removeCallbacks(null)
        updateUi()
    }

    private fun updateUi() {
        updateClock()
        updateButton()
    }

    private fun updateButton() {
        if (startTime == DEFAULT_START_TIME) {
            button.setText(R.string.start)
            button.setBackgroundResource(R.drawable.button_green)
        } else {
            button.setText(R.string.stop)
            button.setBackgroundResource(R.drawable.button_red)
        }
    }

    private fun updateClock() {
        if (startTime == DEFAULT_START_TIME) {
            clock.text = "0"
            clock.setTextColor(grey!!)
            minutes.visibility = INVISIBLE
        } else {
            val now = Date().time
            val mins: Int = ((now - startTime) / 1000 / 60).toInt()
            val secs: Int = ((now - startTime) / 1000 % 60).toInt()
            clock.text = "$secs"
            clock.setTextColor(black!!)
            when (secs) {
                0 -> if (mins > 0) tg?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 300)
                57, 58, 59 -> tg?.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }
            minutes.visibility = VISIBLE
            minutes.text = getString(R.string.minutes, mins)
            handler.postDelayed({ updateClock() }, 1000)
        }
    }

    private fun saveStartTime() {
        val sp = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        sp.edit().putLong(START_TIME, startTime).apply()
    }

    private fun getSavedStartTime() : Long {
        val sp = getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        return sp.getLong(START_TIME, DEFAULT_START_TIME)
    }
}