package com.example.projetosaveit.adapter

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import java.util.Date
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.api.repository.EstoqueRepository
import com.example.projetosaveit.api.repository.LoteRepository
import com.example.projetosaveit.api.repository.VitrineRepository
import com.example.projetosaveit.model.EstoqueDTO
import com.example.projetosaveit.model.ProdutoInfoDTO
import com.example.projetosaveit.model.VitrineDTO
import com.example.projetosaveit.model.VitrineInsertDTO
import com.example.projetosaveit.ui.TelaEstoque
import com.google.android.material.appbar.MaterialToolbar
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale


class AdapterProduto : RecyclerView.Adapter<AdapterProduto.ViewHolder>() {
    var listProdutos = listOf<Produto>()
    var repository : LoteRepository = LoteRepository()
    var repositoryVitrine : VitrineRepository = VitrineRepository()
    var repositoryEstoque : EstoqueRepository = EstoqueRepository()

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
            holder.quantidade.setText("Quantidade: " + produto.quantity.toString())
        }
        holder.botao.setOnClickListener {
            if (produto == null) {
                Toast.makeText(holder.itemView.context, "Produto nulo", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val context = holder.itemView.context
            val activity = context as? android.app.Activity
            val activityName = activity?.javaClass?.simpleName ?: context::class.java.simpleName

            when {
                activityName == "AdicionarProdutoVitrine" -> {
                    val dialogView = activity?.layoutInflater?.inflate(R.layout.dialog_card_view, null)
                    val builder = AlertDialog.Builder(activity).setView(dialogView)
                    val dialog = builder.create()

                    val cancelBtn = dialogView!!.findViewById<Button>(R.id.button)
                    val saveBtn = dialogView.findViewById<Button>(R.id.button3)
                    val editQuantidade = dialogView.findViewById<EditText>(R.id.editTextNumber)

                    cancelBtn.setOnClickListener { dialog.dismiss() }
                    saveBtn.setOnClickListener {
                        val quantidade = editQuantidade.text.toString().toInt()
                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                        val currentDate = sdf.format(Date())

                        val vitrineInsert = VitrineInsertDTO("", produto.batchId, quantidade, currentDate)

                        repositoryVitrine.postVitrine(vitrineInsert).enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onResponse(
                                call: retrofit2.Call<ResponseBody>,
                                response: retrofit2.Response<ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    val apenasNumero = produto.quantity.filter { it.isDigit() }
                                    val quantidadeAtual = apenasNumero.toInt()
                                    Toast.makeText(holder.itemView.context, "Produto adicionado à vitrine com sucesso!", Toast.LENGTH_LONG).show()
                                    var estoque : EstoqueDTO = EstoqueDTO(0, 0, quantidadeAtual - quantidade, produto.batchId, produto.id, 0, "", currentDate)
                                    repositoryEstoque.postEstoque(estoque).enqueue(object : retrofit2.Callback<ResponseBody> {
                                        override fun onResponse(
                                            call: retrofit2.Call<ResponseBody>,
                                            response: retrofit2.Response<ResponseBody>
                                        ) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(holder.itemView.context, "O estoque foi atualizado junto com inserção na vitrine", Toast.LENGTH_LONG).show()
                                            }else {
                                                val errorBodyString = try {
                                                    response.errorBody()?.string()
                                                } catch (e: Exception) {
                                                    "Erro desconhecido ao ler o corpo de erro"
                                                }

                                                Log.e("API_ERROR", "Erro na API: $errorBodyString")
                                                Toast.makeText(context, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                                            }
                                        }

                                        override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                                            Toast.makeText(holder.itemView.context, "Erro ao atualizar estoque: ${t.message}", Toast.LENGTH_LONG).show()
                                        }
                                    })
                                }else {
                                    val errorBodyString = try {
                                        response.errorBody()?.string()
                                    } catch (e: Exception) {
                                        "Erro desconhecido ao ler o corpo de erro"
                                    }

                                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                                    Toast.makeText(context, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                                }
                            }

                            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(holder.itemView.context, "Erro ao adicionar à vitrine: ${t.message}", Toast.LENGTH_LONG).show()
                            }
                        })

                        dialog.dismiss()
                    }

                    dialog.show()
                    dialog.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

                }

                else -> {
                    repository.getProdutoId(produto.batchId).enqueue(object : retrofit2.Callback<ProdutoInfoDTO> {
                        override fun onResponse(
                            call: retrofit2.Call<ProdutoInfoDTO>,
                            response: retrofit2.Response<ProdutoInfoDTO>
                        ) {
                            if (response.isSuccessful) {
                                val produto : ProdutoInfoDTO? = response.body()

                                val bundle : Bundle = Bundle()
                                val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                                try {
                                    val expirationDate = produto?.batchResponseDTO?.expirationDate
                                    val entryDate = produto?.batchResponseDTO?.entryDate

                                    val parsedExpiration = expirationDate?.let { apiDateFormat.parse(it) }
                                    val parsedEntry = entryDate?.let { apiDateFormat.parse(it) }

                                    bundle.putString("validadeProduto", parsedExpiration?.let { displayDateFormat.format(it) })
                                    bundle.putString("dataEntradaProduto", parsedEntry?.let { displayDateFormat.format(it) })
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                bundle.putString("nomeProduto",produto?.productResponseDTO?.name)
                                bundle.putString("categoriaProduto", produto?.productResponseDTO?.category)
                                bundle.putString("descricaoProduto", produto?.productResponseDTO?.description)
                                bundle.putString("imagemProduto", produto?.imageResponseDTO?.image)
                                bundle.putString("quantidadeProduto", produto?.batchResponseDTO?.quantity.toString())
                                bundle.putString("quantidadeMaximaProduto", produto?.batchResponseDTO?.maxQuantity.toString())
                                bundle.putString("quantidadeMostrada", produto?.batchResponseDTO?.quantity.toString())
                                bundle.putString("marcaProduto", produto?.productResponseDTO?.brand)
                                bundle.putString("skuProduto", produto?.batchResponseDTO?.batchCode)
                                bundle.putLong("idProduto", produto?.productResponseDTO?.id!!)

                                val intent : Intent = Intent(holder.itemView.context, TelaEstoque::class.java)
                                intent.putExtras(bundle)
                                holder.itemView.context.startActivity(intent)

                            }
                        }

                        override fun onFailure(call: retrofit2.Call<ProdutoInfoDTO>, t: Throwable) {
                            Toast.makeText(holder.itemView.context, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
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