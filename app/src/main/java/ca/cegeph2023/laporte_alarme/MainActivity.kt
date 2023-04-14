package ca.cegeph2023.laporte_alarme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.os.Handler
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        val btnProfil: Button = findViewById(R.id.btn_profil)
        btnProfil.text = "COUCOU"
        val serverUri = "tcp://172.16.5.202:2222"
        val clientId = "pi"
        val topic = "LaPorte/detection"
        val qos = 2 // Quality of Service



        // Créer un client MQTT

        try {
            val client = MqttAndroidClient(this, serverUri, clientId)
            // Configurer une callback pour la réception des messages
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    // Connecté au broker
                    client.subscribe(topic, qos)
                    btnProfil.text = "GOOD"
                }

                override fun connectionLost(cause: Throwable?) {
                    // Connexion perdue
                    btnProfil.text = "NOT GOOD"
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // Message reçu
                    val payload = message?.payload
                    // Faire quelque chose avec le message reçu
                    btnProfil.text = message?.toString()

                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // Message délivré
                }
            })

            // Connecter au broker
            val options = MqttConnectOptions()
            client.connect(options)
        } catch (ex: MqttException) {
            // Gérer l'exception et imprimer le code d'erreur
            println("Erreur de connexion au broker MQTT: ${ex.reasonCode}")
            btnProfil.text = "VERY NOT GOOD"
        }

    }
}