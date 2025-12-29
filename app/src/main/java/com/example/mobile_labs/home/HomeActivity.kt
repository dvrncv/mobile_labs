package com.example.mobile_labs.home

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mobile_labs.BaseActivity
import android.os.Bundle
import com.example.mobile_labs.ui.theme.Mobile_labsTheme

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mobile_labsTheme {
                HomeScreen()
            }
        }
    }
}