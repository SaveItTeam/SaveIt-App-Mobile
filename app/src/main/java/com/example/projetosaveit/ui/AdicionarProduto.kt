package com.example.projetosaveit.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.api.repository.LoteRepository
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.ImagemDTO
import com.example.projetosaveit.model.LoteDTO
import com.example.projetosaveit.model.LoteInsertDTO
import com.example.projetosaveit.model.ProdutoDTO
import com.example.projetosaveit.ui.CadastroEndereco
import com.example.projetosaveit.util.GetFuncionario
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdicionarProduto : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val CAMERA_REQUEST_CODE = 100
    private var photoUri: Uri? = null
    private var selectedImageFile: File? = null
    private var selectedImageUri: Uri? = null

    var imageUrl: String = ""
    var publicIdImage: String = ""
    var isInsert = false
    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    val repositoryEmp: EmpresaRepository = EmpresaRepository()
    val repositoryLote: LoteRepository = LoteRepository()
    var idEmpresa: Long = 0


    val pickImageLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                Toast.makeText(this@AdicionarProduto, "Imagem selecionada!", Toast.LENGTH_SHORT).show()
                // binding.imageView.setImageURI(imageUri)
            }
        }
    }

    val permissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            openGallery()
        } else {
            Toast.makeText(this@AdicionarProduto, "Permissão negada!", Toast.LENGTH_SHORT).show()
        }
    }


    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(
                    this@AdicionarProduto,
                    "Permissão de câmera é necessária para tirar fotos.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val config = hashMapOf(
        "cloud_name" to "dxdjsvo0e",
        "api_key" to "515376282616285",
        "api_secret" to "T2BSV6xtsGY9Bk3Cxm4dfhKC7H4"
    )
    val cloudinary = Cloudinary(config)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_produto)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (imageUrl.isNotEmpty() && !isInsert) {
                destroyFromCloudinarySync(publicIdImage)
            }
            previousHandler?.uncaughtException(thread, throwable)
        }

        val activityContext: Context = this
        var emailEmpresa: String = objAutenticar.currentUser?.email.toString()

        pegarEmailEmpresa(emailEmpresa) { empresa ->
            if ((empresa != null)&&(empresa.id.toInt() != 0)) {
                idEmpresa = empresa.id
            }else {
                GetFuncionario.pegarEmailFunc(emailEmpresa) { funcionario ->
                    if ((funcionario != null )&&(funcionario!!.id.toInt() != 0)) {
                        idEmpresa = funcionario!!.enterpriseId
                    }else {
                        Toast.makeText(this@AdicionarProduto, "não foi possivel pegar o id do usuario", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        (findViewById<ImageView>(R.id.imagemProdutoInput)).setOnClickListener { v ->
            showImagePickerOptions()
        }

        (findViewById<ImageView>(R.id.setaEsquerdaVoltarInserirProduto)).setOnClickListener { v ->
            finish()
        }


        val entradaProdutoDate = findViewById<EditText>(R.id.entradaProdutoInput)
        val validadeProdutoDate = findViewById<EditText>(R.id.validadeProdutoInput)
        val unidadeMedidaProduto = findViewById<TextInputEditText>(R.id.unidadeMedidaProdutoInput)
        val categoriaProduto = findViewById<TextInputEditText>(R.id.categoriaProduto)
        val optionsCategory = arrayOf(
            "Embutidos",
            "Laticínios",
            "Grãos",
            "Frutas",
            "Salgados",
            "Doces",
            "Bebidas",
            "Outros"
        )
        val options = arrayOf("L", "KG")


        unidadeMedidaProduto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Selecione uma opção")
                .setItems(options) { dialog, which ->
                    unidadeMedidaProduto.setText(options[which])
                }
                .show()
        }

        categoriaProduto.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Selecione uma opção")
                .setItems(optionsCategory) { dialog, which ->
                    categoriaProduto.setText(optionsCategory[which])
                }
                .show()
        }

        validadeProdutoDate.setOnClickListener {
            val calendario = Calendar.getInstance()
            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dataSelecionada = String.format(
                        "%04d-%02d-%02dT%02d:%02d:%02d",
                        year, month + 1, dayOfMonth, 0, 0, 0
                    )
                    validadeProdutoDate.setText(dataSelecionada)
                },
                ano, mes, dia
            )
            datePicker.show()
        }

        entradaProdutoDate.setOnClickListener {
            val calendario = Calendar.getInstance()
            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // mês começa do zero, então soma +1
                    val dataSelecionada = String.format(
                        "%04d-%02d-%02dT%02d:%02d:%02d",
                        year, month + 1, dayOfMonth, 0, 0, 0
                    )
                    entradaProdutoDate.setText(dataSelecionada)
                },
                ano, mes, dia
            )
            datePicker.show()
        }
        (findViewById<Button>(R.id.botaoCadastrarProduto)).setOnClickListener { v ->

            if (selectedImageFile != null) {
                // Faz o upload APENAS AGORA
                uploadToCloudinary(
                    selectedImageFile!!,
                    onSuccess = { url ->
                        imageUrl = url
                        inserirProdutoCampos()
                    },
                    onError = { e ->
                        Toast.makeText(this, "Erro no upload: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "Selecione uma imagem primeiro!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inserirProdutoCampos() {
        var nomeProduto: String =
            findViewById<TextInputEditText>(R.id.nomeProdutoInput).text.toString()
        var marcaProduto: String =
            findViewById<TextInputEditText>(R.id.marcaProdutoInput).text.toString()
        var skuProduto: String =
            findViewById<TextInputEditText>(R.id.skuProdutoInput).text.toString()
        var descricaoProduto: String =
            findViewById<TextInputEditText>(R.id.descricaoProdutoInput).text.toString()
        var dataEntradaProduto: String =
            findViewById<EditText>(R.id.entradaProdutoInput).text.toString()
        var dataValidadeProduto: String =
            findViewById<EditText>(R.id.validadeProdutoInput).text.toString()
        var quantidadeProduto: String =
            findViewById<EditText>(R.id.quantidadeProdutoInput).text.toString()
        var unidadeMedidaProduto: String =
            findViewById<TextInputEditText>(R.id.unidadeMedidaProdutoInput).text.toString()
        var quantidadeMaximaProduto: String =
            findViewById<EditText>(R.id.quantidadeMaximaProdutoInput).text.toString()
        var categoriaProduto: String =
            findViewById<TextInputEditText>(R.id.categoriaProduto).text.toString()



        if (nomeProduto.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "O nome do produto não pode ser vazio!!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (marcaProduto.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "A marca do produto não pode ser vazia",
                Toast.LENGTH_SHORT
            ).show()
        } else if (skuProduto.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "A SKU do produto não pode ser vazia!!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (dataEntradaProduto.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "A data de entrada não pode ser vazia!!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (dataValidadeProduto.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "A data de validade não pode ser vazia!!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (imageUrl.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "A imagem não pode ser vazia!!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (quantidadeProduto.isEmpty()) {
            Toast.makeText(
                this@AdicionarProduto,
                "a quantidade de produso não pode ser vazia!!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            var produtoInserir: ProdutoDTO = ProdutoDTO(
                0,
                nomeProduto,
                marcaProduto,
                categoriaProduto,
                descricaoProduto,
                idEmpresa.toLong()
            )
            var loteInserir: LoteDTO = LoteDTO(
                0,
                unidadeMedidaProduto,
                dataEntradaProduto,
                skuProduto,
                dataValidadeProduto,
                quantidadeMaximaProduto.toInt(),
                quantidadeProduto.toInt(),
                1
            )
            var imageInserir: ImagemDTO = ImagemDTO(0, imageUrl, 1)
            var lote: LoteInsertDTO = LoteInsertDTO(loteInserir, produtoInserir, imageInserir)
            inserirProduto(lote)
        }
    }

    private fun inserirProduto(lote: LoteInsertDTO) {
        repositoryLote.postLote(lote).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                p0: Call<ResponseBody?>,
                p1: Response<ResponseBody?>
            ) {
                if (p1.isSuccessful) {
                    Toast.makeText(
                        this@AdicionarProduto,
                        "Inserido com sucesso. Resultado: " + p1.body(),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorBodyString = try {
                        p1.errorBody()?.string()
                    } catch (e: Exception) {
                        "Erro desconhecido ao ler o corpo de erro"
                    }

                    Log.e("API_ERROR", "Erro na API: $errorBodyString")
                    Toast.makeText(this@AdicionarProduto, errorBodyString ?: "Erro desconhecido", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                p0: Call<ResponseBody?>,
                p1: Throwable
            ) {
                Toast.makeText(
                    this@AdicionarProduto,
                    "Api não funcionou: " + p1.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun pegarEmailEmpresa(email: String, onResult: (EmpresaDTO?) -> Unit) {
        repositoryEmp.getEmpresa(email).enqueue(object : retrofit2.Callback<EmpresaDTO> {
            override fun onResponse(
                call: Call<EmpresaDTO>,
                response: Response<EmpresaDTO>
            ) {
                onResult(response.body())
            }

            override fun onFailure(call: Call<EmpresaDTO>, t: Throwable) {
                Toast.makeText(
                    this@AdicionarProduto,
                    "Erro na API: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onResult(null)
            }
        })
    }

    private fun destroyFromCloudinarySync(publicId: String) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
            Log.d("Cloudinary", "Destroy sync OK: $publicId")
        } catch (e: Exception) {
            Log.e("Cloudinary", "Erro ao excluir imagem no destroySync: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val avatar = findViewById<ImageView>(R.id.imagemProdutoInput)

            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        selectedImageFile = getFileFromUri(selectedImageUri!!)
                        Glide.with(this).load(selectedImageUri).circleCrop().into(avatar)
                    }
                }

                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        val tempFile = File(cacheDir, "temp_${System.currentTimeMillis()}.jpg")
                        FileOutputStream(tempFile).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                        selectedImageFile = tempFile
                        Glide.with(this).load(tempFile).circleCrop().into(avatar)
                    }
                }
            }
        }
    }

    private fun uploadToCloudinary(file: File, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        Thread {
            try {
                val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                val url = result["secure_url"] as String
                publicIdImage = result["public_id"] as String
                runOnUiThread { onSuccess(url) }
            } catch (e: Exception) {
                runOnUiThread { onError(e) }
            }
        }.start()
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("gallery_image_", ".jpg", cacheDir)
            inputStream?.use { input -> FileOutputStream(tempFile).use { output -> input.copyTo(output) } }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Tirar foto", "Escolher da galeria")
        AlertDialog.Builder(this)
            .setTitle("Selecione uma opção")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }
}