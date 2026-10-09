package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TapGameViewModel
import com.example.ui.navigation.TapGameApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val tapGameViewModel: TapGameViewModel = viewModel()
            val isDarkTheme by tapGameViewModel.isDarkTheme.collectAsState()
            val themeColor by tapGameViewModel.themeColor.collectAsState()

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                themeColor = themeColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TapGameApp(viewModel = tapGameViewModel)
                }
            }
        }
    }
}
