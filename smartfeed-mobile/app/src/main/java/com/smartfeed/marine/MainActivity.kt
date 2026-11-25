package com.smartfeed.marine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smartfeed.marine.screens.tomas.TomasScreen
import com.smartfeed.marine.ui.theme.SmartFeedMarineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartFeedMarineTheme {
                TomasScreen()
            }
        }
    }
}