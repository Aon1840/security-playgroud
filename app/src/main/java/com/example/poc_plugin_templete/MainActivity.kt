package com.example.poc_plugin_templete

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability


class MainActivity : AppCompatActivity() {
    private lateinit var tvGMS: TextView
    private lateinit var tvHMS: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvGMS = findViewById(R.id.tvGMS)
        tvHMS = findViewById(R.id.tvHMS)
        HuaweiApiAvailability.getInstance()
        tvHMS.text = "Is HMS: ${isHMSAvailable(this)}"
        tvGMS.text = "Is GMS: ${isGMSAvailable(this)}"
        Log.d("AON", isHMSAvailable(this).toString())
        Log.d("AON", isGMSAvailable(this).toString())
    }

    private fun isHMSAvailable(context: Context): Boolean {
        val hms = HuaweiApiAvailability.getInstance()
        val isHMS = hms.isHuaweiMobileServicesAvailable(context)
        return isHMS == ConnectionResult.SUCCESS
    }
    private fun isGMSAvailable(context: Context): Boolean {
        val gms = GoogleApiAvailability.getInstance()
        val isGMS = gms.isGooglePlayServicesAvailable(context)
        return isGMS == com.google.android.gms.common.ConnectionResult.SUCCESS
    }
}