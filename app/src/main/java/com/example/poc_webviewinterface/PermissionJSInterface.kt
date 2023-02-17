package com.example.poc_webviewinterface

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64.encodeToString
import android.provider.MediaStore
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class PermissionJSInterface(private val context: Context, private val webView: WebView) {

    var permissionGranted: Boolean = false
    var capturedUri: Uri? = null


    private var requestPermission =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                grant ->
            if (grant) {
                permissionGranted = true
                openCamera(context)
            } else {
                permissionGranted = false
//                webView.evaluateJavascript(
//                    "javascript: updateFromNative(\"$grant\")"
//                ) { "false" }
            }
        }

    private var openCamera = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.TakePicture()) {
        val imageUri: Uri? = capturedUri
        if(imageUri != null){
            val bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri)
           val imageBase64 = encodeImage(bitmap)
            webView.evaluateJavascript(
                "javascript: ImageFromNative(\"$imageBase64\")"
            ) { "true" }
        }
    }

    private fun encodeImage(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return encodeToString(b, android.util.Base64.DEFAULT)
    }

    private fun openCamera(context: AppCompatActivity) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context.packageManager) != null) {
            capturedUri = getImageURI()
            openCamera.launch(capturedUri)
        }
    }

    private fun getImageURI(): Uri? {
        val directory = File(context.filesDir, "camera_images")
        if(!directory.exists()){
            directory.mkdirs()
        }
        val file = File(directory,"${Calendar.getInstance().timeInMillis}.png")
        return FileProvider.getUriForFile(context, context.packageName + ".provider", file);
    }

    @JavascriptInterface
    fun requestCameraPermission() {
//        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.CAMERA)
//        }
    }
}