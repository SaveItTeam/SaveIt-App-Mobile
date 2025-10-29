package com.example.projetosaveit.ui

import android.content.Intent
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.ProdutoRepository
import com.example.projetosaveit.util.GetEmpresa
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProdutoVitrine : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    private var produtoRepository = ProdutoRepository()
    private var imgEmpresa = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_produto_vitrine)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras
        val idEmpresa = bundle?.getLong("idEmpresa")
        val nomeProduto = bundle?.getString("nomeVitrine")
        val descricaoProduto = bundle?.getString("descricaoVitrine")
        val empresaVitrine = bundle?.getString("empresaVitrine")
        val validadeProduto = bundle?.getString("validadeVitrine")
        val quantidadeVitrine = bundle?.getInt("quantidadeVitrine").toString()
        val pesoVitrine = bundle?.getString("pesoVitrine")
        val localizacaoVitrine = bundle?.getString("localizacaoVitrine")
        val imgProduto = bundle?.getString("imagemVitrine")

        (findViewById<TextView>(R.id.nomeProdutoVItrine)).setText(nomeProduto)
        Glide.with(this)
            .load(imgProduto)
            .into(findViewById<ImageView>(R.id.imgProdutoVitrine))
        (findViewById<TextView>(R.id.descricaoProdutoResult)).setText(descricaoProduto)
        (findViewById<TextView>(R.id.empresaProdutoResult)).setText(empresaVitrine)
        (findViewById<TextView>(R.id.validadeProdutoResult)).setText(validadeProduto)
        (findViewById<TextView>(R.id.pesoProdutoResult)).setText(quantidadeVitrine + " " + pesoVitrine)
        (findViewById<TextView>(R.id.cidadeProdutoResult)).setText(localizacaoVitrine)

        (findViewById<ImageView>(R.id.setaEsquerdaVoltar)).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.BotaoAcessar).setOnClickListener {

            GetEmpresa.pegarIdEmpresa(idEmpresa!!.toLong()) { empresa ->
                if (empresa != null) {
                    imgEmpresa = empresa.enterpriseImage
                }
            }

            val intentChat = Intent(this, Conversa::class.java)
            intentChat.putExtra("empresaFoto", imgEmpresa)
            intentChat.putExtra("empresaId", idEmpresa)
            startActivity(intentChat)
        }
    }
}