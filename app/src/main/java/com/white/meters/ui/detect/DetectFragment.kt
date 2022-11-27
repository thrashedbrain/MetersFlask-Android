package com.white.meters.ui.detect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.white.meters.data.models.DetectArgs
import com.white.meters.databinding.FragmentDetectBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class DetectFragment : Fragment() {

    private lateinit var binding: FragmentDetectBinding
    private val model: DetectVM by viewModels()

    private var path: Uri? = null
    private var picturePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetectBinding.inflate(inflater, container, false)
        val meterType = (arguments?.getParcelable("type")) as DetectArgs?
        if (meterType != null) {
            model.init(meterType.meterType)
        } else {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkCamera()

        model.loading.observe(viewLifecycleOwner) {
            binding.pbDetect.visibility = if (it) View.VISIBLE else View.GONE
        }

        model.err.observe(viewLifecycleOwner) {
            if (it) {
                Snackbar.make(binding.root, "Err :(", Snackbar.LENGTH_LONG)
                    .setAction("Try Again?") {
                        checkCamera()
                    }.show()
            }
        }

        model.image.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.ivPreview.setImageBitmap(
                    model.grayscaleImage(
                        model.enhanceImage(it, 1.2f, 1.2f)
                    )
                )
                model.recognizeText(
                    model.grayscaleImage(
                        model.enhanceImage(it, 1.2f, 1.2f)
                    )
                )
            }
        }

        model.text.observe(viewLifecycleOwner) {
            binding.etDetect.setText(it)
        }

        binding.mbDetectDone.setOnClickListener {
            model.setResult(binding.etDetect.text.toString())
            findNavController().popBackStack()
        }
    }

    private val permissionCameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                initCamera()
            }
        }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val file = File(picturePath)
                if (file.exists()) {
                    val image = BitmapFactory.decodeFile(file.absolutePath)
                    val rotateImage = model.rotateBitmap(image, 90f)
                    binding.ivPreview.setImageBitmap(rotateImage)
                    model.sendImg(rotateImage ?: return@registerForActivityResult)
                }

            }
        }

    private fun initCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "$timeStamp.jpg"
            val storageDir: File = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            picturePath = storageDir.absolutePath + "/" + imageFileName
            val file = File(picturePath)
            path = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                file
            )
            it.putExtra(MediaStore.EXTRA_OUTPUT, path)
            cameraResult.launch(it)
        }
    }

    private fun checkPermission(): Boolean {
        return when (ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION_CAMERA
        )) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }

    private fun checkCamera() {
        if (checkPermission()) {
            initCamera()
        } else {
            val intent =
                Intent(ActivityResultContracts.RequestMultiplePermissions.ACTION_REQUEST_PERMISSIONS)
                    .putExtra(
                        ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS,
                        arrayOf(REQUIRED_PERMISSION_CAMERA)
                    )
            permissionCameraResult.launch(intent)

        }
    }

    companion object {
        private const val REQUIRED_PERMISSION_CAMERA = Manifest.permission.CAMERA
    }
}