package com.example.myapplication

import android.content.Context
import com.google.firebase.database.*

class Datastore(private var mContext: Context) {

    private val store = FirebaseDatabase.getInstance()

    companion object {
        fun getThis(c: Context): Datastore {
            return Datastore(c)
        }
    }

    fun addLocation(lat: Double, lon: Double) {
        val name = LocalData.getMyName(mContext)
        store.reference.child("users").child(name)
            .setValue(Location(name, lat, lon))
    }

    fun onLocationChange(onChange: (Location) -> Unit) {
        store.reference.child("users").child(LocalData.getMyName(mContext))
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val location = snapshot.getValue(Location::class.java)
                    if (location != null) onChange(location)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun getAllUsers(onComplete: (MutableList<Location>) -> Unit){
        var list = mutableListOf<Location>()
        store.reference.child("users")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(snap in snapshot.children){
                        val user = snap.getValue(Location::class.java)
                        if(user!=null && user.lat!=0.0) list.add(user)
                    }
                    onComplete(list)
                }
                override fun onCancelled(error: DatabaseError) {
                    onComplete(list)
                }

            })
    }
}

@IgnoreExtraProperties
data class Location(
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)