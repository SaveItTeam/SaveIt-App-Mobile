package com.example.projetosaveit.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.ChatRepository
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.model.ChatDTO
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.ui.Conversa
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterChat : RecyclerView.Adapter<AdapterChat.ViewHolder>() {
    var listChats = mutableListOf<ChatDTO>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = listChats[position]

        holder.ultimaMsg.text = chat.text ?: ""

        val empresa = chat.empresa
        if (empresa != null) {
            holder.nome.text = empresa.name
            Glide.with(holder.itemView.context)
                .load(empresa.enterpriseImage)
//                .placeholder(R.drawable.placeholder_img)
                .into(holder.imagem)
        } else {
            holder.nome.text = "Carregando"
//            holder.imagem.setImageResource(R.drawable.placeholder_empresa)
        }

        holder.itemConversa.setOnClickListener {
            val intent = Intent(holder.itemView.context, Conversa::class.java)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listChats.size;
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nome : TextView = itemView.findViewById<TextView>(R.id.nomeEmpresaCard)
        var imagem : ImageView = itemView.findViewById<ImageView>(R.id.imgCardEmpresa)
        var ultimaMsg : TextView = itemView.findViewById<TextView>(R.id.ultimaMsg)
        var itemConversa : ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.itemConversa)
    }
}