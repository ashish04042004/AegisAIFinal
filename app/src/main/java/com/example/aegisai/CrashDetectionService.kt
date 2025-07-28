package com.example.aegisai

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.aegisai.onnx.CrashPredictor
import com.example.aegisai.ui.emergency.EmergencyActivity
import kotlinx.coroutines.*
import java.util.*

class CrashDetectionService : Service(), SensorEventListener {

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "CrashDetectionChannel"

    private lateinit var crashPredictor: CrashPredictor
    private lateinit var sensorManager: SensorManager

    private val SEQUENCE_LENGTH = 20
    private val sensorDataBuffer = LinkedList<FloatArray>()
    private var accelData = FloatArray(3)
    private var gyroData = FloatArray(3)
    private var isAccelReady = false
    private var isGyroReady = false
    private var isCrashProcessing = false

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        crashPredictor = CrashPredictor(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createMonitoringNotification())
        MonitoringStatus.statusFlow.value = "✅ Monitoring for crashes..."
        isCrashProcessing = false

        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || isCrashProcessing) return

        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                System.arraycopy(event.values, 0, accelData, 0, 3)
                isAccelReady = true
            }
            Sensor.TYPE_GYROSCOPE -> {
                System.arraycopy(event.values, 0, gyroData, 0, 3)
                isGyroReady = true
            }
        }

        if (isAccelReady && isGyroReady) {
            val combinedData = floatArrayOf(
                accelData[0], accelData[1], accelData[2],
                gyroData[0], gyroData[1], gyroData[2]
            )
            sensorDataBuffer.add(combinedData)
            if (sensorDataBuffer.size > SEQUENCE_LENGTH) sensorDataBuffer.removeFirst()
            isAccelReady = false
            isGyroReady = false

            if (sensorDataBuffer.size == SEQUENCE_LENGTH) {
                val inputData = sensorDataBuffer.flatMap { it.asIterable() }.toFloatArray()
                val score = crashPredictor.predict(inputData, SEQUENCE_LENGTH)

                if (score > 0.85) {
                    isCrashProcessing = true
                    // --- THIS IS THE KEY CHANGE ---
                    launchEmergencyFlow()
                }
            }
        }
    }

    /**
     * Launches the dedicated, fullscreen EmergencyActivity to handle the user flow.
     */
    private fun launchEmergencyFlow() {
        // Update the app's internal status
        MonitoringStatus.statusFlow.value = "💥 Severe Crash Detected!"

        // Create an Intent to start the EmergencyActivity
        val intent = Intent(this, EmergencyActivity::class.java).apply {
            // This flag is necessary to start an Activity from a Service context
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // If you generate a unique ID for the incident, you can pass it here
            // putExtra("INCIDENT_ID", "some_unique_id")
        }
        startActivity(intent)

        // The service's job is to trigger the UI. The EmergencyActivity will now
        // be responsible for what happens next (e.g., stopping the service if the user cancels).
        // Therefore, we DO NOT call stopSelf() here anymore.
    }

    /**
     * Creates the persistent notification shown while the service is monitoring.
     */
    private fun createMonitoringNotification(): Notification {
        val channelId = CHANNEL_ID
        val channelName = "Monitoring Status"
        val importance = NotificationManager.IMPORTANCE_LOW // Low importance so it's not intrusive

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        // Make the notification open the main app when tapped
        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("AegisAI is Actively Monitoring")
            .setContentText("Your vehicle is protected.")
            .setSmallIcon(R.drawable.ic_shield_24) // Using the shield icon for a professional look
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Makes the notification non-dismissible
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        crashPredictor.close()
        serviceScope.cancel()
        MonitoringStatus.statusFlow.value = "Monitoring stopped."
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onBind(intent: Intent?): IBinder? = null
}