package com.frogobox.githubaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.HttpAuthHandler
import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.view.View
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Vollbildmodus aktivieren
        try {
            requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            supportActionBar?.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Nutzen der im Template eingebauten Layout-Datei
        setContentView(R.id.custom_id_if_needed_otherwise_layout_main) 
        
        // Wir erstellen die WebView dynamisch, um ID-Konflikte im Template zu umgehen
        val myWebView = WebView(this)
        setContentView(myWebView)

        myWebView.settings.allowFileAccess = true
        myWebView.settings.allowContentAccess = true
        myWebView.settings.domStorageEnabled = true
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.javaScriptCanOpenWindowsAutomatically = true 

        myWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                view?.stopLoading()
                view?.loadUrl("file:///android_asset/index.html")
            }

            override fun onReceivedHttpAuthRequest(
                view: WebView?,
                handler: HttpAuthHandler?,
                host: String?,
                realm: String?
            ) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Anmeldung erforderlich")
                
                val layout = LinearLayout(this@MainActivity)
                layout.orientation = LinearLayout.VERTICAL
                
                val usernameInput = EditText(this@MainActivity)
                usernameInput.hint = "Benutzername"
                layout.addView(usernameInput)
                
                val passwordInput = EditText(this@MainActivity)
                passwordInput.hint = "Passwort"
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                layout.addView(passwordInput)
                
                builder.setView(layout)
                
                builder.setPositiveButton("Anmelden") { _, _ ->
                    val user = usernameInput.text.toString()
                    val pass = passwordInput.text.toString()
                    handler?.proceed(user, pass)
                }
                
                builder.setNegativeButton("Abbrechen") { dialog, _ ->
                    dialog.cancel()
                    handler?.cancel()
                }
                
                builder.setCancelable(false)
                builder.show()
            }
        }

        myWebView.loadUrl("https://lima-city.ch")
    }
}
