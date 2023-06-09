/* Copyright (C) 2023 LaPorte/EPEE
 * All rights reserved.
 *
 * Projet Alarme LaPorte
 * Laporte & EPEE

    @fichier     LogsActivity.kt
    @auteur      @ThomasBaudry
    @date        2023-04-27
    @description
                Ce fichier sert d'activité pour voir la liste des Logs.
                * Appel l'API pour récupèrer les différents Logs.
                * Display la liste dans un tableau.

*/
package ca.cegeph2023.laporte_alarme

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LogsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        // Variable
        val btnRetour: Button = findViewById<Button>(R.id.buttonBack)
        val langue = intent.getStringExtra("langue")
        //Affichage Langue
        //Affichage du texte avec la langue selectionné
        if(langue == "French"){
            btnRetour.text = getString(R.string.retour_fr)
        }else{
            btnRetour.text = getString(R.string.back_en)
        }

        // Appel à l'API pour récupéré les logs.
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

        // Afficher les Logs dynamiquement sur la page Logs.
        fun afficherLogs(listeLogs: List<Logs>) {
            val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

            // Supprimer toutes les lignes existantes
            tableLayout.removeAllViews()

            // Ajouter les entêtes de colonnes
            val entetes = arrayOf("Date", "Heure", "Description")
            val enteteRow = TableRow(this)
            for (entete in entetes) {
                val textView = TextView(this)
                textView.text = entete
                enteteRow.addView(textView)
            }
            tableLayout.addView(enteteRow)

            // Ajouter les données
            for (log in listeLogs) {
                val tableRow = TableRow(this)
                val textViewDate = TextView(this)
                val textViewHeure = TextView(this)
                val textViewDescription = TextView(this)

                textViewDate.text = log.date
                textViewHeure.text = log.hour
                textViewDescription.text = log.description

                tableRow.addView(textViewDate)
                tableRow.addView(textViewHeure)
                tableRow.addView(textViewDescription)

                tableLayout.addView(tableRow)
            }
        }

        val reponseApi: String = makeHttpRequest(getString(R.string.api_url)+getString(R.string.api_action_getListLogs))
        println(reponseApi)
        //Gestion JSON
        val jsonArray = JSONArray(reponseApi)
        val data = mutableListOf<Logs>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getString("id")
            val date = jsonObject.getString("date")
            val hour = jsonObject.getString("hour")
            val description = jsonObject.getString("description")
            val item = Logs(id, date, hour, description)
            data.add(item)
        }
        val listeLogs = data.toList()
        afficherLogs(listeLogs)

        // Retour a la page d'Accueil
        btnRetour.setOnClickListener {
            finish()
        }

    }
}