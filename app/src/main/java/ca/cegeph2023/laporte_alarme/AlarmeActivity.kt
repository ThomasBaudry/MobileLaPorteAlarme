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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AlarmeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarme)

        // Bouton de retour à l'accueil.
        val btnDismiss: Button = findViewById<Button>(R.id.btn_dismiss)
        btnDismiss.setOnClickListener {
            finish()
        }
    }
}