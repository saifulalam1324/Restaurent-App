package com.example.restaurentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.restaurentapp.pages.LoginPage
import com.example.restaurentapp.ui.theme.RestaurentAppTheme
//cng
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val authViewModel :AuthViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RestaurentAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding->
                    Namvigation(modifier = Modifier.padding(innerPadding),authViewModel=AuthViewModel())
                }
            }
        }
    }
}