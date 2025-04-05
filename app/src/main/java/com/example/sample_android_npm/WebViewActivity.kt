package com.example.sample_android_npm

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class WebViewActivity : ComponentActivity() {
    private val client = OkHttpClient()

    private var permissionRequest: PermissionRequest? = null
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private val privateApiKey = "<<---- PRIVATE API KEY ---->>"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        // Register permission launcher using the modern Activity Result API
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    println("Permiso concedido")
                    permissionRequest?.grant(permissionRequest?.resources)
                } else {
                    println("Permiso denegado")
                    Toast.makeText(
                        this,
                        "Se necesita permiso de cámara para continuar",
                        Toast.LENGTH_SHORT
                    ).show()
                    permissionRequest?.deny()
                }
                permissionRequest = null
            }

        val webView: WebView = findViewById(R.id.webview)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true
        webSettings.allowContentAccess = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                    //Solicitar el permiso de cámara
                    permissionRequest = request
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        //Ejecuta los endpoints en un hilo de fondo
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Crear verificación
                val identifier = createVerification()
                if (identifier != null) {
                    // Obtener la url usando el identificador de la verificación
                    val url = getVerificationUrl(identifier)

                    if (url !== null) {
                        withContext(Dispatchers.Main) {
                            //Cargar la URL en WebView
                            webView.loadUrl(url)
                        }
                    } else {
                        showError("No se pudo crear la URL de la verificación")
                    }
                } else {
                    showError("No se pudo crear la verificación")
                }
            } catch (e: Exception) {
                Log.e("Error", "Exception: ${e.message}")
                showError("Ocurrió un error inesperado")
            }
        }

    }

    private fun createVerification(): String? {
        val json = JSONObject().apply {
            put("id", "<<---- IDENTIFIER ---->>")
            put("options", JSONObject().apply {
                put("checks", JSONObject().apply {
                    put("selfie", false)
                    put("verifyIp", false)
                })
                put("redirect_url", "https://plataforma.sumamexico.com")
            })
        }

        val mediaType = "application/json".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request =
            Request.Builder().url("https://veridocid.azure-api.net/api/id/v3/createVerification")
                .addHeader("x-api-key", privateApiKey).addHeader("Content-type", "application/json")
                .post(body).build()

        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        }
    }

    private fun getVerificationUrl(identifier: String): String? {
        val json = JSONObject().apply {
            put("identifier", identifier)
        }

        val mediaType = "application/json".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request =
            Request.Builder().url("https://veridocid.azure-api.net/api/id/v3/urlSdk")
                .addHeader("x-api-key", privateApiKey).addHeader("Content-type", "application/json")
                .post(body).build()

        client.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                response.body?.string()
            } else {
                null
            }
        }
    }

    private suspend fun showError(message: String?) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@WebViewActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}