package com.example.projetosaveit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetosaveit.R

data class ConversaItem(val titulo: String, val sessionId: String)

class AdapterSessoes(
    private val conversas: MutableList<ConversaItem>,
    private val onClick: (ConversaItem) -> Unit
) : RecyclerView.Adapter<AdapterSessoes.ConversaViewHolder>() {

    inner class ConversaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.textTituloConversa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversa_drawer, parent, false)
        return ConversaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversaViewHolder, position: Int) {
        val item = conversas[position]
        holder.tituloTextView.text = item.titulo
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = conversas.size

    fun adicionarConversa(titulo: String, sessionId: String) {
        conversas.add(ConversaItem(titulo, sessionId))
        notifyItemInserted(conversas.size - 1)
    }

    fun limpar() {
        conversas.clear()
        notifyDataSetChanged()
    }
}