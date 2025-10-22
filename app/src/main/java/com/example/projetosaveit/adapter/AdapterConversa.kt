package com.example.projetosaveit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.recycleView.Mensagem
import com.example.projetosaveit.adapter.recycleView.TipoMensagem
import com.google.android.material.imageview.ShapeableImageView

class AdapterConversa(private val mensagens: MutableList<Mensagem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ENVIADA = 0
        private const val RECEBIDA = 1
    }

    inner class EnviadaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.mensagem_direita)
        val foto: ShapeableImageView = view.findViewById(R.id.fotoEmpresaDireita)
    }

    inner class RecebidaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.mensagem_esquerda)
        val foto: ShapeableImageView = view.findViewById(R.id.fotoEmpresaEsquerda)
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensagens[position].tipo == TipoMensagem.ENVIADA) ENVIADA else RECEBIDA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == ENVIADA)
            R.layout.bolha_mensagem_direita else R.layout.bolha_mensagem_esquerda
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return if (viewType == ENVIADA) EnviadaViewHolder(view) else RecebidaViewHolder(view)
    }

    override fun getItemCount(): Int = mensagens.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensagem = mensagens[position]
        when (holder) {
            is EnviadaViewHolder -> {
                holder.textView.text = mensagem.texto
                Glide.with(holder.itemView.context)
                    .load(mensagem.fotoUrl)
                    .into(holder.foto)
            }
            is RecebidaViewHolder -> {
                holder.textView.text = mensagem.texto
                Glide.with(holder.itemView.context)
                    .load(mensagem.fotoUrl)
                    .into(holder.foto)
            }
        }
    }

    fun adicionarMensagem(mensagem: Mensagem) {
        mensagens.add(mensagem)
        notifyItemInserted(mensagens.size - 1)
    }
}