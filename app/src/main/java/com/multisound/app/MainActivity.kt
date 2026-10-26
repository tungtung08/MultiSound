package com.multisound.app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var deviceInfoTextView: TextView
    private lateinit var btnCheckStatus: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter?

    private val REQUEST_BLUETOOTH_PERMISSIONS = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val titleView = TextView(this).apply {
            text = "MultiSound Audio Share"
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        }
        layout.addView(titleView)

        statusTextView = TextView(this).apply {
            text = "Status: Checking Android 15 Bluetooth..."
            textSize = 16f
            setPadding(0, 0, 0, 20)
        }
        layout.addView(statusTextView)

        deviceInfoTextView = TextView(this).apply {
            text = "Connected Devices: None"
            textSize = 14f
            setPadding(0, 0, 0, 40)
        }
        layout.addView(deviceInfoTextView)

        btnCheckStatus = Button(this).apply {
            text = "Check Bluetooth & Devices"
            setOnClickListener {
                checkBluetoothAndDevices()
            }
        }
        layout.addView(btnCheckStatus)

        setContentView(layout)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        requestBluetoothPermissions()
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSIONS)
            } else {
                checkBluetoothAndDevices()
            }
        } else {
            checkBluetoothAndDevices()
        }
    }

    private fun checkBluetoothAndDevices() {
        if (bluetoothAdapter == null) {
            statusTextView.text = "Status: Bluetooth is not supported on this device."
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            statusTextView.text = "Status: Bluetooth is OFF. Please turn it on."
            Toast.makeText(this, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        statusTextView.text = "Status: Bluetooth is ON (Android 15 Ready)"

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
                val deviceListString = StringBuilder("Paired / Connected Devices:\n")
                
                if (pairedDevices != null && pairedDevices.isNotEmpty()) {
                    for (device in pairedDevices) {
                        deviceListString.append("- ${device.name ?: "Unknown Device"} (${device.address})\n")
                    }
                } else {
                    deviceListString.append("No paired Bluetooth devices found.")
                }
                deviceInfoTextView.text = deviceListString.toString()
            } else {
                deviceInfoTextView.text = "Connected Devices: Permission denied for Bluetooth Connect."
            }
        } catch (e: SecurityException) {
            deviceInfoTextView.text = "Error accessing Bluetooth devices: ${e.message}"
        }
    }
}
