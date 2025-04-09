package com.example.restaurentapp.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurentapp.ViewModels.AuthViewModel
import com.example.restaurentapp.ViewModels.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHome(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Restaurant App", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E3A5F)
                    ),
                    actions = {
                        IconButton(onClick = {
                            authViewModel.logout {
                                navController.navigate("UserLogin") {
                                    popUpTo("AdminHome") { inclusive = true }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp, // Logout Icon
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TopNavItem("Orders", Icons.Default.List, selectedIndex == 0) {
                        selectedIndex = 0
                    }
                    TopNavItem("Users", Icons.Default.Group, selectedIndex == 1) {
                        selectedIndex = 1
                    }
                    TopNavItem("Add Food", Icons.Default.Add, selectedIndex == 2) {
                        selectedIndex = 2
                    }
                    TopNavItem("Foods", Icons.Default.Fastfood, selectedIndex == 3) {
                        selectedIndex = 3
                    }
                    TopNavItem("Add Users", Icons.Default.Add, selectedIndex == 4) {
                        selectedIndex = 4
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        AdminContentScreen(
            modifier = modifier.padding(paddingValues),
            selectedIndex,
            navController,
            authViewModel,
            foodViewModel
        )
    }
}

@Composable
fun TopNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF1565C0) else Color.Gray
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) Color(0xFF1565C0) else Color.Gray
        )
    }
}

@Composable
fun AdminContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    when (selectedIndex) {
        0 -> AdminOrderPanel(modifier, navController, authViewModel, foodViewModel)
        1 -> Users(modifier,navController,authViewModel,foodViewModel)
        2 -> AddFood(modifier, navController, authViewModel, foodViewModel)
        3 -> Food()
        4 -> UserSignup(modifier,navController,authViewModel)
    }
}


