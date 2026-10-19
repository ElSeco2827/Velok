package com.velok.app.domain

import com.velok.app.domain.model.Musculo
import com.velok.app.domain.model.Serie
import com.velok.app.domain.model.TipoCategoria
import com.velok.app.domain.model.TipoSerie
import com.velok.app.domain.model.actualizarSerie
import com.velok.app.domain.model.agregarEjercicio
import com.velok.app.domain.model.agregarSerie
import com.velok.app.domain.model.alternarTipoSerie
import com.velok.app.domain.model.cambiarDescanso
import com.velok.app.domain.model.conCategoriaInferida
import com.velok.app.domain.model.eliminarEjercicio
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EdicionPlanTest {

    @Test
    fun agregarEjercicio_creaLasSeriesSugeridasSinPeso() {
        val plan = planDePrueba().agregarEjercicio(ejercicioDePrueba(series = 3, repeticiones = 10))

        val series = plan.ejercicios.single().series
        assertEquals(listOf(1, 2, 3), series.map { it.id })
        assertEquals(listOf(10, 10, 10), series.map { it.repeticiones })
        assertEquals(listOf(null, null, null), series.map { it.pesoKg })
    }

    @Test
    fun agregarSerie_copiaLaUltimaYUsaUnIdNuevo() {
        val plan = planDePrueba(
            ejercicioPlanDePrueba(series = arrayOf(Serie(1, pesoKg = 10.0, repeticiones = 12), Serie(5, pesoKg = 20.0, repeticiones = 8))),
        ).agregarSerie(0)

        val nueva = plan.ejercicios[0].series.last()
        assertEquals(6, nueva.id)
        assertEquals(20.0, nueva.pesoKg)
        assertEquals(8, nueva.repeticiones)
        assertEquals(TipoSerie.NORMAL, nueva.tipo)
    }

    @Test
    fun actualizarSerie_soloCambiaLaSerieIndicada() {
        val plan = planDePrueba(ejercicioPlanDePrueba(series = arrayOf(Serie(1, repeticiones = 12), Serie(2, repeticiones = 12))))
            .actualizarSerie(0, idSerie = 2, pesoKg = 15.0, repeticiones = 10)

        assertEquals(Serie(1, repeticiones = 12), plan.ejercicios[0].series[0])
        assertEquals(Serie(2, pesoKg = 15.0, repeticiones = 10), plan.ejercicios[0].series[1])
    }

    @Test
    fun alternarTipoSerie_cambiaEntreNormalYCalentamiento() {
        val plan = planDePrueba(ejercicioPlanDePrueba(series = arrayOf(Serie(1))))

        val calentamiento = plan.alternarTipoSerie(0, 1)
        assertEquals(TipoSerie.CALENTAMIENTO, calentamiento.ejercicios[0].series[0].tipo)
        assertEquals(TipoSerie.NORMAL, calentamiento.alternarTipoSerie(0, 1).ejercicios[0].series[0].tipo)
    }

    @Test
    fun indiceInvalido_noCambiaElPlan() {
        val plan = planDePrueba(ejercicioPlanDePrueba(series = arrayOf(Serie(1))))
        assertSame(plan, plan.agregarSerie(5))
    }

    @Test
    fun eliminarEjercicio_quitaSoloEseEjercicio() {
        val plan = planDePrueba(
            ejercicioPlanDePrueba(ejercicioDePrueba("a")),
            ejercicioPlanDePrueba(ejercicioDePrueba("b")),
        ).eliminarEjercicio(0)

        assertEquals(listOf("b"), plan.ejercicios.map { it.ejercicio.id })
    }

    @Test
    fun cambiarDescanso_noAceptaNegativos() {
        val plan = planDePrueba(ejercicioPlanDePrueba()).cambiarDescanso(0, -30)
        assertEquals(0, plan.ejercicios[0].descansoSegundos)
    }

    @Test
    fun duracionEstimada_sumaTrabajoYDescansoPorSerie() {
        // 4 series × (45 s + 60 s) = 420 s = 7 min.
        val plan = planDePrueba(ejercicioPlanDePrueba(series = Array(4) { Serie(it + 1) }))
        assertEquals(7, plan.duracionEstimadaMinutos)
    }

    @Test
    fun categoriaInferida_esLaMasRepetidaYEnEmpateLaPrimera() {
        val pecho1 = ejercicioDePrueba("p1", TipoCategoria.PECHO, Musculo.PECHO)
        val pecho2 = ejercicioDePrueba("p2", TipoCategoria.PECHO, Musculo.PECHO)
        val pierna = ejercicioDePrueba("l1", TipoCategoria.PIERNA)

        val mayoria = planDePrueba(
            ejercicioPlanDePrueba(pierna),
            ejercicioPlanDePrueba(pecho1),
            ejercicioPlanDePrueba(pecho2),
        ).conCategoriaInferida()
        assertEquals(TipoCategoria.PECHO, mayoria.categoria)
        assertEquals("img-p1", mayoria.imagenUrl)

        val empate = planDePrueba(ejercicioPlanDePrueba(pierna), ejercicioPlanDePrueba(pecho1)).conCategoriaInferida()
        assertEquals(TipoCategoria.PIERNA, empate.categoria)
    }

    @Test
    fun categoriaInferida_sinEjerciciosNoCambia() {
        val plan = planDePrueba()
        assertSame(plan, plan.conCategoriaInferida())
    }
}
