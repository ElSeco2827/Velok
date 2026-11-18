package com.velok.app.data.remote

/**
 * Respuestas de ExerciseDB para las pruebas (sin internet). Los ejercicios están copiados de
 * respuestas reales de `GET https://oss.exercisedb.dev/api/v1/exercises` (noviembre de 2026); solo
 * se recortaron las instrucciones a un paso. El catálogo real tiene 1,500 ejercicios en páginas de
 * 25; aquí se reparte en 3 páginas de 2 con el mismo formato de `meta` (el cursor siguiente es el
 * id del último ejercicio de la página, como en la API).
 */
object JsonExerciseDb {

    const val PERRO_BOCA_ARRIBA = """{"exerciseId":"01qpYSe","name":"upward facing dog","gifUrl":"https://static.exercisedb.dev/media/01qpYSe.gif","bodyParts":["back"],"equipments":["bodyweight"],"targetMuscles":["erector spinae"],"secondaryMuscles":["deltoids","pectorals"],"instructions":["Step:1 Position yourself face down on the floor with your legs extended behind you."]}"""

    const val ELEVACION_RODILLAS = """{"exerciseId":"03lzqwk","name":"assisted hanging knee raise","gifUrl":"https://static.exercisedb.dev/media/03lzqwk.gif","bodyParts":["waist"],"equipments":["assisted"],"targetMuscles":["abdominals"],"secondaryMuscles":["hip flexors"],"instructions":["Step:1 Suspend yourself from a pull-up bar with your arms straight and your palms turned away from you."]}"""

    /** Trae dos equipos: se usa el primero (bodyweight -> TAPETE). */
    const val CURL_INVERSO = """{"exerciseId":"0rHfvy9","name":"inverse leg curl (on pull-up cable machine)","gifUrl":"https://static.exercisedb.dev/media/0rHfvy9.gif","bodyParts":["upper legs"],"equipments":["bodyweight","cable"],"targetMuscles":["hamstrings"],"secondaryMuscles":["glutes","calves"],"instructions":["Step:1 Set the cable machine so that the ankle straps are at the lowest setting."]}"""

    const val PRESS_BRADFORD = """{"exerciseId":"0dCyly0","name":"barbell seated bradford rocky press","gifUrl":"https://static.exercisedb.dev/media/0dCyly0.gif","bodyParts":["shoulders"],"equipments":["barbell"],"targetMuscles":["deltoids"],"secondaryMuscles":["triceps","upper back"],"instructions":["Step:1 Take a seat on a bench with your back straight and feet planted."]}"""

    const val CURL_INVERTIDO = """{"exerciseId":"0IgNjSM","name":"dumbbell standing reverse curl","gifUrl":"https://static.exercisedb.dev/media/0IgNjSM.gif","bodyParts":["upper arms"],"equipments":["dumbbell"],"targetMuscles":["biceps"],"secondaryMuscles":["forearms"],"instructions":["Step:1 Stand tall with your feet about shoulder width and hold a dumbbell in each hand, palms facing your body."]}"""

    /** Músculo principal sin equivalente ("cardiovascular system"): se toma el de su parte del cuerpo. */
    const val BURPEE = """{"exerciseId":"0JtKWum","name":"dumbbell burpee","gifUrl":"https://static.exercisedb.dev/media/0JtKWum.gif","bodyParts":["cardio"],"equipments":["dumbbell"],"targetMuscles":["cardiovascular system"],"secondaryMuscles":["quadriceps","hamstrings","calves","deltoids","triceps","core"],"instructions":["Step:1 Begin in a standing position with your feet about shoulder width and a dumbbell in each hand."]}"""

    /**
     * NO es real: valores inventados que la API no tiene, para probar que un valor desconocido no
     * impide guardar el ejercicio. También trae un campo extra que no existe en el DTO.
     */
    const val VALORES_DESCONOCIDOS = """{"exerciseId":"prueba1","name":"mystery move","gifUrl":"https://static.exercisedb.dev/media/prueba1.gif","bodyParts":["tail"],"equipments":["jetpack"],"targetMuscles":["wings"],"secondaryMuscles":["hands","feet","glutes"],"instructions":[],"campoNuevo":42}"""

    fun pagina(ejercicios: List<String>, siguiente: String?, anterior: Boolean = false): String {
        val cursor = if (siguiente != null) "\"$siguiente\"" else "null"
        return """{"success":true,"meta":{"total":6,"hasNextPage":${siguiente != null},"hasPreviousPage":$anterior,"nextCursor":$cursor},"data":[${ejercicios.joinToString(",")}]}"""
    }

    val PAGINA_1 = pagina(listOf(PERRO_BOCA_ARRIBA, ELEVACION_RODILLAS), siguiente = "03lzqwk")
    val PAGINA_2 = pagina(listOf(CURL_INVERSO, PRESS_BRADFORD), siguiente = "0dCyly0", anterior = true)
    val PAGINA_3 = pagina(listOf(CURL_INVERTIDO, BURPEE), siguiente = null, anterior = true)

    /** Página según el parámetro `after` de la petición (null = primera). */
    fun paginaPara(after: String?): String = when (after) {
        null -> PAGINA_1
        "03lzqwk" -> PAGINA_2
        "0dCyly0" -> PAGINA_3
        else -> error("Cursor inesperado: $after")
    }

    const val NO_ES_JSON = "<!DOCTYPE html><html><head><title>AscendAPI</title></head></html>"
    const val SIN_EXITO = """{"success":false,"error":"Something went wrong"}"""
}
