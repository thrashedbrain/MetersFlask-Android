package com.white.meters.ui.detect

import android.graphics.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.white.meters.data.base.DetectRepository
import com.white.meters.data.base.MeterRepository
import com.white.meters.data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetectVM @Inject constructor(
    private val detectRepository: DetectRepository,
    private val meterRepository: MeterRepository
) : ViewModel() {

    private val _image = MutableLiveData<Bitmap?>()
    val image: LiveData<Bitmap?> = _image

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _err = MutableLiveData<Boolean>()
    val err: LiveData<Boolean> = _err

    private var meterType = MeterType.COLD_BATH

    fun init(meterType: MeterType) {
        this.meterType = meterType
    }

    fun sendImg(image: Bitmap) = viewModelScope.launch {
        _loading.value = true
        val resultImage = detectRepository.processImage(image)
        if (resultImage == null) {
            _err.value = true
        } else {
            _image.value = resultImage
        }
        _loading.value = false
    }

    fun recognizeText(bitmap: Bitmap) = viewModelScope.launch {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener {
                _text.value = processText(it.text)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun processText(text: String): String {
        val result = text
            .replace(" ", "")
            .replace(".", "")
            .replace("D", "0")
            .replace(",", "")
            .replace(":", "")
            .replace("a", "")
            .replace("J", "")
            .replace("j", "")
            .replace("O", "0")
            .replace("-", "")
            .replace("l", "")
            .replace("L", "")
            .replace("%", "")
            .replace("/", "")
            .replace("T", "")
            .replace(";", "")
            .replace("x", "")
            .replace("X", "")
            .replace("s", "")
            .replace("b", "")
            .replace("B", "")
            .replace("i", "")
            .replace("I", "")
            .replace("\n", "")

        val stringBuilder = StringBuilder(result)
        if (stringBuilder.length < 7) {
            _err.value = true
        } else {
            stringBuilder.insert(5, ",")
        }

        return stringBuilder.toString()
    }

    fun setResult(result: String) = viewModelScope.launch {
        when (meterType) {
            MeterType.COLD_BATH -> meterRepository.setColdBath(ColdBathMeterData(result))
            MeterType.HOT_BATH -> meterRepository.setHotBath(HotBathMeterData(result))
            MeterType.COLD_KITCHEN -> meterRepository.setColdKitchen(ColdKitchenMeterData(result))
            MeterType.HOT_KITCHEN -> meterRepository.setHotKitchen(HotKitchenMeterData(result))
        }
    }

    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap? {
        val matrix = Matrix()
        matrix.preRotate(degrees)
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }

    fun grayscaleImage(bitmap: Bitmap): Bitmap {
        val bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val bmpGrayScale = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmpGrayScale)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixColorFilter
        canvas.drawBitmap(bmp, 0f, 0f, paint)
        return bmpGrayScale
    }

    fun enhanceImage(bitmap: Bitmap, contrast: Float, brightness: Float): Bitmap {
        val colorMatrix = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        val enhancedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(enhancedBitmap)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return enhancedBitmap
    }
}