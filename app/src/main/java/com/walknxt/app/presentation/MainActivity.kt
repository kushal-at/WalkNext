package com.walknxt.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.walknxt.app.WalkNxtApplication
import com.walknxt.app.core.di.AppViewModelFactory
import com.walknxt.app.presentation.navigation.WalkNxtApp
import com.walknxt.app.presentation.theme.WalkNxtTheme

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.walknxt.app.core.utils.SetupHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            SetupHelper.copyAssetsToInternalStorage(applicationContext)
        }
        
        val appContainer = (application as WalkNxtApplication).container
        val viewModelFactory = AppViewModelFactory(appContainer)

        setContent {
            WalkNxtTheme {
                WalkNxtApp(viewModelFactory)
            }
        }
    }
}
