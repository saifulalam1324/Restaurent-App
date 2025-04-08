package com.example.restaurentapp.ViewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurentapp.Models.FoodItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.*
import com.example.restaurentapp.Models.CartItem
import com.example.restaurentapp.Models.Order
import com.example.restaurentapp.Models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class FoodViewModel : ViewModel() {
    private val db = Firebase.firestore
    fun addFoodItem(category: String, foodName: String, price: Double) {
        val foodData = hashMapOf(
            "name" to foodName,
            "price" to price,
            "category" to category
        )

        db.collection("FoodItem").document(category)
            .collection("items")
            .add(foodData)
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Food item added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error adding food item", e)
            }
    }
    private var _foodItems by mutableStateOf<Map<String, List<FoodItem>>>(emptyMap())
    val foodItems: Map<String, List<FoodItem>> get() = _foodItems


    fun loadFoodItems() {
        viewModelScope.launch {
            try {
                val categories = listOf("Burger", "Pizza", "Pasta", "Fries","Drinks")
                val itemsMap = mutableMapOf<String, List<FoodItem>>()

                for (category in categories) {
                    val items = db.collection("FoodItem")
                        .document(category)
                        .collection("items")
                        .get()
                        .await()
                        .map { doc ->
                            FoodItem(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                category = category
                            )
                        }
                    itemsMap[category] = items
                }
                _foodItems = itemsMap
            } catch (e: Exception) {
                println("Error loading food items: ${e.message}")
            }
        }
    }

    private var _cartItems by mutableStateOf<List<CartItem>>(emptyList())
    val cartItems: List<CartItem> get() = _cartItems




    fun addToCart(item: FoodItem) {
        val existingItem = _cartItems.find { it.foodItem.id == item.id }
        if (existingItem != null) {
            _cartItems = _cartItems.map {
                if (it.foodItem.id == item.id) {
                    it.copy(quantity = it.quantity + 1)
                } else {
                    it
                }
            }
        } else {
            _cartItems = _cartItems + CartItem(item) // Add new item
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems = _cartItems.filter { it.foodItem.id != itemId }
    }

    fun increaseQuantity(itemId: String) {
        _cartItems = _cartItems.map {
            if (it.foodItem.id == itemId) {
                it.copy(quantity = it.quantity + 1)
            } else {
                it
            }
        }
    }
    fun decreaseQuantity(itemId: String) {
        _cartItems = _cartItems.map {
            if (it.foodItem.id == itemId) {
                val newQuantity = (it.quantity - 1).takeIf { it > 0 } ?: 1
                it.copy(quantity = newQuantity)
            } else {
                it
            }
        }
    }

    fun getCartTotal(): Double {
        return _cartItems.sumOf { it.foodItem.price * it.quantity }
    }

    fun placeOrder() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val orderData = hashMapOf<String, Any>()

        _cartItems.forEach { cartItem ->
            val key = "${cartItem.foodItem.category} - ${cartItem.foodItem.name}"
            orderData[key] = cartItem.quantity
        }

        orderData["totalPrice"] = getCartTotal()
        orderData["status"] = false
        orderData["userId"] = userId //

        db.collection("orders")
            .add(orderData)
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Order placed successfully!")
                _cartItems = emptyList()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error placing order", e)
            }
    }
    fun fetchUserOrders(onOrdersFetched: (List<Order>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val orders = result.map { doc ->
                    val items = doc.data
                        .filterKeys { it != "userId" && it != "totalPrice" && it != "status" }
                        .mapNotNull {
                            val key = it.key
                            val value = (it.value as? Long)?.toInt() ?: (it.value as? Int) ?: return@mapNotNull null
                            key to value
                        }

                    Order(
                        items = items,
                        totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                        status = doc.getBoolean("status") ?: false
                    )
                }
                onOrdersFetched(orders)
            }
    }
    fun fetchAllOrders(onOrdersFetched: (List<Pair<String, Order>>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("orders")
            .whereEqualTo("status", false)
            .get()
            .addOnSuccessListener { result ->
                val orders = result.documents.mapNotNull { doc ->
                    val data = doc.data
                    if (data != null) {
                        val items = data
                            .filterKeys { it != "userId" && it != "totalPrice" && it != "status" }
                            .mapNotNull {
                                val key = it.key
                                val value = (it.value as? Long)?.toInt() ?: (it.value as? Int)
                                if (value != null) key to value else null
                            }

                        val orderId = doc.id
                        val order = Order(
                            items = items,
                            totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                            status = false
                        )

                        orderId to order
                    } else null
                }
                onOrdersFetched(orders)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching orders", exception)
            }
    }

    fun updateOrderStatusToReady(orderId: String, onComplete: () -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("orders")
            .document(orderId)
            .update("status", true)
            .addOnSuccessListener { onComplete() }
    }
    fun deleteOrder(orderId: String, onComplete: () -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("orders")
            .document(orderId)
            .delete()
            .addOnSuccessListener { onComplete() }
    }
    fun fetchAllUsers(onResult: (List<UserModel>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .get()
            .addOnSuccessListener { result ->
                val users = result.map { document ->
                    val email = document.getString("email") ?: ""
                    val uid = document.id  // Here we use the Firestore document ID as the uid
                    val isAdmin = document.getBoolean("isAdmin") ?: false
                    val isUser = document.getBoolean("isUser") ?: true
                    UserModel(email, uid, isAdmin, isUser)
                }
                onResult(users)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error fetching users", e)
                onResult(emptyList())
            }
    }


    fun getOrderCountForUser(userId: String, onResult: (Int) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                // Count the number of orders for this user
                val orderCount = result.size()
                onResult(orderCount) // Pass the count to the callback function
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error fetching order count", e)
                onResult(0) // In case of failure, return 0
            }
    }
    fun getPendingOrderCountForUser(userId: String, onResult: (Int) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("orders")
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", false)
            .get()
            .addOnSuccessListener { result ->
                val pendingOrderCount = result.size()
                onResult(pendingOrderCount)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Error fetching pending order count", e)
                onResult(0)
            }
    }






}



