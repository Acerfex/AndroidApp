package com.frogobox.githubaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.HttpAuthHandler
import android.webkit.WebSettings
import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val myWebView = WebView(this)
        setContentView(myWebView)

        // RADIKALER FIX FÜR DIE OBERE LEISTE: Blendet Status- und Systemleisten vollständig aus
        supportActionBar?.hide()
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars()) 

        // Zugriff auf lokale Dateien und modernen Web-Speicher erlauben
        myWebView.settings.allowFileAccess = true
        myWebView.settings.allowContentAccess = true
        myWebView.settings.domStorageEnabled = true

        myWebView.settings.javaScriptEnabled = true
        // Erlaubt das Öffnen von Fenstern über JavaScript
        myWebView.settings.javaScriptCanOpenWindowsAutomatically = true 

        myWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                // FIX FÜR OFFLINE-MODUS: Stoppt das Festfrieren der WebView bei Verbindungsabbruch
                view?.stopLoading()
                // Erzwingt das sofortige, saubere Laden der lokalen HTML-Datei
                view?.loadUrl("file:///android_asset/index.html")
            }

            // DIESER BLOCK FÄNGT DAS HTACCESS-POPUP AB:
            override fun onReceivedHttpAuthRequest(
                view: WebView?,
                handler: HttpAuthHandler?,
                host: String?,
                realm: String?
            ) {
                // Erstellt ein Android-Eingabefenster für den Nutzer
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Anmeldung erforderlich")
                
                val layout = LinearLayout(this@MainActivity)
                layout.orientation = LinearLayout.VERTICAL
                
                val usernameInput = EditText(this@MainActivity)
                usernameInput.hint = "Benutzername"
                layout.addView(usernameInput)
                
                val passwordInput = EditText(this@MainActivity)
                passwordInput.hint = "Passwort"
                // Versteckt das Passwort bei der Eingabe
                passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                layout.addView(passwordInput)
                
                builder.setView(layout)
                
                builder.setPositiveButton("Anmelden") { _, _ ->
                    val user = usernameInput.text.toString()
                    val pass = passwordInput.text.toString()
                    // Sendet die Daten an den Server
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

        // Ihre Webseite
        myWebView.loadUrl("https://acerfex.lima-city.ch")
    }
}
