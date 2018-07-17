package com.jakmall.mystopwatch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    @BindView(R.id.txtTime)
    lateinit var txtTime: TextView

    @BindView(R.id.btnStart)
    lateinit var btnStart: Button

    @BindView(R.id.btnStop)
    lateinit var btnStop: Button

    @BindView(R.id.btnReset)
    lateinit var btnReset: Button

    private var startTimeFlag = false
    private var continueTimeFlag = false
    private var continueTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        btnStart.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        btnReset.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            btnStart.id -> {
                if(startTimeFlag) return
                startTimeFlag = true
                Observable.interval(1, TimeUnit.MILLISECONDS)
                        .takeWhile{ _: Long? -> startTimeFlag }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.computation())
                        .subscribe { t: Long ->
                            val time = t + continueTime
                            val hh = TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(time))
                            val mm = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.SECONDS.toMinutes(TimeUnit.MILLISECONDS.toHours(time))
                            val ss = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
                            val sss = TimeUnit.MILLISECONDS.toMillis(time) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(time))
                            if(hh > 24) {
                                this.startTimeFlag = false
                            } else {
                                val stringTime: String = String.format("%02d:%02d:%02d.%03d", hh, mm, ss, sss)
                                txtTime.setText(stringTime)
                                txtTime.setTag(txtTime.id, time)
                            }
                        }
            }
            btnStop.id -> {
                if(!startTimeFlag) return
                startTimeFlag = false
                continueTimeFlag = true
                continueTime = txtTime.getTag(txtTime.id) as Long
            }
            btnReset.id -> {
                if(!startTimeFlag) {
                    txtTime.setText(R.string.reset_time)
                    continueTimeFlag = false
                    continueTime = 0
                }
            }
        }
    }
}
