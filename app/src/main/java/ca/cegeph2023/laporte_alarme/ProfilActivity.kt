package ca.cegeph2023.laporte_alarme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        // Variable
        val btnRetour: Button = findViewById<Button>(R.id.ButtonRetour)
        val langue = intent.getStringExtra("langue")
        val courrielLabel = findViewById<TextView>(R.id.courrielLabel)
        val nomLabel = findViewById<TextView>(R.id.nomLabel)

        //Affichage du texte avec la langue selectionné
        if(langue == "French"){
            btnRetour.text = getString(R.string.retour_fr)
            courrielLabel.text = getString(R.string.courrielLabel_fr)
            nomLabel.text = getString(R.string.nomLabel_fr)
        }else{
            btnRetour.text = getString(R.string.back_en)
            courrielLabel.text = getString(R.string.mailLabel_en)
            nomLabel.text = getString(R.string.nameLabel_en)
        }

        // Bouton de retour.
        btnRetour.setOnClickListener {
            finish()
        }
    }
}