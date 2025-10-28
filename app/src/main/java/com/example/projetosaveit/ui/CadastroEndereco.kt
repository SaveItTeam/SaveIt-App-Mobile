package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.EmpresaInsertDTO
import com.example.projetosaveit.model.EnderecoDTO
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class CadastroEndereco : AppCompatActivity() {
    val autenticacao : FirebaseAuth = FirebaseAuth.getInstance()
    val repository : EmpresaRepository = EmpresaRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro_endereco)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = intent.extras

        val nomeEmpresa = bundle?.getString("nomeEmpresa")
        val cnpjEmpresa = bundle?.getString("cnpjEmpresa")
        val emailEmpresa = bundle?.getString("emailEmpresa")
        val telefoneEmpresa = bundle?.getString("telefoneEmpresa")
        val senhaEmpresa = bundle?.getString("senhaEmpresa")


        (findViewById<Button>(R.id.btnCadastroEmpresa)).setOnClickListener {
            val cepEmpresa : String = findViewById<TextInputEditText>(R.id.cepEmpresaInput).text.toString()
            val ruaEmpresa : String = findViewById<TextInputEditText>(R.id.ruaEmpresaInput).text.toString()
            val bairroEmpresa : String = findViewById<TextInputEditText>(R.id.bairroEmpresaInput).text.toString()
            val numeroEmpresa : String = findViewById<TextInputEditText>(R.id.numeroEmpresaInput).text.toString()
            val cidadeEmpresa : String = findViewById<TextInputEditText>(R.id.cidadeEmpresaInput).text.toString()
            val estadoEmpresa : String = findViewById<TextInputEditText>(R.id.estadoEmpresaInput).text.toString()
            val complementoEmpresa : String = findViewById<TextInputEditText>(R.id.complementoEmpresaInput).text.toString()

            if (cepEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "CEP não pode ser vazio", Toast.LENGTH_LONG).show()
            }else if(ruaEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "A rua da empresa não pode ser vazia", Toast.LENGTH_LONG).show()
            }else if(bairroEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "O bairro da empresa não pode ser vazio", Toast.LENGTH_LONG).show()
            }else if(numeroEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "O número da empresa não pode ser vazio", Toast.LENGTH_LONG).show()
            }else if(cidadeEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "A cidade da empresa não pode ser vazio", Toast.LENGTH_LONG).show()
            }else if (estadoEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "O estado da empresa não pode ser vazio", Toast.LENGTH_LONG).show()
            }else if (complementoEmpresa.isEmpty()) {
                Toast.makeText(this@CadastroEndereco, "O complemento da empresa não pode ser vazio",
                    Toast.LENGTH_LONG).show()
            }else {
                val empresaDTO : EmpresaDTO = EmpresaDTO(0,cnpjEmpresa.toString(), nomeEmpresa.toString(), emailEmpresa.toString(), planId = 2, "", telefoneEmpresa.toString(), addressId = 0, senhaEmpresa.toString())
                val enderecoDTO : EnderecoDTO = EnderecoDTO(0,estadoEmpresa.toString(), cidadeEmpresa.toString(), ruaEmpresa.toString(), cepEmpresa.toString(), bairroEmpresa.toString(), complementoEmpresa.toString(), numeroEmpresa.toInt())
                val empresaInsertDTO : EmpresaInsertDTO = EmpresaInsertDTO(empresaDTO, enderecoDTO)
                postEmpresa(empresaInsertDTO)
            }

            autenticacao.createUserWithEmailAndPassword(
                emailEmpresa.toString(),
                senhaEmpresa.toString()
            )
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = autenticacao.getCurrentUser()

                        val profileUpdate: UserProfileChangeRequest =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(nomeEmpresa)
                                .build()

                        user?.updateProfile(profileUpdate)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Empresa cadastrada com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("plano", 0)
                                    intent.putExtra("tipoFunc", false)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar empresa!", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("CadastroIndustria", "Erro: ${task.exception?.message}")
                    }
                }
        }
    }


    fun postEmpresa(empresaInsert : EmpresaInsertDTO) {
        repository.postEmpresa(empresaInsert).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                p0: Call<ResponseBody?>,
                p1: Response<ResponseBody?>
            ) {
                if(p1.isSuccessful) {
                    Toast.makeText(this@CadastroEndereco, "Empresa cadastrada com sucesso!", Toast.LENGTH_LONG).show()
                }else {
                    val errorBodyString = try {
                        p1.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(this@CadastroEndereco, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                p0: Call<ResponseBody?>,
                p1: Throwable
            ) {
                Toast.makeText(this@CadastroEndereco, "Deu erro na API: " + p1.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}