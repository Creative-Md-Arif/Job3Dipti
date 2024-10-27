package com.example.job3diptiictamadl40401.viewdiptiictamadl40401

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.job3diptiictamadl40401.databinding.ActivityRegisterActivitydiptiictamadl40401Binding
import com.example.job3diptiictamadl40401.viewmodelDipti.AuthenticationViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.FirestoreViewModelDiptiictamadl40401
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivitydiptiictamadl40401 : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterActivitydiptiictamadl40401Binding
    private lateinit var authenticationViewModel: AuthenticationViewModelDiptiictamadl40401
    private lateinit var firestoreViewModel:FirestoreViewModelDiptiictamadl40401
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterActivitydiptiictamadl40401Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authenticationViewModel = ViewModelProvider(this).get(AuthenticationViewModelDiptiictamadl40401::class.java)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModelDiptiictamadl40401::class.java)

        binding.registerBtn.setOnClickListener {
            val name = binding.displayNameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val password = binding.passwordEt.text.toString()
            val confirmPassword = binding.conPasswordEt.text.toString()
            val location = "Don't found any location yet"
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                    .show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            } else {
                authenticationViewModel.register(email, password, {
                    firestoreViewModel.saveUser(this, authenticationViewModel.getCurrentUserId(), name, email, location)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                })
            }
        }
        binding.loginTxt.setOnClickListener {
            startActivity(Intent(this, LoginActivitydiptiictamadl40401::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this@RegisterActivitydiptiictamadl40401, MainActivity::class.java))
            finish()
        }
    }
}