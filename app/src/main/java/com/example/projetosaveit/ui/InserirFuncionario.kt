package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.api.repository.FuncionarioRepository
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.FuncionarioInsertDTO
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InserirFuncionario : AppCompatActivity() {

    private val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    private val repository = FuncionarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inserir_funcionario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btCadastrarFunc).setOnClickListener {

            val usuario: FirebaseUser = objAutenticar.currentUser!!
            var id: Long = 0

            GetFuncionario.pegarEmailFunc(usuario.email.toString()) { func ->
                if (func != null) {
                    id = func!!.enterpriseId
                    GetEmpresa.pegarIdEmpresa(id) { empresa ->
                        if (empresa == null) {
                            Toast.makeText(
                                this@InserirFuncionario,
                                "Erro ao obter dados da empresa.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@pegarIdEmpresa
                        } else {
                            val idEmpresa = empresa.id
                            postFuncionario(idEmpresa)
                        }
                    }
                } else {
                    GetEmpresa.pegarEmailEmpresa(usuario.email.toString()) { empresa ->
                        val idEmpresa = empresa!!.id
                        postFuncionario(idEmpresa)
                    }
                }
            }
        }

        findViewById<ImageView>(R.id.voltarInserirFunc).setOnClickListener {
            finish()
        }
    }

    private fun postFuncionario(id: Long) {
        val nome = findViewById<TextInputEditText>(R.id.nomeFunc).text.toString()
        val email = findViewById<TextInputEditText>(R.id.emailFunc).text.toString()
        val senha = findViewById<TextInputEditText>(R.id.senhaFunc).text.toString()

        val tipoCheck = findViewById<RadioGroup>(R.id.radioGrupo)
        val tipoFunc = when (tipoCheck.checkedRadioButtonId) {
            R.id.radioSim -> true
            else -> false
        }

        val funcionario = FuncionarioInsertDTO(
            enterpriseId = id,
            email = email,
            name = nome,
            password = senha,
            isAdmin = tipoFunc
        )

        // Cadastrar funcionário
        repository.postFuncionario(funcionario).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@InserirFuncionario,
                        "Funcionário cadastrado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@InserirFuncionario, MainActivity::class.java)
                    intent.putExtra("plano", 1)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@InserirFuncionario,
                        "Erro na API: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    this@InserirFuncionario,
                    "Erro na API: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}