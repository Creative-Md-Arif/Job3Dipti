package com.example.job3diptiictamadl40401.viewdiptiictamadl40401

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.job3diptiictamadl40401.R
import com.example.job3diptiictamadl40401.databinding.ActivityMainBinding
import com.example.job3diptiictamadl40401.viewmodelDipti.AuthenticationViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.FirestoreViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.LocationViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.UserProfileViewModelDiptiictamadl40401
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var authViewModel: AuthenticationViewModelDiptiictamadl40401
    private lateinit var firestoreViewModel: FirestoreViewModelDiptiictamadl40401
    private lateinit var locationViewModel: LocationViewModelDiptiictamadl40401
    private lateinit var userProfileViewModel: UserProfileViewModelDiptiictamadl40401
    lateinit var actionDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this).get(AuthenticationViewModelDiptiictamadl40401::class.java)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModelDiptiictamadl40401::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModelDiptiictamadl40401::class.java)
        userProfileViewModel = ViewModelProvider(this).get(UserProfileViewModelDiptiictamadl40401::class.java)

        val navController = findNavController(R.id.fragmentContainerView)
        binding.bottomBar.setupWithNavController(navController)
        binding.drawerNav.setupWithNavController(navController)

        actionDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerlayout,
            R.string.nav_open,
            R.string.nav_close
        )
        actionDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.drawerNav.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this, LoginActivitydiptiictamadl40401::class.java)
                    )
                    finish()
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)

                }
                R.id.friendsFragment -> {
                    navController.navigate(R.id.friendsFragment)
                }

            }
            true
        }
        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity(
                        Intent(this, LoginActivitydiptiictamadl40401::class.java)
                    )
                    finish()
                }
                R.id.friendsFragment -> {
                    navController.navigate(R.id.friendsFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }

            }
            true
        }

        setupNavHeader()
    }

    private fun setupNavHeader() {
        // Get the header view
        val headerView: View = binding.drawerNav.getHeaderView(0)


        val profileImageView: ImageView = headerView.findViewById(R.id.profile_Image)
        val nameTextView: TextView = headerView.findViewById(R.id.profileName)
        val emailTextView: TextView = headerView.findViewById(R.id.profileEmail)
        val locationTextView: TextView = headerView.findViewById(R.id.location)

        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            emailTextView.text = currentUser.email.toString()

            firestoreViewModel.getUser(this,currentUser.uid) {
                if (it != null) {
                    nameTextView.text = it.displayName.toString()


                    firestoreViewModel.getUserLocation(this,currentUser.uid) {
                        if (it.isNotEmpty()) {
                            locationTextView.text = it.toString()
                        }
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()

                }
            }
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            profileImageView.setImageResource(R.drawable.profile) // Replace with your default image resource

        }

        // Fetch and display the profile image when the activity starts
        userProfileViewModel.fetchProfileImageUrl()

        // Observe the LiveData from the ViewModel
        userProfileViewModel.profileImageUrl.observe(this) {
            // Load the image into the navigation header
            val navView = binding.drawerNav.getHeaderView(0)
            val profileImageView = navView.findViewById<ImageView>(R.id.profile_Image)
            if (it != null) {
                // Load user image from URL
                Glide.with(this).load(it).into(profileImageView)
            } else {
                // Load default image from drawable
                profileImageView.setImageResource(R.drawable.profile) // Replace with your default image resource
            }
        }

        userProfileViewModel.errorMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        userProfileViewModel.isUploading.observe(this) { isUploading ->
            binding.progressBar.visibility = if (isUploading) View.VISIBLE else View.GONE
        }

        // Button to trigger image picking
        profileImageView.setOnClickListener {
            openGallery()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { userProfileViewModel.uploadProfileImage(it) }
        }
    }

    // Function to open gallery for image selection
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }


}