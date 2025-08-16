package com.example.projetosaveit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.File
import java.io.IOException
import java.io.ObjectInput

/**
 * A simple [Fragment] subclass.
 * Use the [Perfil.newInstance] factory method to
 * create an instance of this fragment.
 */
class Perfil : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val CAMERA_REQUEST_CODE = 100
    private var photoUri: Uri? = null

    val config = hashMapOf(
        "cloud_name" to "mediaflows_056bef3f-c7ee-465c-969d-f27f6d076ac7",
        "api_key" to "515376282616285",
        "api_secret" to "T2BSV6xtsGY9Bk3Cxm4dfhKC7H4"
    )
    val cloudinary = Cloudinary(config)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val avatar = view.findViewById<ImageView>(R.id.imagemPerfil)
        avatar.setOnClickListener {
            showImagePickerOptions()
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
                    val file = File(photoUri?.path!!) // Arquivo local

                    // Mostra a imagem na tela imediatamente
                    Glide.with(this)
                        .load(file)
                        .circleCrop()
                        .into(avatar!!)

                    // Em paralelo, faz o upload para o Cloudinary
                    uploadToCloudinary(file,
                        onSuccess = { url ->
                            // A URL está pronta, mas a imagem já foi exibida
                            // Você pode salvar a URL em uma variável ou banco de dados
                        },
                        onError = { e ->
                            Toast.makeText(requireContext(), "Erro no upload: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    val file = File(getRealPathFromUri(selectedImageUri!!))
                    uploadToCloudinary(file,
                        onSuccess = { url ->
                            avatar?.setImageURI(selectedImageUri)
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

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

}