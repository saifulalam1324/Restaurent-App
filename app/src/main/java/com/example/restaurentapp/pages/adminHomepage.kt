package com.example.restaurentapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurentapp.AuthState
import com.example.restaurentapp.AuthViewModel


@Composable
fun adminHomepage(modifier: Modifier = Modifier,navController: NavController,authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("AdminLogin")
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier=Modifier.fillMaxSize().padding(0.dp,16.dp,0.dp,16.dp)) {
            TextButton(onClick = {
            }) {
                Text(text = "Orders", fontSize = 20.sp)
            }
            TextButton(onClick = {
            }) {
                Text(text = "Manu",fontSize = 20.sp)
            }
            Spacer(modifier =Modifier.padding(30.dp))
            TextButton(onClick = {
                authViewModel.signout()
            }) {
                Text(text = "Sign out")
            }
        }

    }

}