package com.example.restaurentapp.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurentapp.Models.FoodItem
import com.example.restaurentapp.Models.Order
import com.example.restaurentapp.Models.UserModel
import com.example.restaurentapp.ViewModels.AuthViewModel
import com.example.restaurentapp.ViewModels.FoodViewModel

@Composable
fun AddFood(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    var foodName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Burger") }
    var showError by remember { mutableStateOf(false) }
    val categories = listOf("Burger","Sandwich", "Pasta","Wings","Sides","Shakes")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp,0.dp,16.dp,0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add Food Item",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = foodName,
            onValueChange = { foodName = it },
            label = { Text("Food Name") },
            isError = showError && foodName.isBlank(),
            supportingText = {
                if (showError && foodName.isBlank()) {
                    Text("Please enter a food name")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = price,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) {
                    price = it
                }
            },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showError && (price.isBlank() || price.toDoubleOrNull() == null),
            supportingText = {
                if (showError) {
                    when {
                        price.isBlank() -> Text("Please enter a price")
                        price.toDoubleOrNull() == null -> Text("Invalid price format")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select Category:",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column {
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory = category }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                    Text(
                        text = category,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                showError = false
                when {
                    foodName.isBlank() -> showError = true
                    price.isBlank() -> showError = true
                    price.toDoubleOrNull() == null -> showError = true
                    else -> {
                        foodViewModel.addFoodItem(
                            category = selectedCategory,
                            foodName = foodName,
                            price = price.toDouble()
                        )
                        foodName = ""
                        price = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = foodName.isNotBlank() && price.isNotBlank()
        ) {
            Text("Add Food Item")
        }
    }
}

@Composable
fun AdminOrderPanel(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    var orders by remember { mutableStateOf<List<Pair<String, Order>>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodViewModel.fetchAllOrders { fetchedOrders ->
            orders = fetchedOrders
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 130.dp, start = 16.dp, end = 16.dp)) {
            Text(
                text = "🧾Orders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 60.dp, bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(orders) { (orderId, order) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            order.items.forEach { (name, quantity) ->
                                Text(
                                    text = "$name x$quantity",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Total: ${order.totalPrice}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (order.status) "✅ Ready to Serve" else "⌛ Pending",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (order.status) Color(0xFFB2FF59) else Color(0xFFFFCC80)
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                // Confirm Button
                                if (!order.status) {
                                    IconButton(onClick = {
                                        foodViewModel.updateOrderStatusToReady(orderId) {
                                            foodViewModel.fetchAllOrders { updatedOrders ->
                                                orders = updatedOrders
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = "Confirm",
                                            tint = Color.White
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    foodViewModel.deleteOrder(orderId) {
                                        foodViewModel.fetchAllOrders { updatedOrders ->
                                            orders = updatedOrders
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun Users(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    var users by remember { mutableStateOf<List<UserModel>>(emptyList()) }
    var ordersCount by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var pendingOrdersCount by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    LaunchedEffect(Unit) {
        foodViewModel.fetchAllUsers { fetchedUsers ->
            users = fetchedUsers
            fetchedUsers.forEach { user ->
                foodViewModel.getOrderCountForUser(user.userId) { count ->
                    ordersCount = ordersCount + (user.userId to count)
                }
                foodViewModel.getPendingOrderCountForUser(user.userId) { pendingCount ->
                    pendingOrdersCount = pendingOrdersCount + (user.userId to pendingCount)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 130.dp, start = 16.dp, end = 16.dp)) {

            Text(
                text = "👥 Users",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 60.dp, bottom = 16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(users) { user ->
                    val orderCount = ordersCount[user.userId] ?: 0
                    val pendingCount = pendingOrdersCount[user.userId] ?: 0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Email: ${user.email}",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Role: Table Boy",
                                fontSize = 16.sp,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Total Orders Placed: $orderCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Pending Orders: $pendingCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC80)
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Food(viewModel: FoodViewModel = viewModel()) {
    val foodItems = viewModel.foodItems

    LaunchedEffect(Unit) {
        viewModel.loadFoodItems()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 160.dp, 16.dp, 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        foodItems.forEach { (category, items) ->
            item {
                Text(
                    text = category,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(items) { item ->
                FoodItemDeleteCard(item = item, onDelete = { viewModel.deleteFoodItem(it) })
            }
        }
    }
}
@Composable
fun FoodItemDeleteCard(
    item: FoodItem,
    onDelete: (FoodItem) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A5F))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Text(
                    text = "${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onDelete(item) },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete")
            }
        }
    }
}















