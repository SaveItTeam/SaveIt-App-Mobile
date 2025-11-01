package com.example.projetosaveit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetosaveit.ui.AtualizarNome
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.ui.AdicionarProduto
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ConfiguracoesPerfil : AppCompatActivity() {
    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    var idEmpresa : Long = 0
    val repository : EmpresaRepository = EmpresaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_configuracoes_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = objAutenticar.currentUser?.email.toString()

        GetEmpresa.pegarEmailEmpresa(email) { empresa ->
            if (empresa != null) {
                idEmpresa = empresa.id
                (findViewById<TextView>(R.id.emailUsuarioTxt)).text = empresa.email
            } else {
                GetFuncionario.pegarEmailFunc(email) { func ->
                    if (func != null) {
                        idEmpresa = func.id
                        GetEmpresa.pegarIdEmpresa(func.enterpriseId) { empresa ->
                            if (empresa != null) {
                                (findViewById<TextView>(R.id.emailUsuarioTxt)).text = (empresa.email)
                            }
                        }

                        if (func.isAdmin == false) {
                            findViewById<FrameLayout>(R.id.FrameAlterarNome).visibility = FrameLayout.GONE
                        }
                    }
                }
            }
        }

        (findViewById<Button>(R.id.botaoExcluirConta)).setOnClickListener { v->
            AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim") { _, _ ->
                    deleteConta(idEmpresa)
                    deleteContaFirebase()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


        (findViewById<ConstraintLayout>(R.id.constraintAtualizarSenha)).setOnClickListener { v ->
            val intent = Intent(this, AtualizarSenha::class.java)
            intent.putExtra("origem", "logado")
            startActivity(intent)
        }

        (findViewById<Button>(R.id.botaoLogOut)).setOnClickListener { v ->
            objAutenticar.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        findViewById<FrameLayout>(R.id.FrameAlterarNome).setOnClickListener {
            val intent = Intent(this, AtualizarNome::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btVoltarConfigPerfil).setOnClickListener {
            finish()
        }
    }

    private fun deleteConta(enterpriseId : Long) {
        repository.deleteEmpresa(enterpriseId).enqueue(object : retrofit2.Callback<ResponseBody>{
            override fun onResponse(
                p0: Call<ResponseBody?>,
                p1: Response<ResponseBody?>
            ) {
                if(p1.isSuccessful){
                    Toast.makeText(this@ConfiguracoesPerfil, "Empresa excluido com sucesso" , Toast.LENGTH_LONG).show()
                    finish()
                }else {
                    val errorBodyString = try {
                        p1.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(this@ConfiguracoesPerfil, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                p0: Call<ResponseBody?>,
                p1: Throwable
            ) {
                Toast.makeText(
                    this@ConfiguracoesPerfil,
                    "Api não funcionou: " + p1.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun deleteContaFirebase() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.delete()
            ?.addOnSuccessListener {
                Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, Login::class.java))
                finish()
            }
            ?.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao excluir: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}