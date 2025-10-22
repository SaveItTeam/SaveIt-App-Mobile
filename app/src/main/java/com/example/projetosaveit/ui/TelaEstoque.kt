package com.example.projetosaveit.ui

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.projetosaveit.R

class TelaEstoque : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_estoque)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras
        if (bundle != null) {
            findViewById<TextView>(R.id.textView14).text = bundle.getString("nomeProduto")
            findViewById<TextView>(R.id.textView19).text = bundle.getString("categoriaProduto")
            findViewById<TextView>(R.id.textView28).text = bundle.getString("marcaProduto")
            findViewById<TextView>(R.id.textView30).text = bundle.getString("descricaoProduto")
            findViewById<TextView>(R.id.textView50).text = bundle.getString("dataEntradaProduto")
            findViewById<TextView>(R.id.textView23).text = bundle.getString("validadeProduto")
            findViewById<TextView>(R.id.textView46).text = bundle.getString("quantidadeProduto")
            findViewById<TextView>(R.id.textView25).text = bundle.getString("skuProduto")

            val quantidadeStr = bundle.getString("quantidadeMostrada")
            val quantidadeMaxStr = bundle.getString("quantidadeMaximaProduto")

            val quantidade = quantidadeStr?.toIntOrNull() ?: 0
            val quantidadeMax = quantidadeMaxStr?.toIntOrNull() ?: 1

            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            progressBar.max = quantidadeMax
            progressBar.progress = quantidade

            val imageUrl = bundle.getString("imagemProduto")
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(findViewById(R.id.imageView28))
            }
        }
    }
}