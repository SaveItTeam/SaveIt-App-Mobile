package com.example.projetosaveit.ui.perfil

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.projetosaveit.R
import com.example.projetosaveit.api.repository.EmpresaRepository
import com.example.projetosaveit.api.repository.ImagemRepository
import com.example.projetosaveit.ui.AdicionarProduto
import com.example.projetosaveit.ui.ConfiguracoesPerfil
import com.example.projetosaveit.util.GetEmpresa
import com.example.projetosaveit.ui.InserirFuncionario
import com.example.projetosaveit.ui.Planos
import com.example.projetosaveit.ui.chatbot.Chatbot
import com.example.projetosaveit.util.GetFuncionario
import com.google.firebase.auth.FirebaseAuth
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class Perfil : Fragment() {

    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()
    val imageURl : String = ""
    var imagePublicId : String = ""
    val repository : ImagemRepository = ImagemRepository()
    val repositoryEmpresa : EmpresaRepository = EmpresaRepository()
    var idEmpresa : Long = 0
    var imagemAtual : String = ""

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val REQUEST_IMAGE_PICK = 1001

    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_REQUEST_CODE = 100
    private var photoUri: Uri? = null
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {

                openCamera()
            } else {

                Toast.makeText(requireContext(), "Permissão de câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show()
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

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    Toast.makeText(requireContext(), "Imagem selecionada!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configura o launcher de permissão
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permissão negada!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)
        var plano: String
        val email = objAutenticar.currentUser?.email

        GetEmpresa.pegarEmailEmpresa(email.toString()) { empresa ->
            if (empresa != null) {
                idEmpresa = empresa.id
                view.findViewById<TextView>(R.id.nomePerfil).text = empresa.name

                if (empresa.planId == 1) {
                    view.findViewById<FrameLayout>(R.id.FrameInserirFunc).visibility = View.GONE
                    plano = "Plano Atual: Nenhum"
                    view.findViewById<LinearLayout>(R.id.btChatbot).visibility = View.GONE
                } else {
                    plano = "Plano Atual: SaveIt Pro"
                    view.findViewById<LinearLayout>(R.id.btChatbot).visibility = View.VISIBLE
                }

                view.findViewById<TextView>(R.id.planoAtual).text = plano

                pegarImagemEmpresa(idEmpresa)
            } else {
                GetFuncionario.pegarEmailFunc(email.toString()) { func ->
                    if (func != null) {
                        val id = func.enterpriseId.toLong()
                        GetEmpresa.pegarIdEmpresa(id) { empresa ->
                            if (empresa != null) {
                                idEmpresa = empresa.id
                                view.findViewById<TextView>(R.id.nomePerfil).text = empresa.name

                                val plano = "Plano Atual: SaveIt Pro"
                                view.findViewById<TextView>(R.id.planoAtual).text = plano

                                pegarImagemEmpresa(idEmpresa)

                                if (func.isAdmin == true) {
                                    view.findViewById<FrameLayout>(R.id.FrameInserirFunc).visibility = View.VISIBLE
                                } else {
                                    view.findViewById<FrameLayout>(R.id.FrameInserirFunc).visibility = View.GONE
                                    view.findViewById<FrameLayout>(R.id.FramePlanos).visibility = View.GONE
                                }

                            } else {
                                Toast.makeText(context, "Empresa não encontrada.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Funcionário não encontrado.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        val avatar = view.findViewById<ImageView>(R.id.imagemPerfil)
        avatar.setOnClickListener {
            showImagePickerOptions()
        }

        view.findViewById<LinearLayout>(R.id.btChatbot).setOnClickListener {
            val intent = Intent(this.activity, Chatbot::class.java)
            startActivity(intent)
        }

        view.findViewById<ConstraintLayout>(R.id.btConfiguracoes).setOnClickListener {
            val intent = Intent(requireContext(), ConfiguracoesPerfil::class.java)
            startActivity(intent)
        }

        view.findViewById<ConstraintLayout>(R.id.btInserirFunc).setOnClickListener {
            val intent = Intent(this.activity, InserirFuncionario::class.java)
            startActivity(intent)
        }

        view.findViewById<ConstraintLayout>(R.id.btPlanos).setOnClickListener {
            val intent = Intent(this.activity, Planos::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            val avatar = view?.findViewById<ImageView>(R.id.imagemPerfil)

            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    if (photoUri != null) {

                        val file = File(photoUri!!.path!!)
                        uploadToCloudinary(file,
                            onSuccess = { url ->
                                Glide.with(this)
                                    .load(url)
                                    .circleCrop()
                                    .into(avatar!!)
                            },
                            onError = { e ->
                                Toast.makeText(requireContext(), "Erro no upload: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else if (data?.extras?.get("data") != null) {

                        val bitmap = data.extras?.get("data") as Bitmap
                        avatar?.setImageBitmap(bitmap)

                        val tempFile = File(requireContext().cacheDir, "temp.jpg")
                        FileOutputStream(tempFile).use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        }
                        uploadToCloudinary(tempFile,
                            onSuccess = { url ->
                                Glide.with(this)
                                    .load(url)
                                    .circleCrop()
                                    .into(avatar!!)
                            },
                            onError = { e ->
                                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
                                Log.d(ContentValues.TAG, "${e.message}")
                            }
                        )
                    } else {
                        Toast.makeText(requireContext(), "Erro: Nenhuma imagem recebida", Toast.LENGTH_SHORT).show()
                    }
                }


                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val file = getFileFromUri(selectedImageUri)
                        if (file != null) {
                            uploadToCloudinary(file,
                                onSuccess = { url ->
                                    val avatar = view?.findViewById<ImageView>(R.id.imagemPerfil)
                                    Glide.with(this)
                                        .load(url)
                                        .circleCrop()
                                        .into(avatar!!)

                                    Toast.makeText(requireContext(), "Imagem enviada com sucesso!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { e ->
                                    Toast.makeText(requireContext(), "Erro no upload: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(requireContext(), "Erro ao processar imagem", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

    private fun uploadToCloudinary(file: File, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        Thread {
            try {
                Log.d(ContentValues.TAG, "inserindo")
                destroyFromCloudinary(imagemAtual)
                val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                var url = result["secure_url"] as String
                imagePublicId = result["public_id"] as String
                requireActivity().runOnUiThread {
                    inserirImagemEmpresa(url)
                    onSuccess(url)
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    onError(e)
                }
            }
        }.start()
    }

    private fun getRealPathFromUri(uri: Uri): String {
        var filePath = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = cursor.getString(idx)
            cursor.close()
        }
        return filePath
    }


    private fun showImagePickerOptions() {
        val options = arrayOf("Tirar foto", "Escolher da galeria")

        AlertDialog.Builder(requireContext())
            .setTitle("Alterar foto de perfil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> requestGalleryPermission()
                }
            }
            .show()
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("selectedImage_", ".jpg", requireContext().cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs()
            }

            val photoFile = File.createTempFile(
                "profile_",
                ".jpg",
                storageDir
            )

            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                photoFile
            )

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun pegarImagemEmpresa(id: Long) {
        repositoryEmpresa.getEmpresaId(id).enqueue(object : retrofit2.Callback<com.example.projetosaveit.model.EmpresaDTO> {
            override fun onResponse(
                call: retrofit2.Call<com.example.projetosaveit.model.EmpresaDTO>,
                response: retrofit2.Response<com.example.projetosaveit.model.EmpresaDTO>
            ) {
                if (response.isSuccessful) {
                    imagemAtual = response.body()!!.enterpriseImage
                    val avatar = view?.findViewById<ImageView>(R.id.imagemPerfil)
                    if (!imagemAtual.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imagemAtual)
                            .circleCrop()
                            .into(avatar!!)
                    }
                } else {
                    Toast.makeText(context, "Erro ao carregar imagem!", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: retrofit2.Call<com.example.projetosaveit.model.EmpresaDTO>, t: Throwable) {
                Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun inserirImagemEmpresa(imagem : String) {
        val updates = mapOf("enterpriseImage" to imagem)
        repositoryEmpresa.patchEmpresa(idEmpresa, updates).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Imagem atualizada com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Erro ao atualizar imagem!", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Erro: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun destroyFromCloudinary(publicId: String) {
        Thread {
            try {
                val result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
                Log.d("Cloudinary", "Delete result: $result")

                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Resultado: ${result["result"]}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Não foi possível excluir a imagem: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }


}