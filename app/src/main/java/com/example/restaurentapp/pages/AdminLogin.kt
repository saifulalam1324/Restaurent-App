package com.example.restaurentapp.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurentapp.AuthState
import com.example.restaurentapp.AuthViewModel


@Composable
fun AdminLogin(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(true) }
    var isUser by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                val role = (authState as AuthState.Authenticated).role
                if (role == "Admin") {
                    navController.navigate("adminHomepage")
                } else {
                    navController.navigate("userHomepage")
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(35.dp))
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            Text(text = "Login", fontSize = 20.sp)
            Spacer(modifier = Modifier.padding(6.dp))

            // Role Selection
            Row(
                modifier = Modifier.fillMaxWidth(.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isAdmin,
                        onCheckedChange = {
                            isAdmin = true
                            isUser = false
                        }
                    )
                    Text(text = "Admin")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isUser,
                        onCheckedChange = {
                            isUser = true
                            isAdmin = false
                        }
                    )
                    Text(text = "User")
                }
            }

            Spacer(modifier = Modifier.padding(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") },
                placeholder = { Text(text = "Enter Email") },
                modifier = Modifier.fillMaxWidth(.9f),
                singleLine = true
            )

            Spacer(modifier = Modifier.padding(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                placeholder = { Text(text = "Enter Password") },
                modifier = Modifier.fillMaxWidth(.9f),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                modifier = Modifier.fillMaxWidth(.5f),
                onClick = {
                    authViewModel.login(email, password, isAdmin)

                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        Toast.makeText(context, "Logging in as ${if (isAdmin) "Admin" else "User"}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Fill all the fields", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = authState != AuthState.Loading
            ) {
                Text(text = "Submit")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("AdminSignUp") }) {
                Text(text = "Go to Admin SignUp")
            }
        }
    }
}

