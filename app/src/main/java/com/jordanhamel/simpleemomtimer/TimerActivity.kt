package com.jordanhamel.simpleemomtimer

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*

class TimerActivity : AppCompatActivity(), View.OnClickListener {

    val APP_NAME = "SimpleEMOMTimer"
    val START_TIME = "start_time"
    var DEFAULT_START_TIME : Long = -1
    var startTime : Long = DEFAULT_START_TIME
    var handler = Handler()
    var grey : Int? = null
    var black : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        button.setOnClickListener(this)
        grey = resources.getColor(R.color.grey)
        black = resources.getColor(android.R.color.black)
    }

    override fun onResume() {
        super.onResume()
        startTime = getSavedStartTime()
        updateUi()
    }

    override fun onPause() {
        super.onPause()
        saveStartTime()
        handler.removeCallbacks(null)
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
            button.setBackgroundResource(android.R.color.holo_green_dark)
        } else {
            button.setText(R.string.stop)
            button.setBackgroundResource(android.R.color.holo_red_dark)
        }
    }

    private fun updateClock() {
        if (startTime == DEFAULT_START_TIME) {
            clock.text = "0"
            clock.setTextColor(grey!!)
        } else {
            val now = Date().time
            val secs: Int = ((now - startTime) / 1000 % 60).toInt()
            clock.text = "$secs"
            clock.setTextColor(black!!)
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