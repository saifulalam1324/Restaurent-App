package com.example.restaurentapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val user = auth.currentUser
        if (user == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            getUserRole(user.uid)
        }
    }

    fun login(email: String, password: String, isAdmin: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    getUserRole(userId, isAdmin)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String, isAdmin: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val role = if (isAdmin) "Admin" else "User"

                    // Store role in Firestore
                    firestore.collection("users").document(userId)
                        .set(mapOf("role" to role))
                        .addOnSuccessListener {
                            _authState.value = AuthState.Authenticated(role)
                        }
                        .addOnFailureListener {
                            _authState.value = AuthState.Error("Failed to save role")
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    private fun getUserRole(userId: String, isAdminCheck: Boolean? = null) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role") ?: "User"
                    _authState.value = AuthState.Authenticated(role)
                } else {
                    _authState.value = AuthState.Error("Role not found")
                }
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error("Failed to retrieve role")
            }
    }
}

sealed class AuthState {
    data class Authenticated(val role: String) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
