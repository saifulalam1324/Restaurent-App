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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurentapp.ViewModels.AuthViewModel


@Composable
fun UserSignup(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var isUser by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White) // White background for the layout
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Signup", fontSize = 32.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field with Blue Outline and Blue Label
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email", color = Color(0xFF1565C0)) }, // Blue label
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0), // Blue border color when focused
                unfocusedBorderColor = Color(0xFF1565C0) // Blue border color when not focused
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Field with Blue Outline and Blue Label
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", color = Color(0xFF1565C0)) }, // Blue label
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1565C0), // Blue border color when focused
                unfocusedBorderColor = Color(0xFF1565C0) // Blue border color when not focused
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role Selection with Blue Checkboxes
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isAdmin,
                onCheckedChange = {
                    isAdmin = it
                    if (it) isUser = false // Ensuring only one role is selected
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1565C0), // Blue when checked
                    uncheckedColor = Color(0xFF1565C0) // Blue when unchecked
                )
            )
            Text("Admin")

            Spacer(modifier = Modifier.width(16.dp))

            Checkbox(
                checked = isUser,
                onCheckedChange = {
                    isUser = it
                    if (it) isAdmin = false
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1565C0), // Blue when checked
                    uncheckedColor = Color(0xFF1565C0) // Blue when unchecked
                )
            )
            Text("User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && (isAdmin || isUser)) {
                    authViewModel.signup(email, password, isAdmin, isUser) { success, message ->
                        if (success) {
                            Toast.makeText(context, "Signup Successful!", Toast.LENGTH_SHORT).show()
                            navController.navigate("UserLogin")
                        } else {
                            Toast.makeText(context, message ?: "Signup Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1565C0),
                contentColor = Color.White
            )
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                navController.navigate("UserLogin")
            },
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF1565C0) // Blue color for the TextButton
            )
        ) {
            Text(text = "Already have an account? Login")
        }
    }
}