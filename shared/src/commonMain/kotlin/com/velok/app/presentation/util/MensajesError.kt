package com.velok.app.presentation.util

import androidx.compose.runtime.Composable
import com.velok.app.domain.model.ErrorCuenta
import com.velok.app.domain.model.MetaEntrenamiento
import com.velok.app.domain.model.UnidadPeso
import com.velok.app.domain.model.kgALibras
import com.velok.app.domain.validacion.ErrorCampo
import com.velok.app.domain.validacion.ReglasCuenta
import com.velok.app.domain.validacion.ReglasPerfil
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import velok.shared.generated.resources.Res
import velok.shared.generated.resources.error_contrasena_corta
import velok.shared.generated.resources.error_contrasenas_distintas
import velok.shared.generated.resources.error_correo_invalido
import velok.shared.generated.resources.error_correo_no_registrado
import velok.shared.generated.resources.error_correo_registrado
import velok.shared.generated.resources.error_credenciales
import velok.shared.generated.resources.error_estatura
import velok.shared.generated.resources.error_fecha_futura
import velok.shared.generated.resources.error_menor_edad
import velok.shared.generated.resources.error_nombre_corto
import velok.shared.generated.resources.error_nombre_largo
import velok.shared.generated.resources.error_peso
import velok.shared.generated.resources.error_vacio
import velok.shared.generated.resources.meta_ganar_musculo
import velok.shared.generated.resources.meta_mantener
import velok.shared.generated.resources.meta_perder_grasa
import velok.shared.generated.resources.sufijo_kg
import velok.shared.generated.resources.sufijo_lb
import velok.shared.generated.resources.unidad_kg
import velok.shared.generated.resources.unidad_lb

// Traduce los errores del dominio (enums, sin textos) a mensajes en español.

/** Mensaje del error. [unidadPeso] solo importa para el error de peso (el rango se muestra en esa unidad). */
@Composable
fun ErrorCampo.mensaje(unidadPeso: UnidadPeso = UnidadPeso.KG): String = when (this) {
    ErrorCampo.VACIO -> stringResource(Res.string.error_vacio)
    ErrorCampo.CORREO_INVALIDO -> stringResource(Res.string.error_correo_invalido)
    ErrorCampo.CONTRASENA_CORTA -> stringResource(Res.string.error_contrasena_corta, ReglasCuenta.CONTRASENA_MINIMA)
    ErrorCampo.CONTRASENAS_DISTINTAS -> stringResource(Res.string.error_contrasenas_distintas)
    ErrorCampo.NOMBRE_CORTO -> stringResource(Res.string.error_nombre_corto, ReglasCuenta.NOMBRE_MINIMO)
    ErrorCampo.NOMBRE_LARGO -> stringResource(Res.string.error_nombre_largo, ReglasCuenta.NOMBRE_MAXIMO)
    ErrorCampo.FECHA_FUTURA -> stringResource(Res.string.error_fecha_futura)
    ErrorCampo.MENOR_DE_EDAD -> stringResource(Res.string.error_menor_edad, ReglasPerfil.EDAD_MINIMA)
    ErrorCampo.ESTATURA_FUERA_DE_RANGO -> stringResource(
        Res.string.error_estatura, ReglasPerfil.ESTATURA_CM.first, ReglasPerfil.ESTATURA_CM.last,
    )
    ErrorCampo.PESO_FUERA_DE_RANGO -> {
        val rango = ReglasPerfil.PESO_KG
        val (minimo, maximo) = when (unidadPeso) {
            UnidadPeso.KG -> rango.start to rango.endInclusive
            UnidadPeso.LB -> kgALibras(rango.start) to kgALibras(rango.endInclusive)
        }
        stringResource(Res.string.error_peso, formatearNumero(minimo), formatearNumero(maximo), stringResource(unidadPeso.sufijo))
    }
}

@Composable
fun ErrorCuenta.mensaje(): String = when (this) {
    ErrorCuenta.CREDENCIALES_INCORRECTAS -> stringResource(Res.string.error_credenciales)
    ErrorCuenta.CORREO_YA_REGISTRADO -> stringResource(Res.string.error_correo_registrado)
    ErrorCuenta.CORREO_NO_REGISTRADO -> stringResource(Res.string.error_correo_no_registrado)
}

/** Texto del botón de la unidad ("Kg", "lb"). */
val UnidadPeso.nombre: StringResource
    get() = when (this) {
        UnidadPeso.KG -> Res.string.unidad_kg
        UnidadPeso.LB -> Res.string.unidad_lb
    }

/** Sufijo junto a un valor ("100 kg"). */
val UnidadPeso.sufijo: StringResource
    get() = when (this) {
        UnidadPeso.KG -> Res.string.sufijo_kg
        UnidadPeso.LB -> Res.string.sufijo_lb
    }

val MetaEntrenamiento.nombre: StringResource
    get() = when (this) {
        MetaEntrenamiento.PERDER_GRASA -> Res.string.meta_perder_grasa
        MetaEntrenamiento.GANAR_MUSCULO -> Res.string.meta_ganar_musculo
        MetaEntrenamiento.MANTENER -> Res.string.meta_mantener
    }
