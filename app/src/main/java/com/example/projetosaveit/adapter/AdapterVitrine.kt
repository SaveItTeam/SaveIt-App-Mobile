package com.example.projetosaveit.adapter

import android.content.Intent
import android.os.Bundle
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
import com.example.projetosaveit.adapter.recycleView.Vitrine
import com.example.projetosaveit.api.repository.VitrineRepository
import com.example.projetosaveit.model.VitrineDTO
import com.example.projetosaveit.ui.ProdutoVitrine
import java.text.SimpleDateFormat
import java.util.Locale

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
        holder.nomeVitrine.setText(itemVitrine.name)
        Glide.with(holder.itemView.context)
            .load(itemVitrine.image)
            .into(holder.imagemVitrine)
        holder.itemVitrine.setOnClickListener {
            val repository : VitrineRepository = VitrineRepository()
            repository.getVitrine(itemVitrine.id).enqueue(object : retrofit2.Callback<VitrineDTO> {
                override fun onResponse(
                    call: retrofit2.Call<VitrineDTO>,
                    response: retrofit2.Response<VitrineDTO>
                ) {
                    if (response.isSuccessful) {
                        val context = holder.itemView.context
                        val intent = Intent(context, ProdutoVitrine::class.java)

                        val bundle : Bundle = Bundle()
                        bundle.putLong("idEmpresa", response.body()?.enterpriseId ?: 0L)
                        bundle.putLong("idVitrine", itemVitrine.id)
                        bundle.putString("nomeVitrine", itemVitrine.name)
                        bundle.putString("imagemVitrine", itemVitrine.image)
                        bundle.putString("descricaoVitrine", response.body()?.description)
                        bundle.putString("localizacaoVitrine", response.body()?.localizacao)
                        bundle.putString("empresaVitrine", response.body()?.empresa)
                        bundle.putInt("quantidadeVitrine", response.body()?.quantidadeGeral ?: 0)
                        val validadeOriginal = response.body()?.validade
                        val sdfEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val sdfSaida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val validadeFormatada = try {
                            validadeOriginal?.let { sdfSaida.format(sdfEntrada.parse(it)!!) }
                        } catch (e: Exception) {
                            null
                        }

                        bundle.putString("validadeVitrine", validadeFormatada ?: "Data inválida")
                        bundle.putString("pesoVitrine", response.body()?.tipoPeso)
                        intent.putExtras(bundle)

                        context.startActivity(intent)
                    }else {
                        Toast.makeText(holder.itemView.context, "Erro: " + response.code(), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<VitrineDTO>, t: Throwable) {
                    Toast.makeText(holder.itemView.context, "Erro: " + t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return listVitrine.size
    }

    class ViewHolder(itemView : View)  : RecyclerView.ViewHolder(itemView) {
        var imagemVitrine : ImageView = itemView.findViewById<ImageView>(R.id.imagemVitrine)
        var nomeVitrine : TextView = itemView.findViewById<TextView>(R.id.nomeProdutoTxt)
        var itemVitrine: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.itemVitrine)
    }
}