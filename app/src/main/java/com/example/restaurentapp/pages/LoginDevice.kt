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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.restaurentapp.AuthViewModel

@Composable
fun LoginPage(modifier: Modifier = Modifier,navController: NavController,authViewModel: AuthViewModel) {

    var TableCode by remember { mutableStateOf("") }
    var TablePassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    Row (modifier=Modifier.fillMaxWidth().padding(vertical = 60.dp, horizontal = 11.dp)
        ,horizontalArrangement = Arrangement.End
    ){
        Button(onClick = {
            navController.navigate("AdminLogin")
        }) {
            Icon(
                imageVector = Icons.Default.Person, // Built-in icon
                contentDescription = "Admin Icon"
            )
        }

    }
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp).clip(RoundedCornerShape(16.dp)).background(Color.LightGray) ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier =Modifier.padding(16.dp))
            Text(text = "Set Table", fontSize = 20.sp)
            Spacer(modifier =Modifier.padding(6.dp))
            OutlinedTextField(
                value = TableCode,
                onValueChange = {
                    TableCode = it
                },
                label = {
                    Text(text = "Table Code")
                },
                placeholder = { Text(text = "Enter Table Code") },
                modifier = Modifier.fillMaxWidth(.9f),singleLine = true
            )
            Spacer(modifier =Modifier.padding(6.dp))
            OutlinedTextField(
                value = TablePassword,
                onValueChange = {
                    TablePassword = it
                },
                label = {
                    Text(text = "Table Password")
                },
                placeholder = { Text(text = "Enter Table Password") },
                modifier = Modifier.fillMaxWidth(.9f),singleLine = true
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(modifier = Modifier.fillMaxWidth(.5f)
                ,onClick = {

                    if(TableCode.isNotEmpty() && TablePassword.isNotEmpty()){
                        Toast.makeText(context,"$TableCode Sated", Toast.LENGTH_LONG).show()
                        navController.navigate("HomePage")
                    }else{
                        Toast.makeText(context,"fill all the field", Toast.LENGTH_LONG).show()
                    }

                }) {
                Text(text = "submit",
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }


}
