/* Copyright (C) 2023 LaPorte/EPEE
 * All rights reserved.
 *
 * Projet Alarme LaPorte
 * Laporte & EPEE

    @fichier     MainActivity.kt
    @auteur      @ThomasBaudry
    @date        2023-04-27
    @description
                Ce fichier sert d'activité Principale de l'application Mobile.
                * Ecoute sur le topic du Broker, en cas de mouvement déclenche l'activité Alarme.
                * Permet d'activé ou non le système d'alarme.
                * Permet d'accedé a la page des logs.
                * Voir la date et l'heure actuel.
                * En cas de déclenchement d'alarme crée un logs dans l'api.

*/
package ca.cegeph2023.laporte_alarme

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.Editable
import android.text.TextWatcher
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
//Classe Logs
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

        // Variable Button et TextView
        val btnAlarm: Button = findViewById(R.id.btn_setAlarme)
        val alarmeState = findViewById<TextView>(R.id.label_alarmeState)
        val btnLogs: Button = findViewById(R.id.btn_logs)
        val dateTimeLabel = findViewById<TextView>(R.id.label_date)
        val hourTimeLabel = findViewById<TextView>(R.id.label_hour)
        val btnProfil: Button = findViewById(R.id.btn_profil)

        // Variable Date et Heure
        val handler = Handler()
        val delay = 1000L
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.CANADA_FRENCH)
        val hourFormat = SimpleDateFormat("hh:mm", Locale.CANADA_FRENCH)

        // Variable Connextion MQTT.
        val serverUri = getString(R.string.mqtt_url)
        val clientId = getString(R.string.mqtt_client)
        val topic = getString(R.string.mqtt_topic)
        val qos = 2 // Quality of Service

        // Detection Declenchement Alarme
        alarmeState.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Le texte a été modifié
                println("Le texte a été modifié : $s")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Avant que le texte ne soit modifié
                if (!btnAlarm.isSelected) {
                    val intent = Intent(this@MainActivity, AlarmeActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Pendant que le texte est modifié
            }
        })

        // Button Alarme OFF/ON
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
        btnLogs.setOnClickListener {
            val intent = Intent(this, LogsActivity::class.java)
            startActivity(intent)
        }

        // Affiche la Date et Heure actuel.
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
                    if(message?.toString() == "Motion Detected!") {
                        alarmeState.text = message?.toString()
                    }
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
        } catch (ex: MqttException) {
            // Gérer l'exception et imprimer le code d'erreur
            println("MQTT Connection : Erreur de connexion au broker MQTT: ${ex.reasonCode}")
        }
    }
}