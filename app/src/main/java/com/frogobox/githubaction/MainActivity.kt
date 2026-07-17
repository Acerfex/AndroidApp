package com.frogobox.githubaction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.HttpAuthHandler
import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val myWebView = WebView(this)
        setContentView(myWebView)

        // BEHEBT DEN WEISSEN BILDSCHIRM: Erlaubt den Zugriff auf lokale HTML-Dateien
        myWebView.settings.allowFileAccess = true
        myWebView.settings.allowContentAccess = true

        myWebView.settings.javaScriptEnabled = true
        // Erlaubt das Öffnen von Fenstern über JavaScript
        myWebView.settings.javaScriptCanOpenWindowsAutomatically = true 

        myWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                // Bei Fehlern (z.B. Offline) lokale Datei laden
                myWebView.loadUrl("file:///android_asset/index.html")
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
