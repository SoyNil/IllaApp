package com.example.illaapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(private val pacientes: List<Paciente>) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    class PatientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.tvNombrePaciente)
        val dniTextView: TextView = view.findViewById(R.id.tvDNI)
        val fechaCitaTextView: TextView = view.findViewById(R.id.tvFechaCita)
        val precioTextView: TextView = view.findViewById(R.id.tvPrecio)
        val descuentoTextView: TextView = view.findViewById(R.id.tvDescuento)
        val precioDescuentoTextView: TextView = view.findViewById(R.id.tvPrecioDescuento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_paciente, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val paciente = pacientes[position]

        holder.nombreTextView.text = paciente.Nombre
        holder.dniTextView.text = "DNI: ${paciente.DNI}"
        holder.fechaCitaTextView.text = "Fecha Cita: ${paciente.Fecha}"
        holder.precioTextView.text = "Precio: ${paciente.Precio}"
        holder.descuentoTextView.text = "Descuento: ${paciente.Descuento}%"
        holder.precioDescuentoTextView.text = "Precio Final: ${paciente.Precio_Descuento}"
    }

    override fun getItemCount() = pacientes.size
}
