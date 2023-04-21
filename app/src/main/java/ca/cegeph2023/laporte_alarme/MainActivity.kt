package ca.cegeph2023.laporte_alarme

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONObject

data class Logs(
    val id: String,
    val date: String,
    val hour: String,
    val description: String
)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Button Alarme OFF/ON
        val btnAlarm: Button = findViewById(R.id.btn_setAlarme)
        btnAlarm.setOnClickListener {
            if (btnAlarm.isSelected) {
                btnAlarm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vec_alarme_on, 0, 0, 0)
                btnAlarm.text = getString(R.string.home_alarme_on)
                btnAlarm.isSelected = false
            } else {
                btnAlarm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vec_alarme_off, 0, 0, 0)
                btnAlarm.text = getString(R.string.home_alarme_off)
                btnAlarm.isSelected = true
            }
        }

        // Button Logs
        val btnLogs: Button = findViewById(R.id.btn_logs)
        btnLogs.setOnClickListener {
            val intent = Intent(this, LogsActivity::class.java)
            startActivity(intent)
        }

        // Affiche la Date et Heure actuel.
        val handler = Handler()
        val delay = 1000L
        val dateTimeLabel = findViewById<TextView>(R.id.label_date)
        val hourTimeLabel = findViewById<TextView>(R.id.label_hour)
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.CANADA_FRENCH)
        val hourFormat = SimpleDateFormat("hh:mm", Locale.CANADA_FRENCH)
        handler.postDelayed(object : Runnable {
            override fun run() {
                val timeZone = TimeZone.getTimeZone("America/New_York")
                val calendar = Calendar.getInstance(timeZone)
                val currentDateTime = dateFormat.format(calendar.time)
                val currentHourTime = hourFormat.format(calendar.time)
                dateTimeLabel.text = currentDateTime
                hourTimeLabel.text = currentHourTime
                handler.postDelayed(this, delay)
            }
        }, delay)

        // Appel au Broker MQTT
        val btnProfil: Button = findViewById(R.id.btn_profil)
        val serverUri = "tcp://172.16.5.202:1883"
        val clientId = "pi"
        val topic = "LaPorte/detection"
        val qos = 2 // Quality of Service

        // Créer un client MQTT
        try {
            val client = MqttAndroidClient(this, serverUri, clientId)
            // Configurer une callback pour la réception des messages
            val options = MqttConnectOptions()
            options.isCleanSession = true
            options.keepAliveInterval = 60
            options.connectionTimeout = 30
            options.isCleanSession = true
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    // Connecté au broker

                    client.subscribe(topic, qos)
                    println("MQTT Connection : Complete")
                }

                override fun connectionLost(cause: Throwable?) {
                    // Connexion perdue
                    println("MQTT Connection : Lost")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // Message reçu
                    val payload = message?.payload
                    // Faire quelque chose avec le message reçu
                    btnProfil.text = message?.toString()
                    println("MQTT Message: " + message?.toString())

                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Message délivré
                    println("MQTT Delivery")
                }
            })
            client.connect(options, this, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // Connexion réussie
                    println("MQTT Connection : Reussie")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // Connexion échouée
                    println("MQTT Connection : Failed")
                    exception?.printStackTrace()
                }
            })

            // Connecter au broker
        } catch (ex: MqttException) {
            // Gérer l'exception et imprimer le code d'erreur
            println("MQTT Connection : Erreur de connexion au broker MQTT: ${ex.reasonCode}")
        }

    }
}