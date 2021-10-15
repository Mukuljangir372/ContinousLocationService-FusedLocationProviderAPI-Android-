package com.example.myapplication

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.database.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val requestLocationPermissionCode = 1

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            Datastore.getThis(this@MainActivity).onLocationChange {
                textView.text = "LAT - ${it.lat} LON - ${it.lon}"
            }

            name.text = LocalData.getMyName(this@MainActivity)

            done.setOnClickListener {
                var nameText = edittext.text.toString().trim()
                if (nameText.isNotEmpty()) {
                    nameText = "$nameText ${getRandomString(5)}"
                    LocalData.saveMyName(this@MainActivity,nameText)
                    name.text = nameText
                    showMsg("NAME ADDED : $nameText")
                }
            }

            startBtn.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity, arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ), requestLocationPermissionCode
                    )
                } else {
                    if(LocalData.getMyName(this@MainActivity)==""){
                        showMsg("Enter you name first")
                    }else {
                        startMyService()
                    }
                }
            }
            stopBtn.setOnClickListener {
                stopMyService()
            }
            locateAll.setOnClickListener {
                startActivity(Intent(this@MainActivity,SecondActivity::class.java))
            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestLocationPermissionCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(LocalData.getMyName(this@MainActivity)==""){
                    showMsg("Enter you name first")
                }else {
                    startMyService()
                }
            } else {
                showMsg("PERMISSION DENIED")
            }
        }
    }


    private fun isServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationService::class.java.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

    private fun startMyService() {
        if (!isServiceRunning()) {
            var intent = Intent(this@MainActivity, LocationService::class.java).apply {
                action = Consts.LOCATION_SERVICE_START_ACTION
            }
            startService(intent)
            showMsg("STARTED")
        } else {
            showMsg("Service already running")
        }
    }

    private fun stopMyService() {
        if (isServiceRunning()) {
            var intent = Intent(this@MainActivity, LocationService::class.java).apply {
                action = Consts.LOCATION_SERVICE_STOP_ACTION
            }
            startService(intent)
            showMsg("STOPPED")
        } else {
            showMsg("Service not running")
        }
    }

    private fun logThis(s: String) {
        Log.d("LOCATION-SERVICE-X", s)
    }

    private fun showMsg(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        logThis(text)
    }
   private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

}

