package com.example.paint.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.paint.PermissionUtils
import com.example.paint.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.paint.PermissionUtils.isPermissionGranted
import com.example.paint.R
import com.example.paint.mainActivity.MainActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private var map: GoogleMap? = null
    private var permissionDenied = false

    private var mGpsLocationClient: LocationManager? = null
    private val polylineOptions = PolylineOptions()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(this)

        childFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fragmentContainerViewMap, mapFragment)
        }

        mGpsLocationClient =
            activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = true
            startLocationTracker()
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(parentFragmentManager, "dialog")
            return
        }

        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    override fun onMapReady(p0: GoogleMap) {
        map = p0
        enableMyLocation()

    }

    private val locationListener =
        android.location.LocationListener { location -> //handle location change
            val latLng = LatLng(location.latitude, location.longitude)
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng, 18F
                )
            )

            polylineOptions.add(latLng)
            map?.addPolyline(polylineOptions)

        }

    override fun onResume() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map?.isMyLocationEnabled = true
            startLocationTracker()
        }
        super.onResume()
    }

    private fun startLocationTracker() {
        mGpsLocationClient?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 100L, 1f, locationListener
        )
    }

    override fun onPause() {
        mGpsLocationClient?.removeUpdates(locationListener)
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions, grantResults, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            enableMyLocation()
        } else {
            permissionDenied = true
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}