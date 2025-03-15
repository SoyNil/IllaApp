package com.example.illaapp

data class Paciente(
    val ID: String = "",
    val DNI: String = "",
    val Nombre: String = "",
    val Fecha: String = "",
    val Precio: Double = 0.0,
    val Descuento: Double = 0.0,
    val Usuario_ID: String = "",
    val Atendido: Int = 0,
    val Historial_Clinico: Int = 0,
    val Precio_Descuento: Double = 0.0,
    val fecha_actual: Long = 0
)
