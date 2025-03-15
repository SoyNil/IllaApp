package com.example.illaapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.widget.Toolbar
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(this, "Usuario no autenticado. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("Usuarios")
        val navView = findViewById<NavigationView>(R.id.navView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val welcomeMessage = findViewById<TextView>(R.id.textViewWelcome)

        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Obtener el nombre del usuario desde Firebase
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nombre = snapshot.child("nombre").getValue(String::class.java) ?: "Usuario"

                    // Actualizar mensaje de bienvenida
                    welcomeMessage.text = "Bienvenido, $nombre"

                    // También actualizar el nombre en la barra lateral
                    val headerView = navView.getHeaderView(0)
                    val navUserName = headerView.findViewById<TextView>(R.id.navUserName)
                    navUserName.text = nombre
                } else {
                    Toast.makeText(this@MainActivity, "No se encontró el usuario.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al obtener el nombre del usuario: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    drawerLayout.closeDrawers()
                }
                R.id.nav_view_user -> Toast.makeText(this, "Ver usuario", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }

        val btnAgregarPaciente = findViewById<Button>(R.id.btnAgregarPaciente)
        btnAgregarPaciente.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_agregar_paciente, null)

            val dniField = view.findViewById<EditText>(R.id.etDNI)
            val nombreField = view.findViewById<EditText>(R.id.etNombrePaciente)
            val fechaCitaField = view.findViewById<EditText>(R.id.etFechaCita)
            val precioField = view.findViewById<EditText>(R.id.etPrecio)
            val porcentajeField = view.findViewById<EditText>(R.id.etPorcentajeGanancia)

            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Agregar Paciente")
                .setNegativeButton("Cancelar", null)
                .create()

            view.findViewById<Button>(R.id.btnGuardarPaciente).setOnClickListener {
                val dni = dniField.text.toString()
                val nombre = nombreField.text.toString()
                val fechaCita = fechaCitaField.text.toString()
                val precio = precioField.text.toString().toDoubleOrNull() ?: 0.0
                val porcentaje = porcentajeField.text.toString().toDoubleOrNull() ?: 0.0
                val precioDescuento = precio - (precio * (porcentaje / 100))
                val fechaActual = System.currentTimeMillis()

                if (dni.isNotEmpty() && nombre.isNotEmpty() && fechaCita.isNotEmpty()) {
                    val pacientesRef = FirebaseDatabase.getInstance().getReference("Pacientes")
                    val pacienteId = pacientesRef.push().key!!

                    val pacienteData = hashMapOf(
                        "ID" to pacienteId,
                        "DNI" to dni,
                        "Nombre" to nombre,
                        "Fecha" to fechaCita,
                        "Precio" to precio,
                        "Descuento" to porcentaje,
                        "Usuario_ID" to userId,
                        "Atendido" to 0,
                        "Historial_Clinico" to 0,
                        "Precio_Descuento" to precioDescuento,
                        "fecha_actual" to fechaActual
                    )

                    pacientesRef.child(pacienteId).setValue(pacienteData).addOnSuccessListener {
                        Toast.makeText(this, "Paciente guardado exitosamente", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error al guardar paciente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPacientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val pacientesList = mutableListOf<Paciente>()
        val databaseRef = FirebaseDatabase.getInstance().getReference("Pacientes")

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (pacienteSnapshot in snapshot.children) {
                    val paciente = pacienteSnapshot.getValue(Paciente::class.java)
                    if (paciente != null && paciente.Usuario_ID == userId) {
                        pacientesList.add(paciente)
                    }
                }
                val adapter = PatientAdapter(pacientesList)
                recyclerView.adapter = adapter
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar pacientes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }
}
