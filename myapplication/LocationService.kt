package com.example.myapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import kotlin.math.log

class LocationService : Service() {
    private var datastore: Datastore? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            logThis("OnLocationResult")
            result.lastLocation.apply {
                logThis("LAT - $latitude LON - $longitude")
                //save
                datastore?.apply {
                    addLocation(latitude,longitude)
                }
            }
        }
        override fun onLocationAvailability(p0: LocationAvailability) {
            logThis("onLocationAvailability - ${p0.isLocationAvailable}")
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logThis("onStartCommand")

        datastore = Datastore.getThis(this)

        if (intent != null) {
            val action = intent.action
            if (action == Consts.LOCATION_SERVICE_START_ACTION) {
                startLocationUpdates()
            } else if (action == Consts.LOCATION_SERVICE_STOP_ACTION) {
                stopLocationUpdates()
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        logThis("START_LOCATION_UPDATES")

        var channelId = "defaultChannelId"
        var notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var intent = Intent()
        var pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        var notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
        notificationBuilder.apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle("Location Service")
            setContentText("Running")
            setContentIntent(pendingIntent)
            priority = NotificationCompat.PRIORITY_HIGH
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setAutoCancel(false)
            setOngoing(true)
        }
        //channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                var channel = NotificationChannel(
                    channelId, "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
                )
                channel.description = "This channel is used by Location Service X"
                notificationManager.createNotificationChannel(channel)
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 4000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        startForeground(Consts.LOCATION_SERVICE_ID, notificationBuilder.build())
    }

    private fun stopLocationUpdates() {
        logThis("STOP_LOCATION_UPDATES")

        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
        stopForeground(false)
        stopSelf()
    }

    private fun logThis(s: String) {
        Log.d("LOCATION-SERVICE", s)
    }

}