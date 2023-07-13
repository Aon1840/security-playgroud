package com.example.poc_plugin_templete

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.safetydetect.SysIntegrityRequest
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets


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

    private fun invokeSysIntegrity() {
        val nonce = "abc".toByteArray()
        val sysIntegrityRequest = SysIntegrityRequest()
        sysIntegrityRequest.appId = "APP_ID"
        sysIntegrityRequest.nonce = nonce
        sysIntegrityRequest.alg = "alg"

        SafetyDetect.getClient(this)
            .sysIntegrity(sysIntegrityRequest)
            .addOnSuccessListener { response -> // Indicates communication with the service was successful.
                // Use response.getResult() to obtain the result data.
                val jwsStr = response.result

                // Process the result data here.
                val jwsSplit = jwsStr.split(".").toTypedArray()
                val jwsPayloadStr = jwsSplit[1]
                val payloadDetail = String(
                    Base64.decode(
                        jwsPayloadStr.toByteArray(StandardCharsets.UTF_8),
                        Base64.URL_SAFE
                    ), StandardCharsets.UTF_8
                )
                try {
                    val jsonObject = JSONObject(payloadDetail)
                    val basicIntegrity = jsonObject.getBoolean("basicIntegrity")
//                    fg_button_sys_integrity_go.setBackgroundResource(if (basicIntegrity) R.drawable.btn_round_green else R.drawable.btn_round_red)
//                    fg_button_sys_integrity_go.setText(R.string.rerun)
                    val isBasicIntegrity = basicIntegrity.toString()
                    val basicIntegrityResult = "Basic Integrity: $isBasicIntegrity"
//                    fg_payloadBasicIntegrity.text = basicIntegrityResult
                    if (!basicIntegrity) {
                        val advice = "Advice: " + jsonObject.getString("advice")
//                        fg_payloadAdvice.text = advice
                    }
                } catch (e: JSONException) {
                    val errorMsg = e.message
                    Log.e(TAG, errorMsg ?: "unknown error")
                }
            }
            .addOnFailureListener { e -> // There was an error communicating with the service.
                val errorMsg: String = if (e is ApiException) {
                    // An error with the HMS API contains some additional details.
                    SafetyDetectStatusCodes.getStatusCodeString(e.statusCode) + ": " + e.message
                    // You can use the apiException.getStatusCode() method to obtain the status code.
                } else {
                    // An unknown type of error has occurred.
                    e.message.orEmpty()
                }
                Log.e(TAG, errorMsg)
                Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_SHORT).show()
//                fg_button_sys_integrity_go.setBackgroundResource(R.drawable.btn_round_yellow)
//                fg_button_sys_integrity_go.setText(R.string.rerun)
            }
    }
}