package com.example.projetosaveit.ui.perfil

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.projetosaveit.R
import com.example.projetosaveit.ui.ConfiguracoesPerfil
import com.example.projetosaveit.ui.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import java.io.FileOutputStream

class Perfil : Fragment() {

    val objAutenticar: FirebaseAuth = FirebaseAuth.getInstance()

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val CAMERA_REQUEST_CODE = 100
    private var photoUri: Uri? = null
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permissão concedida. Agora pode abrir a câmera.
                openCamera()
            } else {
                // Permissão negada. Avise o usuário.
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val empresaLogada : FirebaseUser = objAutenticar.getCurrentUser()!!

        view.findViewById<TextView>(R.id.nomePerfil).text = empresaLogada.displayName

        val avatar = view.findViewById<ImageView>(R.id.imagemPerfil)
        avatar.setOnClickListener {
            showImagePickerOptions()
        }

        view.findViewById<FrameLayout>(R.id.botaoConfiguracoes).setOnClickListener {
            val intent = Intent(this.activity, ConfiguracoesPerfil::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Perfil.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): Perfil {
            val fragment = Perfil()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

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
                    val file = File(getRealPathFromUri(selectedImageUri!!))
                    uploadToCloudinary(file,
                        onSuccess = { url ->
                            Glide.with(this)
                                .load(url)
                                .circleCrop()
                                .into(avatar!!)

                            Toast.makeText(requireContext(), "Foi inserido com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { e ->
                            Toast.makeText(requireContext(), "Erro no upload: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    private fun uploadToCloudinary(file: File, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        Thread {
            try {
                Log.d(ContentValues.TAG, "inserindo")
                val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                var url = result["secure_url"] as String
                requireActivity().runOnUiThread {
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

}