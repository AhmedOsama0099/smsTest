package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.xabaras.android.smsinterceptor.SmsInterceptor

class MainActivity : AppCompatActivity() {

    private val smsInterceptor: SmsInterceptor = SmsInterceptor(this, lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        smsInterceptor.setNumberFilter("+3912345678", "+441235678")
        smsInterceptor.setBodyFilter {
            it.startsWith("Hello")
        }
    }

    fun startSmsInterceptor(view: View?) {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED ) {
            smsInterceptor.startListening { fromNumber, message ->
                AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage("You received a message from $fromNumber.\nMessage:\n$message")
                    .setNeutralButton(android.R.string.ok, null)
                    .create()
                    .show()
            }
            Toast.makeText(this, "Started listening!", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS), 1001)
        }
    }

    fun stopSmsInterceptor(view: View) {
        try {
            smsInterceptor?.stopListening()
            Toast.makeText(this, "Ended listening!", Toast.LENGTH_LONG).show()
        } catch (e: IllegalStateException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1001) {
            if ( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSmsInterceptor(null)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val smsInterceptor: SmsInterceptor = SmsInterceptor(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        smsInterceptor.startListening { fromNumber, message ->
                // Do what you want with fromNumber and message body
        }
    }*/
}