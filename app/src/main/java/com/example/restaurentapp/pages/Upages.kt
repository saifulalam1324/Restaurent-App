package com.example.restaurentapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.restaurentapp.Models.FoodItem
import com.example.restaurentapp.Models.Order
import com.example.restaurentapp.ViewModels.AuthViewModel
import com.example.restaurentapp.ViewModels.FoodViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: FoodViewModel = viewModel()) {
    val foodItems = viewModel.foodItems
    LaunchedEffect(Unit) {
        viewModel.loadFoodItems()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 100.dp, 16.dp, 170.dp),
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
                FoodItemCard(item = item, onAddToCart = { viewModel.addToCart(it) })
            }
        }
    }
}
@Composable
fun FoodItemCard(item: FoodItem, onAddToCart: (FoodItem) -> Unit) {
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
                onClick = { onAddToCart(item) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1565C0),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Add to Cart",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Cart")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartPage(viewModel: FoodViewModel = viewModel()) {
    val cartItems = viewModel.cartItems
    val totalPrice = viewModel.getCartTotal()

    Scaffold(
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 16.dp, 150.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ${"%.2f".format(totalPrice)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Button(
                        onClick = { viewModel.placeOrder() },
                        enabled = cartItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.LightGray
                        )
                    ) {
                        Text("Order")
                    }
                }
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Your cart is empty", color = Color.Black)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 80.dp, 16.dp, 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems) { cartItem ->
                        CartItemCard(
                            item = cartItem.foodItem,
                            quantity = cartItem.quantity,
                            onIncrease = { viewModel.increaseQuantity(cartItem.foodItem.id) },
                            onDecrease = { viewModel.decreaseQuantity(cartItem.foodItem.id) },
                            onRemove = { viewModel.removeFromCart(cartItem.foodItem.id) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CartItemCard(
    item: FoodItem,
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A5F))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.9f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrease) {
                    Icon(
                        imageVector = Icons.Filled.RemoveCircle,
                        contentDescription = "Decrease quantity",
                        tint = Color.White
                    )
                }
                Text(
                    text = "$quantity",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = onIncrease) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Increase quantity",
                        tint = Color.White
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color.White
                )
            }
        }
    }
}



@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    foodViewModel: FoodViewModel
) {
    Column(modifier= Modifier.fillMaxWidth().padding(top=100.dp)) {
        Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
            Text(
                text = "🛒 Your Orders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            IconButton(onClick = {navController.navigate("PendingOrders")}) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Pending"
                )
            }

            IconButton(onClick = { navController.navigate("ReadyOrders") }) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Done"
                )
            }

            IconButton(onClick = { navController.navigate("AllOrders") }) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "All"
                )
            }

    }
        UserStatsCard(foodViewModel)
    }
}

@Composable
fun PendingOrders(modifier: Modifier= Modifier, foodViewModel: FoodViewModel) {
    val context = LocalContext.current
    var userOrders by remember { mutableStateOf<List<Order>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodViewModel.fetchUserOrders { orders ->
            userOrders = orders
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Pending Orders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (userOrders.isEmpty()) {
                Text("No orders yet.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userOrders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                                    text = if (order.status) "✅ Done" else "⌛ Pending",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (order.status) Color(0xFFB2FF59) else Color(0xFFFFCC80)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ReadyOrders(modifier: Modifier= Modifier, foodViewModel: FoodViewModel) {
    val context = LocalContext.current
    var userOrders by remember { mutableStateOf<List<Order>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodViewModel.fetchUserOrdersDone { orders ->
            userOrders = orders
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Completed Orders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (userOrders.isEmpty()) {
                Text("No orders yet.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userOrders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                                    text = if (order.status) "✅ Done" else "⌛ Pending",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (order.status) Color(0xFFB2FF59) else Color(0xFFFFCC80)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllOrders(modifier: Modifier= Modifier, foodViewModel: FoodViewModel) {
    val context = LocalContext.current
    var userOrders by remember { mutableStateOf<List<Order>>(emptyList()) }

    LaunchedEffect(Unit) {
        foodViewModel.fetchUserOrdersAll { orders ->
            userOrders = orders
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "All Orders",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (userOrders.isEmpty()) {
                Text("No orders yet.", color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userOrders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserStatsCard(viewModel: FoodViewModel) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val email = currentUser?.email ?: "Unknown User"
    val userId = currentUser?.uid

    var totalOrders by remember { mutableStateOf(0) }
    var totalAmount by remember { mutableStateOf(0.0) }

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.getOrderCountForUser(it) { count ->
                totalOrders = count
            }
            FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", it)
                .get()
                .addOnSuccessListener { result ->
                    val total = result.sumOf { doc ->
                        doc.getDouble("totalPrice") ?: 0.0
                    }
                    totalAmount = total
                }
        }
    }

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("👤 Email: $email", color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("🛒 Total Orders: $totalOrders", color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("💸 Total Amount Sale: ${"%.2f".format(totalAmount)}", color = Color.White, fontSize = 16.sp)
        }
    }
}





