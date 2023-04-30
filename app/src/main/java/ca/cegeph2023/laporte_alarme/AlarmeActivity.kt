/* Copyright (C) 2023 LaPorte/EPEE
 * All rights reserved.
 *
 * Projet Alarme LaPorte
 * Laporte & EPEE

    @fichier     AlarmeAcctivity.kt
    @auteur      @ThomasBaudry
    @date        2023-04-27
    @description
                Ce fichier sert d'activité lors du déclement de l'alarme.
                * Affiche une page d'alert signifiant qu'une intrusion à été detecté.

                Fonctionnalité non implémenté:
                * Création d'un log suite au déclanchement de l'alarme
                * Notification + alerte sonore
                * Désactivé l'alarme à l'aide d'un NIP.

*/
package ca.cegeph2023.laporte_alarme

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AlarmeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarme)
        // Variable des Buttons
        val btnDismiss: Button = findViewById<Button>(R.id.btn_dismiss)

        // Appel API pour ajouter un LOG
        fun makeHttpRequest(urlString: String): String {
            if (Build.VERSION.SDK_INT > 9) {
                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
            }

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()

            val responseCode = connection.responseCode
            val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                println("Connection API: OK")
                connection.inputStream
            } else {
                println("Connection API: ERROR")
                connection.errorStream
            }

            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }

            bufferedReader.close()
            inputStream.close()
            connection.disconnect()

            return stringBuilder.toString()
        }

        // Bouton de retour à l'accueil.

        btnDismiss.setOnClickListener {
            finish()
        }

        val reponseApi: String = makeHttpRequest(getString(R.string.api_url)+getString(R.string.api_action_addLog))
        println(reponseApi)
    }
}