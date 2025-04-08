package com.example.restaurentapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.restaurentapp.ViewModels.AuthViewModel
import com.example.restaurentapp.ViewModels.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHome(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Cart", Icons.Default.ShoppingCart),
        NavItem("Profile", Icons.Default.Person)
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant App", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A5F)
                ),
                actions = {
                    // Logout Button
                    IconButton(onClick = {
                        authViewModel.logout {
                            // Handle post-logout actions, like navigation to login page
                            navController.navigate("UserLogin") {
                                popUpTo("UserHome") { inclusive = true }
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
        },
        containerColor = Color.White,
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                tint = Color.Blue
                            )
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                color = Color.Blue
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        ContentScreen(
            modifier = modifier.padding(paddingValues),
            selectedIndex,
            navController,
            authViewModel,
            foodViewModel
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    when (selectedIndex) {
        0 -> HomePage()
        1 -> CartPage()
        2 -> ProfilePage(modifier, navController, authViewModel, foodViewModel)
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)

