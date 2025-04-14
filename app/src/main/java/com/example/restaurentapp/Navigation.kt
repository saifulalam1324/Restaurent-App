package com.example.restaurentapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.restaurentapp.ViewModels.AuthViewModel
import com.example.restaurentapp.ViewModels.FoodViewModel
import com.example.restaurentapp.pages.AddFood
import com.example.restaurentapp.pages.AdminHome
import com.example.restaurentapp.pages.AdminOrderPanel
import com.example.restaurentapp.pages.AllOrders
import com.example.restaurentapp.pages.PendingOrders
import com.example.restaurentapp.pages.ReadyOrders
import com.example.restaurentapp.pages.UserHome
import com.example.restaurentapp.pages.UserLogin
import com.example.restaurentapp.pages.UserSignup

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val foodViewModel: FoodViewModel = viewModel()
    NavHost(navController = navController, startDestination = "UserLogin") {
        composable("UserLogin") {
            UserLogin(modifier, navController, authViewModel)
        }
        composable("UserSignup") {
            UserSignup(modifier, navController, authViewModel)
        }
        composable("UserHome") {
            UserHome(modifier, navController, authViewModel,foodViewModel)
        }
        composable("AdminHome") {
            AdminHome(modifier, navController, authViewModel, foodViewModel)
        }
        composable("AdminOrderPanel") {
            AdminOrderPanel(modifier, navController, authViewModel, foodViewModel)
        }
        composable("AddFood") {
            AddFood(modifier, navController, authViewModel, foodViewModel)
        }
        composable("PendingOrders") {
            PendingOrders(modifier,foodViewModel)
        }
        composable("ReadyOrders") {
            ReadyOrders(modifier,foodViewModel)
        }
        composable("AllOrders") {
            AllOrders(modifier,foodViewModel)
        }

    }
}
