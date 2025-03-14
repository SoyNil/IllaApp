package com.example.illaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContraseña = findViewById<EditText>(R.id.etContraseña)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)

        btnIniciarSesion.setOnClickListener {
            val correo = etCorreo.text.toString()
            val contraseña = etContraseña.text.toString()

            if (correo.isNotEmpty() && contraseña.isNotEmpty()) {
                auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
