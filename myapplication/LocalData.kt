package com.example.myapplication

import android.content.Context

object LocalData {
    const val NAME_KEY = "NAME_LOCATION_OWNER"

    fun saveMyName(mContext: Context,name: String){
        val sharedPreferences = mContext.getSharedPreferences("LOC",Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(NAME_KEY,name).apply()
    }
    fun getMyName(mContext: Context): String{
        val sharedPreferences = mContext.getSharedPreferences("LOC",Context.MODE_PRIVATE)
        return if(sharedPreferences.getString(NAME_KEY,"")==null) ""
        else sharedPreferences.getString(NAME_KEY,"")!!
    }
}