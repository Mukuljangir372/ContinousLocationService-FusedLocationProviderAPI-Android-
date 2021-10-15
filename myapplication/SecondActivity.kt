package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.myapplication.databinding.ActivitySecondBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds

import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.CameraUpdate







class SecondActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding : ActivitySecondBinding
    private lateinit var mMap: GoogleMap

    private lateinit var datastore: Datastore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datastore = Datastore.getThis(this@SecondActivity)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        Toast.makeText(this@SecondActivity
            ,"MAP BECOMING READY AND FETCHING USERS....",Toast.LENGTH_SHORT).show()
        datastore.getAllUsers {
            for(user in it){
                mMap.addMarker(MarkerOptions()
                    .position(LatLng(user.lat,user.lon))
                    .title(user.name))
            }
            val builder = LatLngBounds.Builder()
            for (user in it) {
                builder.include(LatLng(user.lat,user.lon))
            }
            val bounds = builder.build()
            val padding = 100 // offset from edges of the map in pixels

            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

            //move camera
            mMap.animateCamera(cu)
        }
    }
    override fun onDestroy() {
        //set null
        super.onDestroy()
    }
}