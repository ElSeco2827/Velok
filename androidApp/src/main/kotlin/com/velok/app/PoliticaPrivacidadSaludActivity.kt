package com.velok.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.velok.app.presentation.legal.DocumentoLegal
import com.velok.app.presentation.legal.DocumentoLegalScreen
import com.velok.app.presentation.theme.VelokTheme

/**
 * Aviso de privacidad que Health Connect abre cuando el usuario toca "política de privacidad" en
 * el diálogo de permisos. Google lo exige (ver AndroidManifest.xml):
 * - Android 13 y menor: acción androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE.
 * - Android 14+: activity-alias con VIEW_PERMISSION_USAGE y la categoría HEALTH_PERMISSIONS.
 * Muestra el mismo aviso de privacidad que la app (DocumentoLegalScreen).
 */
class PoliticaPrivacidadSaludActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            VelokTheme {
                DocumentoLegalScreen(DocumentoLegal.PRIVACIDAD, onContinuar = ::finish)
            }
        }
    }
}
