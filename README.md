# Velok

App de gimnasio para el seguimiento de sesiones de entrenamiento, hecha con Kotlin Multiplatform
para Android e iOS con una sola base de UI compartida.

Este primer avance incluye:
- **Inicio (Dashboard):** actividad diaria con anillo de progreso, métricas (calorías, pasos,
  distancia y ritmo cardiaco) con mini gráficas, y carrusel de planes.
- **Ejercicio:** pestañas Ejercicio/Entrenamiento y cuadrícula escalonada de categorías.
- Barra de navegación inferior con Calendario, Actividad y Perfil como pantallas provisionales.

Los datos son simulados (`MockActividadRepository`, `MockEjercicioRepository`). Más adelante se
conectarán Health Connect / HealthKit y la API de ExerciseDB sin cambiar la UI.

El diseño de referencia está en [`docs/diseno/`](docs/diseno).

 **Documento de avance:** [`docs/AVANCE-01.md`](docs/AVANCE-01.md) explica todo lo implementado,
la arquitectura, cómo agregar pantallas, el flujo de trabajo con Git y los pendientes.

## Stack

| Área | Tecnología |
|---|---|
| Lenguaje y plataformas | Kotlin Multiplatform (Android, iOS) |
| UI | Compose Multiplatform + Material 3 |
| Arquitectura | Clean Architecture (domain / data / presentation) + MVVM |
| Estado | ViewModel multiplataforma + `StateFlow` |
| Navegación | Navigation Compose (rutas tipadas) |
| Inyección de dependencias | Koin |
| Imágenes | Coil 3 |
| Fechas | kotlinx-datetime |

Las versiones exactas están en [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Estructura

```
androidApp/   Punto de entrada Android (MainActivity, VelokApplication)
iosApp/       Punto de entrada iOS (Xcode / SwiftUI)
shared/
  src/commonMain/kotlin/com/velok/app/
    domain/        Modelos, interfaces de repositorio y casos de uso
    data/          Implementaciones de repositorio (mock)
    di/            Módulos de Koin
    presentation/  Tema, navegación, componentes y pantallas
  src/commonMain/composeResources/  Textos, íconos e imágenes
  src/androidMain, src/iosMain      Código específico de plataforma (expect/actual)
```

## Requisitos

Requisitos: Android Studio (con el plugin de Kotlin Multiplatform) y JDK 21 (Gradle lo descarga automáticamente si no está instalado). Para iOS
se necesita una Mac con Xcode.
