package com.example.mobile_labs

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

abstract class BaseActivity : ComponentActivity() {

    protected val TAG: String = this::class.java.simpleName

    private val BASE_TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BASE_TAG, "[$TAG] onCreate()")
    }

    override fun onStart() {
        super.onStart()
        Log.d(BASE_TAG, "[$TAG] onStart()")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(BASE_TAG, "[$TAG] onRestart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(BASE_TAG, "[$TAG] onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(BASE_TAG, "[$TAG] onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(BASE_TAG, "[$TAG] onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(BASE_TAG, "[$TAG] onDestroy()")
    }
}

