package com.example.quickhire

import android.content.Context
import android.content.SharedPreferences

class Preferences(val context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    fun save(user: User) {
        val edit = prefs.edit()
        edit.putString("email", user.email)
        edit.putString("fullName", user.fullName)
        edit.putString("detail_business", user.detailBusiness)
        edit.putString("location", user.location)
        edit.putString("phone", user.phone)
        edit.putInt("role", user.role)

        edit.apply()
    }

    fun getUser(): User {
        return  User(prefs.getString("email", "")!!, "",prefs.getString("fullName","")!!, prefs.getString("detail_business", "")!!, prefs.getString("location", "")!!, prefs.getString("phone", "")!!, prefs.getInt("role", 0))
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}