package com.example.projetosaveit.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.ui.TelaEstoque
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale


class AdapterProduto : RecyclerView.Adapter<AdapterProduto.ViewHolder>() {
    var listProdutos = listOf<Produto>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): com.example.projetosaveit.adapter.AdapterProduto.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_estoque, parent, false)
        return com.example.projetosaveit.adapter.AdapterProduto.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var produto : Produto = listProdutos.get(position)

        if (produto != null) {
            holder.nomeProduto.setText(produto.name)
            Glide.with(holder.itemView.context)
                .load(produto.image)
                .circleCrop()
                .into(holder.imagem)
            val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            holder.validade.setText("Validade: "+ sdf.format(produto.expirationDate))
            holder.quantidade.setText("Quantidade: " + produto.quantity.toString() + " " + produto.unitMeasure)
        }
        holder.botao.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TelaEstoque::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listProdutos.size;
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nomeProduto : TextView = itemView.findViewById<TextView>(R.id.nomeProdutoTexto)
        var botao : MaterialToolbar = itemView.findViewById<MaterialToolbar>(R.id.botao)
        var imagem : ImageView = itemView.findViewById<ImageView>(R.id.imagemProduto)
        var validade : TextView = itemView.findViewById<TextView>(R.id.validadeTexto)
        var quantidade : TextView = itemView.findViewById<TextView>(R.id.quantidadeTexto)
    }

}