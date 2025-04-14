package com.example.restaurentapp.ViewModels

import androidx.lifecycle.ViewModel
import com.example.restaurentapp.Models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    userId?.let {
                        firestore.collection("users").document(it).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val isAdmin = document.getBoolean("admin") ?: false
                                    val isUser = document.getBoolean("user") ?: false

                                    when {
                                        isAdmin -> onResult(true, null, "AdminHome")
                                        isUser -> onResult(true, null, "UserHome")
                                        else -> onResult(false, "Invalid role", null)
                                    }
                                } else {
                                    onResult(false, "User data not found", null)
                                }
                            }
                            .addOnFailureListener { e ->
                                onResult(false, e.localizedMessage, null)
                            }
                    }
                } else {
                    onResult(false, task.exception?.localizedMessage, null)
                }
            }
    }
    fun signup(
        email: String,
        password: String,
        isAdmin: Boolean,
        isUser: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    userId?.let {
                        val userModel = UserModel(email, userId, isAdmin, isUser)
                        firestore.collection("users").document(it)
                            .set(userModel)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    onResult(true, null)
                                } else {
                                    onResult(false, "Something went wrong")
                                }
                            }
                    }
                } else {
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }
    fun logout(onResult: () -> Unit) {
        auth.signOut()
        onResult()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}