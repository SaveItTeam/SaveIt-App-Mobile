package com.example.projetosaveit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.recycleView.MensagemChatbot
import com.example.projetosaveit.adapter.recycleView.TipoMensagemChatbot

class AdapterChatbot(
    private val mensagens: MutableList<MensagemChatbot>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_USUARIO = 1
    private val VIEW_BOT = 2

    override fun getItemViewType(position: Int): Int {
        return when (mensagens[position].tipo) {
            TipoMensagemChatbot.USUARIO -> VIEW_USUARIO
            TipoMensagemChatbot.BOT -> VIEW_BOT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_USUARIO) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bolha_mensagem_direita, parent, false)
            UsuarioViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.bolha_mensagem_esquerda, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensagem = mensagens[position]
        when (holder) {
            is UsuarioViewHolder -> holder.msg.text = mensagem.texto
            is BotViewHolder -> holder.msg.text = mensagem.texto
        }
    }

    override fun getItemCount(): Int = mensagens.size

    fun adicionarMensagem(mensagem: MensagemChatbot) {
        mensagens.add(mensagem)
        notifyItemInserted(mensagens.size - 1)
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.mensagem_direita)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.mensagem_esquerda)
    }
}