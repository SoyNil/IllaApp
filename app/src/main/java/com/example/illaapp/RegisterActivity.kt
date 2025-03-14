package com.example.illaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Usuarios")

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etNombreUsuario = findViewById<EditText>(R.id.etNombreUsuario)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContraseña = findViewById<EditText>(R.id.etContraseña)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val apellido = etApellido.text.toString()
            val nombreUsuario = etNombreUsuario.text.toString()
            val correo = etCorreo.text.toString()
            val contraseña = etContraseña.text.toString()

            if (nombre.isNotEmpty() && apellido.isNotEmpty() && nombreUsuario.isNotEmpty() && correo.isNotEmpty() && contraseña.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            database.get().addOnSuccessListener { snapshot ->
                                val cantidadPsicologos = snapshot.childrenCount.toInt()
                                val nuevoId = cantidadPsicologos + 1
                                val psicologoNumero = "Psicologo$nuevoId" // Cambiado para ser el nombre del nodo
                                val ocupacion = 2

                                val user = hashMapOf(
                                    "id" to nuevoId,
                                    "nombre" to nombre,
                                    "apellido" to apellido,
                                    "nombreUsuario" to nombreUsuario,
                                    "correo" to correo,
                                    "ocupacion" to ocupacion
                                )

                                database.child(psicologoNumero).setValue(user) // Cambiado el nombre del nodo
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Error al registrar en la base de datos", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
