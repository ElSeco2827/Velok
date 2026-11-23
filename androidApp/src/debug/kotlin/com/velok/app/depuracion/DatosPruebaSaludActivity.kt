package com.velok.app.depuracion

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.lifecycleScope
import com.velok.app.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.KClass

/**
 * SOLO DEBUG (androidApp/src/debug): inserta datos de prueba de hoy en Health Connect para probar
 * la tarjeta "Actividad diaria" en el emulador, donde no hay un reloj o un teléfono que cuente
 * pasos. No existe en la versión release.
 *
 * Abrir: adb shell am start -n com.velok.app/.depuracion.DatosPruebaSaludActivity
 */
class DatosPruebaSaludActivity : ComponentActivity() {

    private val tipos: List<KClass<out Record>> = listOf(
        StepsRecord::class, DistanceRecord::class, ActiveCaloriesBurnedRecord::class, HeartRateRecord::class,
    )
    private val permisosEscritura = tipos.map { HealthPermission.getWritePermission(it) }.toSet()

    private lateinit var estado: TextView
    private lateinit var pedirPermisos: ActivityResultLauncher<Set<String>>
    private var alObtenerPermisos: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pedirPermisos = registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { concedidos ->
            if (concedidos.containsAll(permisosEscritura)) alObtenerPermisos?.invoke() else mostrar(getString(R.string.depuracion_sin_permiso))
        }
        estado = TextView(this).apply { text = getString(R.string.depuracion_explicacion) }
        setContentView(
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(48, 160, 48, 48)
                addView(TextView(this@DatosPruebaSaludActivity).apply { text = getString(R.string.depuracion_titulo); textSize = 20f })
                addView(estado)
                addView(Button(this@DatosPruebaSaludActivity).apply {
                    text = getString(R.string.depuracion_insertar)
                    setOnClickListener { conPermisos { insertar() } }
                })
                addView(Button(this@DatosPruebaSaludActivity).apply {
                    text = getString(R.string.depuracion_borrar)
                    setOnClickListener { conPermisos { borrar() } }
                })
            },
        )
    }

    private fun conPermisos(accion: () -> Unit) {
        if (HealthConnectClient.getSdkStatus(this) != HealthConnectClient.SDK_AVAILABLE) {
            mostrar(getString(R.string.depuracion_sin_health_connect))
            return
        }
        lifecycleScope.launch {
            val concedidos = cliente().permissionController.getGrantedPermissions()
            if (concedidos.containsAll(permisosEscritura)) {
                accion()
            } else {
                alObtenerPermisos = accion
                pedirPermisos.launch(permisosEscritura)
            }
        }
    }

    private fun cliente() = HealthConnectClient.getOrCreate(this)

    /** Un registro por hora desde las 7:00 hasta la hora actual, con valores que varían. */
    private fun insertar() = ejecutar {
        val zona = ZoneId.systemDefault()
        val ahora = Instant.now()
        val inicioDia = LocalDate.now(zona).atStartOfDay(zona).toInstant()
        val horaActual = ahora.atZone(zona).hour
        val desde = if (horaActual > 7) 7 else 0
        val registros = mutableListOf<Record>()
        for (hora in desde..horaActual) {
            val inicio = inicioDia.plus(hora.toLong(), ChronoUnit.HOURS)
            val fin = minOf(inicio.plus(50, ChronoUnit.MINUTES), ahora)
            if (!fin.isAfter(inicio)) continue
            val offset = zona.rules.getOffset(inicio)
            val pasos = 300L + (hora * 137L) % 900
            registros += StepsRecord(inicio, offset, fin, offset, pasos, Metadata.manualEntry())
            registros += DistanceRecord(inicio, offset, fin, offset, Length.meters(pasos * 0.75), Metadata.manualEntry())
            registros += ActiveCaloriesBurnedRecord(inicio, offset, fin, offset, Energy.kilocalories(pasos * 0.04), Metadata.manualEntry())
            registros += HeartRateRecord(
                inicio, offset, fin, offset,
                listOf(
                    HeartRateRecord.Sample(inicio.plus(5, ChronoUnit.MINUTES).coerceAtMost(fin), 72L + hora % 5 * 4),
                    HeartRateRecord.Sample(fin, 80L + hora % 7 * 3),
                ),
                Metadata.manualEntry(),
            )
        }
        cliente().insertRecords(registros)
        mostrar(getString(R.string.depuracion_insertados, registros.size))
    }

    /** Borra los registros de hoy que insertó esta app (Health Connect no deja borrar los de otras). */
    private fun borrar() = ejecutar {
        val zona = ZoneId.systemDefault()
        val rango = TimeRangeFilter.between(LocalDate.now(zona).atStartOfDay(zona).toInstant(), Instant.now())
        tipos.forEach { cliente().deleteRecords(it, rango) }
        mostrar(getString(R.string.depuracion_borrados))
    }

    private fun ejecutar(bloque: suspend () -> Unit) {
        lifecycleScope.launch {
            try {
                bloque()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mostrar(getString(R.string.depuracion_error, e.message ?: e.javaClass.simpleName))
            }
        }
    }

    private fun mostrar(texto: String) {
        estado.text = texto
    }
}
