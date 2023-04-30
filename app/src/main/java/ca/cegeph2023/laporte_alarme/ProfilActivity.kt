package ca.cegeph2023.laporte_alarme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val btnRetour: Button = findViewById<Button>(R.id.ButtonRetour)
        btnRetour.setOnClickListener {
            finish()
        }
    }
}