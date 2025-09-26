package com.example.projetosaveit.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.recycleView.Vitrine
import com.example.projetosaveit.ui.ProdutoVitrine

class AdapterVitrine() : RecyclerView.Adapter<AdapterVitrine.ViewHolder>() {
    var listVitrine = listOf<Vitrine>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): com.example.projetosaveit.adapter.AdapterVitrine.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_vitrine, parent, false)
        return com.example.projetosaveit.adapter.AdapterVitrine.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterVitrine.ViewHolder, position: Int) {
        var itemVitrine : Vitrine = listVitrine.get(position)

        if (itemVitrine != null) {
            Glide.with(holder.itemView.context)
                .load(itemVitrine.image)
                .into(holder.imagemVitrine)
        }
        holder.itemVitrine.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProdutoVitrine::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listVitrine.size
    }

    class ViewHolder(itemView : View)  : RecyclerView.ViewHolder(itemView) {
        var imagemVitrine : ImageView = itemView.findViewById<ImageView>(R.id.imagemVitrine)
        var itemVitrine: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.itemVitrine)
    }
}