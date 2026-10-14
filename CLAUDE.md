# Velok — reglas del proyecto

App de gimnasio para el seguimiento de sesiones de entrenamiento. Proyecto escolar evaluado en:
vistas, capas (arquitectura), modelo de negocio, multiplataforma y funcionalidad.

## Flujo de trabajo
- Claude Code no ejecuta comandos de git que modifiquen el repositorio (add, commit, push, pull, checkout, merge, reset). Solo puede usar git status, git diff y git log para consultar. Al terminar una tarea, sugiere un mensaje de commit y el usuario hace el commit y el push manualmente.
- Los mensajes de commit sugeridos son breves, descriptivos y en español.
- El desarrollo se hace en ramas `feature/*`; `main` no recibe cambios directos.
- Antes de terminar, compilar con `./gradlew :androidApp:assembleDebug` y correr `./gradlew :shared:testAndroidHostTest`.
- No incluir claves de API ni datos sensibles. Las claves futuras (ExerciseDB) van en `local.properties` (ignorado por git).
- Finales de línea LF (ver `.gitattributes`); se trabaja desde Windows y Mac.

## Stack
- Kotlin Multiplatform (Android + iOS) con **Compose Multiplatform**; la UI vive en `shared/src/commonMain`.
- Navegación: Navigation Compose multiplataforma con rutas tipadas `@Serializable`.
- DI: **Koin** (`koin-compose-viewmodel`). Módulos en `di/Modulos.kt`; `initKoin()` se llama desde
  `VelokApplication` (Android) y `MainViewController` (iOS).
- Imágenes: **Coil 3** (`AsyncImage`). Los modelos exponen `imagenUrl: String`; hoy son URIs de
  composeResources (`Res.getUri`), mañana URLs remotas, sin tocar la UI.
- ViewModels de `lifecycle-viewmodel` multiplataforma con `StateFlow<XxxUiState>`.
- Fechas: `kotlinx-datetime` + `kotlin.time.Clock`.
- Versiones en `gradle/libs.versions.toml`; nunca escribir versiones directamente en los `build.gradle.kts`.
- Material3 está en `1.12.0-alpha03` porque no existe una versión estable compatible con Compose MP 1.12.

## Arquitectura (Clean Architecture + MVVM)
Paquete base `com.velok.app`, en `shared/src/commonMain/kotlin/com/velok/app/`:

```
domain/        Kotlin puro: sin Compose, sin recursos, sin textos visibles
  model/       ActividadDiaria, Metrica (+TipoMetrica), Plan, CategoriaEjercicio (+TipoCategoria)
  repository/  Interfaces (ActividadRepository, EjercicioRepository)
  usecase/     Un caso de uso por clase, con `suspend operator fun invoke()`
data/
  repository/  Implementaciones (hoy Mock*; luego Health Connect/HealthKit y ExerciseDB)
di/            Módulos de Koin (dataModule, domainModule, presentationModule)
presentation/
  theme/       VelokTheme: colores, tipografía, formas
  navigation/  Rutas, DestinoPrincipal, VelokNavHost
  components/  Componentes reutilizables con @Preview
  util/        Formato de números y mapeo dominio -> recursos (RecursosUi.kt)
  <pantalla>/  XxxScreen, XxxViewModel, XxxUiState
```

Reglas:
- Las pantallas **nunca** acceden a repositorios: Screen -> ViewModel -> UseCase -> Repository.
- Cada pantalla tiene dos composables: `XxxScreen` (obtiene el ViewModel con `koinViewModel()`) y
  `XxxContenido(estado, callbacks)` sin estado, que es el que se previsualiza.
- El dominio usa enums (`TipoMetrica`, `TipoCategoria`); su texto e ícono se resuelven en
  `presentation/util/RecursosUi.kt`.
- Código específico de plataforma solo en `androidMain` / `iosMain` vía `expect/actual`.
  `Platform` es el ejemplo evaluado: se muestra en la pantalla de Perfil; no eliminarlo.

## Tema visual
- Acento verde lima `#D3FA6E` (`VelokTheme.colores.acento`). Para texto/íconos sobre fondo claro usar
  `acentoTexto` (variante más oscura, por legibilidad).
- Encabezado negro con esquinas inferiores redondeadas, logo de Velok en lima (`logo_velok.xml`,
  convertido de `docs/diseno/logo_velok.svg`) que se corta en el borde inferior, y campana con punto rojo.
- Fondo crema en Inicio y placeholders; blanco en Ejercicio. Tarjetas redondeadas con borde sutil.
- **Nunca** colores, tamaños de fuente ni formas fijos en pantallas/componentes: usar
  `VelokTheme.colores`, `VelokTheme.tipografia`, `VelokTheme.formas` y `VelokTheme.formasVelok`.
  La paleta base (`internal`) solo se usa dentro de `presentation/theme`.
- Los recursos gráficos del diseño (logos, ilustraciones) se toman de docs/diseno; nunca se redibujan a mano.
  Los SVG se convierten a vector drawable XML copiando los trazos sin modificarlos.
- Íconos: vectores XML propios (trazo, 24x24) en `composeResources/drawable`, prefijo `ic_`.
  No usar `material-icons-extended`.

## Idioma y textos
- Toda la UI en **español**. Todos los textos visibles en `composeResources/values/strings.xml`
  (y `string-array` para listas como los meses); nunca literales en el código.
- Identificadores, comentarios y commits en español. Evitar `ñ` y acentos en identificadores
  (`PestanaEjercicio`, no `PestañaEjercicio`).

## Convenciones de nombres
- Pantallas `XxxScreen`, contenido sin estado `XxxContenido`, ViewModels `XxxViewModel`, estado `XxxUiState`.
- Casos de uso `VerboSustantivoUseCase` (`ObtenerPlanesUseCase`).
- Repositorios: interfaz `XxxRepository`, implementaciones `MockXxxRepository`, `ApiXxxRepository`, etc.
- Componentes reutilizables: `XxxCard`, `VelokXxx` para piezas de marca (`VelokHeader`, `VelokBottomBar`).
- Rutas `RutaXxx` (`data object` serializable).
- Recursos: `nav_*`, `metrica_*`, `valor_*`, `categoria_*`, `tab_*` para strings; `ic_*` para íconos;
  `categoria_<tipo>.jpg` para las imágenes de categoría.
