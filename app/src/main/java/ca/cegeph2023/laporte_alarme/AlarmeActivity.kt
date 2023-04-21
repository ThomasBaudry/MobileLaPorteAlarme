package ca.cegeph2023.laporte_alarme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AlarmeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarme)

        val btnDismiss: Button = findViewById<Button>(R.id.btn_dismiss)
        btnDismiss.setOnClickListener {
            finish()
        }
    }
}