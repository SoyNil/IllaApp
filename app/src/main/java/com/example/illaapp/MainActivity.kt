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
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Obtener el nombre del usuario
        val userName = auth.currentUser?.email ?: "Usuario"

        // Configurar Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)  // Asignar el Toolbar como ActionBar

        // Configuración del menú lateral
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Actualizar el nombre en la barra lateral
        val headerView = navView.getHeaderView(0)
        val navUserName = headerView.findViewById<TextView>(R.id.navUserName)
        navUserName.text = userName

        // Botones del menú lateral
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_register -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
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

        // Mostrar mensaje de bienvenida
        val welcomeMessage = findViewById<TextView>(R.id.textViewWelcome)
        welcomeMessage.text = "Bienvenido, $userName"

        // Botón "Agregar paciente"
        val btnAgregarPaciente = findViewById<Button>(R.id.btnAgregarPaciente)
        btnAgregarPaciente.setOnClickListener {
            Toast.makeText(this, "Agregar paciente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }
}