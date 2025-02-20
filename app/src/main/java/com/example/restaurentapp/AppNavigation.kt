package com.example.restaurentapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.restaurentapp.pages.AddTable
import com.example.restaurentapp.pages.AdminLogin
import com.example.restaurentapp.pages.AdminSignUp
import com.example.restaurentapp.pages.HomePage
import com.example.restaurentapp.pages.LoginPage

@Composable
fun Namvigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val navControler = rememberNavController()
    NavHost(navController = navControler, startDestination = "LoginPage", builder = {
        composable("LoginPage"){
            LoginPage(modifier,navControler,authViewModel)
        }
        composable("AddTable"){
            AddTable(modifier,navControler,authViewModel)
        }
        composable("HomePage"){
             HomePage(modifier,navControler,authViewModel)
        }
        composable("AdminLogin"){
            AdminLogin(modifier,navControler,authViewModel)
        }
        composable("AdminSignUp"){
            AdminSignUp(modifier,navControler,authViewModel)
        }
    })
}