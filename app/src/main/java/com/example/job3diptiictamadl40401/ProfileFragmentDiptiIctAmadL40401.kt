package com.example.job3diptiictamadl40401

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.job3diptiictamadl40401.databinding.FragmentProfileDiptiIctAmadL40401Binding
import com.example.job3diptiictamadl40401.viewdiptiictamadl40401.LoginActivitydiptiictamadl40401
import com.example.job3diptiictamadl40401.viewdiptiictamadl40401.MainActivity
import com.example.job3diptiictamadl40401.viewmodelDipti.AuthenticationViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.FirestoreViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.LocationViewModelDiptiictamadl40401
import com.google.firebase.auth.FirebaseAuth


class ProfileFragmentDiptiIctAmadL40401 : Fragment() {

    private lateinit var binding: FragmentProfileDiptiIctAmadL40401Binding
    private lateinit var authViewModel: AuthenticationViewModelDiptiictamadl40401
    private lateinit var firestoreViewModel: FirestoreViewModelDiptiictamadl40401
    private lateinit var locationViewModel: LocationViewModelDiptiictamadl40401
    private val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileDiptiIctAmadL40401Binding.inflate(inflater, container, false)
        authViewModel =
            ViewModelProvider(this).get(AuthenticationViewModelDiptiictamadl40401::class.java)
        firestoreViewModel =
            ViewModelProvider(this).get(FirestoreViewModelDiptiictamadl40401::class.java)
        locationViewModel =
            ViewModelProvider(this).get(LocationViewModelDiptiictamadl40401::class.java)

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), LoginActivitydiptiictamadl40401::class.java))
        }
        binding.homeBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        loadUserInfo()

        binding.updateBtn.setOnClickListener {
            val newName = binding.nameEt.text.toString()
            val newLocation = binding.locationEt.text.toString()

            updateProfile(newName, newLocation)
        }

        return binding.root
    }

    private fun updateProfile(newName: String, newLocation: String) {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            firestoreViewModel.updateUser(requireContext(), userId, newName, newLocation)
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT)
                .show()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        } else {
            // Handle the case where the current user is null
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            binding.emailEt.setText(currentUser.email)

            firestoreViewModel.getUser(requireContext(), currentUser.uid) {
                if (it != null) {
                    binding.nameEt.setText(it.displayName)

                    firestoreViewModel.getUserLocation(requireContext(), currentUser.uid) {
                        if (it.isNotEmpty()) {
                            binding.locationEt.setText(it)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()

                }
            }
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()

        }
    }


}


