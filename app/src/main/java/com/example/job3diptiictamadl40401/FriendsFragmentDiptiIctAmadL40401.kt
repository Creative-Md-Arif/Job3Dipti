package com.example.job3diptiictamadl40401

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.job3diptiictamadl40401.adapterjob3diptiictamadl40401.AdapterDipti
import com.example.job3diptiictamadl40401.databinding.FragmentFriendsDiptiIctAmadL40401Binding
import com.example.job3diptiictamadl40401.viewdiptiictamadl40401.MapsActivity
import com.example.job3diptiictamadl40401.viewmodelDipti.AuthenticationViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.FirestoreViewModelDiptiictamadl40401
import com.example.job3diptiictamadl40401.viewmodelDipti.LocationViewModelDiptiictamadl40401
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class FriendsFragmentDiptiIctAmadL40401 : Fragment() {

    private lateinit var binding: FragmentFriendsDiptiIctAmadL40401Binding
    private lateinit var firestoreViewModel: FirestoreViewModelDiptiictamadl40401
    private lateinit var authenticationViewModel: AuthenticationViewModelDiptiictamadl40401
    private lateinit var userAdapter: AdapterDipti
    private lateinit var locationViewModel: LocationViewModelDiptiictamadl40401
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(requireContext(), "Location Permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsDiptiIctAmadL40401Binding.inflate(inflater,container, false)

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModelDiptiictamadl40401::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModelDiptiictamadl40401::class.java)
        authenticationViewModel = ViewModelProvider(this).get(AuthenticationViewModelDiptiictamadl40401::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationViewModel.initializeFusedLocationClient(fusedLocationClient)

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Permission is already granted
            getLocation()
        }
        userAdapter = AdapterDipti(emptyList())
        binding.userRV.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        fetchUsers()

        binding.locationBtn.setOnClickListener {
            startActivity(Intent(requireContext(),MapsActivity::class.java))
        }


        return binding.root
    }

    private fun fetchUsers() {
        firestoreViewModel.getAllUsers(requireContext()){
            userAdapter.updateData(it)
        }
    }

    private fun getLocation() {
        locationViewModel.getLastLocation(requireContext()) {
            // Save location to Firestore for the current user
            authenticationViewModel.getCurrentUserId().let { userId ->
                firestoreViewModel.updateUserLocation(requireContext(),userId, it)
            }
        }
    }

}