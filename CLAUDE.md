# Velok — reglas del proyecto

App de gimnasio para el seguimiento de sesiones de entrenamiento. Proyecto escolar evaluado en:
vistas, capas (arquitectura), modelo de negocio, multiplataforma y funcionalidad.

## Flujo de trabajo
- Claude Code no ejecuta comandos de git que modifiquen el repositorio (add, commit, push, pull, checkout, merge, reset). Solo puede usar git status, git diff y git log para consultar. Al terminar una tarea, sugiere un mensaje de commit y el usuario hace el commit y el push manualmente.
- Los mensajes de commit sugeridos son breves, descriptivos y en español.
- El desarrollo se hace en ramas `feature/*`; `main` no recibe cambios directos.
- Antes de terminar, compilar con `./gradlew :androidApp:assembleDebug` y correr `./gradlew :shared:testAndroidHostTest`.
- No incluir claves de API ni datos sensibles. Si una API futura pide clave, va en `local.properties`
  (ignorado por git). La API gratuita de ExerciseDB V1 no pide clave.
- Finales de línea LF (ver `.gitattributes`); se trabaja desde Windows y Mac.

## Stack
- Kotlin Multiplatform (Android + iOS) con **Compose Multiplatform**; la UI vive en `shared/src/commonMain`.
- Navegación: Navigation Compose multiplataforma con rutas tipadas `@Serializable`.
- DI: **Koin** (`koin-compose-viewmodel`). Módulos en `di/Modulos.kt`; `initKoin()` se llama desde
  `VelokApplication` (Android) y `MainViewController` (iOS).
- Imágenes: **Coil 3** (`AsyncImage`), configurado en `App()` con `crearCargadorImagenes`
  (descarga con Ktor y caché en disco). Los modelos exponen `imagenUrl: String`: URIs de
  composeResources (`Res.getUri`) en los ejercicios de ejemplo y GIF remotos en los de ExerciseDB.
  **Los GIF se animan solo en Android** (coil-gif); en iOS se muestran como imagen fija porque
  coil-gif no se publica para iOS (limitación de Coil, ver `agregarDecodificadorGif`).
- Red: **Ktor client** con motor por plataforma (`motorHttp()`: OkHttp en Android, Darwin en iOS) y
  `kotlinx-serialization-json`.
- ViewModels de `lifecycle-viewmodel` multiplataforma con `StateFlow<XxxUiState>`.
- Fechas: `kotlinx-datetime` + `kotlin.time.Clock`.
- Versiones en `gradle/libs.versions.toml`; nunca escribir versiones directamente en los `build.gradle.kts`.
- Material3 está en `1.12.0-alpha03` porque no existe una versión estable compatible con Compose MP 1.12.

## Arquitectura (Clean Architecture + MVVM)
Paquete base `com.velok.app`, en `shared/src/commonMain/kotlin/com/velok/app/`:

```
domain/        Kotlin puro: sin Compose, sin recursos, sin textos visibles
  model/       Entidades (Plan, Ejercicio, SesionEntrenamiento, Metrica...) y sus reglas como
               funciones puras que devuelven copias (EdicionPlan.kt, SesionEntrenamiento.kt)
  calculo/     Estadísticas puras a partir del historial (progreso, horas por semana, músculos)
  validacion/  Reglas de formularios (ReglasCuenta, ReglasPerfil) que devuelven un ErrorCampo o null
  repository/  Interfaces (Actividad, Auth, Ejercicio, Plan, Sesion, Usuario)
  usecase/     Un caso de uso por clase con `operator fun invoke()` (`suspend` o que devuelve `Flow`)
data/
  local/       Base de datos Room (BaseDatosVelok): Entidades.kt, Relaciones.kt, Daos.kt,
               Conversiones.kt (entidad <-> dominio), SembradorDatos (siembra inicial) y
               seguridad/HashContrasenas (PBKDF2 con expect/actual)
  remote/      API de ExerciseDB: ClienteHttp (motor por plataforma), ClienteExerciseDb, DTOs,
               MapeoExerciseDb (DTO -> dominio) y SincronizadorCatalogo (descarga a Room)
  repository/  Local* (la app: Plan, Sesion, Ejercicio, Cuenta, sobre Room) y Mock* (en memoria,
               solo para pruebas y vistas previas). Actividad sigue en Mock hasta Health
               Connect/HealthKit. LocalCuentaRepository implementa AuthRepository y
               UsuarioRepository a la vez. DatosMock.kt tiene los datos de ejemplo de ambos.
               Cuenta de prueba: checo@velok.app / velok2026 (cuestionario ya completo).
di/            Módulos de Koin (dataModule, domainModule, presentationModule)
presentation/
  theme/       VelokTheme: colores, tipografía, formas
  navigation/  NavegacionRaiz (carga → cuenta → cuestionario → PantallaPrincipal) y, dentro de
               PantallaPrincipal, VelokNavHost con las pestañas (Rutas, DestinoPrincipal)
  components/  Componentes reutilizables con @Preview
  util/        Formato de números y mapeo dominio -> recursos (RecursosUi.kt)
  <pantalla>/  XxxScreen, XxxViewModel, XxxUiState
```

Reglas:
- Las pantallas **nunca** acceden a repositorios: Screen -> ViewModel -> UseCase -> Repository.
- Cada pantalla tiene dos composables: `XxxScreen` (obtiene el ViewModel con `koinViewModel()`) y
  `XxxContenido(estado, callbacks)` sin estado, que es el que se previsualiza.
- El dominio usa enums (`TipoMetrica`, `TipoCategoria`, `TipoEquipo`, `Musculo`...); su texto e ícono
  se resuelven en `presentation/util/RecursosUi.kt`.
- Las reglas de negocio van en el dominio y se prueban en `commonTest` (sin coroutines ni UI);
  los repositorios locales, en `androidHostTest` (ver "Persistencia local").
  Los ViewModels solo orquestan: llaman casos de uso o aplican funciones del dominio.
- Pantallas de detalle: rutas tipadas con id (`RutaDetallePlan(planId)`) y ViewModel creado con
  `koinViewModel(key = ...) { parametersOf(id) }`.
- Pantallas con muchos eventos los agrupan en una clase `AccionesXxx` en lugar de muchos parámetros.
- Formularios: el caso de uso valida con `validacion/` y devuelve `ResultadoFormulario` (Exito,
  Invalido con los errores por campo, o Rechazado por el repositorio). La pantalla traduce cada
  `ErrorCampo`/`ErrorCuenta` a texto con `presentation/util/MensajesError.kt`.
- Al iniciar sesión, `destinoPara(usuario)` decide si ir al cuestionario o a la app; la navegación
  borra la pila (`popUpTo(graph.id)`) para que "atrás" no regrese a las pantallas de cuenta.
- "Atrás" dentro de una pantalla (pasos del cuestionario): `NavigationBackHandler` de
  navigationevent-compose (no el `BackHandler` de compose-ui, que está obsoleto).
- Los casos de uso de cuenta se llaman `Ingresar...`/`Registrar...` para no confundirlos con
  `IniciarSesionUseCase`, que inicia una sesión de **entrenamiento**.
- Código específico de plataforma solo en `androidMain` / `iosMain` vía `expect/actual`.
  `Platform` es el ejemplo evaluado: se muestra en la pantalla de Perfil; no eliminarlo. Otros
  ejemplos: `moduloPlataforma()` (ruta de la base de datos), el hash de contraseñas, `motorHttp()`
  y `agregarDecodificadorGif()`.

## Persistencia local
- **Room KMP** (`room`, `ksp`, `sqlite-bundled` en `libs.versions.toml`) con `BundledSQLiteDriver`;
  la configuración común está en `crearBaseDatos()` y la ruta del archivo en `moduloPlataforma()`
  (`androidMain`: `context.getDatabasePath`; `iosMain`: carpeta Documents).
- Planes, historial, catálogo de ejercicios y cuentas se guardan en la base de datos y sobreviven
  al cerrar la app. Los repositorios exponen `Flow` (los DAO de Room emiten solos al cambiar).
- Las entidades de Room viven solo en `data/local`; el dominio nunca las ve (se convierten en
  `Conversiones.kt`). Los enums se guardan por nombre y las fechas como texto ISO o milisegundos.
- Al cambiar una tabla: subir `version` en `BaseDatosVelok`, escribir la migración y conservar el
  esquema exportado en `shared/schemas/`.
- Versión actual de la base de datos: **3**.
- La primera vez, `SembradorDatos` copia `DatosMock` (ejercicios, 4 planes, historial, cuenta demo).
  La base se considera nueva si no tiene ejercicios, así los planes borrados no reaparecen.
- **Contraseñas nunca en texto plano**: `HashContrasenas` guarda `pbkdf2-sha256$iteraciones$sal$hash`
  (120,000 iteraciones, sal aleatoria de 16 bytes) y compara en tiempo constante.
- La sesión iniciada se guarda en la tabla `usuario_actual`; "Cerrar sesión" (Perfil) la borra y
  vuelve a la selección de inicio limpiando la pila de navegación.
- Pruebas de los repositorios locales en `androidHostTest` con Robolectric y Room en memoria
  (`Pruebas.kt` arma los repositorios con un hash rápido, una fecha fija y un servidor sin conexión).
- Migraciones: v1 -> v2 automática (`@AutoMigration`); v2 -> v3 manual (`MIGRACION_2_3` en
  `Migraciones.kt`, registrada con `conMigraciones()`), escrita sobre `SQLiteConnection`. Se prueban
  en `MigracionTest` creando bases reales a partir de `shared/schemas/.../N.json`. Las pruebas usan
  `AndroidSQLiteDriver` (como la app usa un driver, Room migra por `migrate(SQLiteConnection)`).

## Unidades de peso (kg / lb)
- **Los pesos se guardan siempre en kg** (planes, sesiones, perfil). La unidad del usuario
  (`PerfilFisico.unidadPeso`) solo cambia cómo se muestran y capturan.
- Conversión con funciones puras del dominio (`pesoEnUnidad`, `pesoAKg`,
  `MetricaProgreso.enUnidad`), probadas en `UnidadesPesoTest`. Lo escrito se pasa a kg **sin
  redondear** (135 lb -> 61.2349... kg -> 135 lb); el redondeo (1 decimal) es solo al mostrar.
- En presentación: `CampoPeso(valorKg, unidad, onCambioKg)` convierte en ambos sentidos;
  `formatearPeso`, `textoMetrica` y `UnidadPeso.columna` en `presentation/util/Unidades.kt`.
  Los ViewModels siguen trabajando en kg y exponen `unidad` en su UiState
  (`ObtenerUnidadPesoUseCase`, un Flow: al cambiarla en Perfil se actualizan todas las pantallas).
- Nunca escribir "kg" fijo en una pantalla: usar `unidad.columna` / `unidad.sufijo`.

## Búsqueda del catálogo
- Se busca en SQLite (`EjercicioDao.buscar`), nunca filtrando en memoria los 1,500 ejercicios.
  El filtro es `FiltroCatalogo` (texto, equipo, categoría y músculos/categorías en español).
- Sin mayúsculas ni acentos: columna `nombreBusqueda` = `normalizarBusqueda(nombre)` (dominio),
  calculada en Kotlin al guardar; el texto escrito se normaliza igual y se escapa para `LIKE`
  (`patronLike`: `%` y `_` se buscan literal).
- Espera de 300 ms (`debounce`) y búsqueda por músculo/categoría en español en
  `presentation/util/BusquedaEjercicios.kt`, compartida por la pestaña Ejercicio y la hoja
  "Agregar ejercicio" del editor (`BuscadorEjercicios`).

## Textos legales
- `documento_terminos` y `documento_privacidad` en `strings.xml` describen lo que la app hace hoy.
  Los datos del equipo responsable (nombres, correo, domicilio, fecha, plazos, jurisdicción) los
  dio el equipo: nunca inventarlos ni cambiarlos sin su indicación. "> " marca la nota de proyecto
  escolar, que debe quedarse visible. Al cambiar lo que hace la app, actualizar estos textos y su
  fecha de última actualización.

## Capa remota (ExerciseDB)
- Todo el acceso a red vive en `data/remote/`; las pantallas, ViewModels y casos de uso nunca
  usan Ktor ni DTOs.
- **Primero local**: la app siempre lee de Room. `LocalEjercicioRepository` inicia en segundo plano
  (`SincronizadorCatalogo.iniciarUnaVez`, una vez por arranque) la descarga, que guarda cada
  página en Room; los `Flow` de Room actualizan las pantallas. Sin internet se usa lo guardado.
- API gratuita `https://oss.exercisedb.dev/api/v1/exercises?limit=25&after=<cursor>`, sin clave.
  Límites estrictos sin cifras publicadas: páginas de 25 con **1 s de pausa**, descarga completa
  solo si la última tiene más de **7 días**, y cualquier error (sin conexión, 5xx, **429**, JSON
  inválido) detiene la descarga sin reintentos; sigue desde el cursor guardado en el siguiente
  arranque (tabla `sincronizacion_catalogo`). Nunca descargar en bucle ni pre-descargar los GIF.
- Los errores de red nunca lanzan excepciones fuera de `data/remote` (`ResultadoPagina`,
  `ResultadoSincronizacion`). La UI solo muestra un aviso discreto (`EstadoCatalogo.SIN_CONEXION`)
  si no hay ningún ejercicio descargado.
- Mapeo en `MapeoExerciseDb`: nombre en inglés tal cual; categoría, equipo y músculos a nuestros
  enums (se ven en español). Valores desconocidos: categoría FUERZA, equipo OTRO, músculo de su
  parte del cuerpo. `TipoEquipo.OTRO` (accesorios) no tiene chip: solo aparece sin filtro.
- Los 21 ejercicios de ejemplo (`OrigenEjercicio.EJEMPLO`) se quedan en la tabla para los planes e
  historial; el catálogo los oculta cuando ya hay ejercicios de ExerciseDB. Guardar ejercicios
  descargados con `@Upsert`, nunca REPLACE (borraría filas que los planes referencian).
- El detalle de un ejercicio de ExerciseDB muestra "Datos e imágenes: ExerciseDB".
- Pruebas sin internet: JSON real copiado en `commonTest/.../JsonExerciseDb.kt` y `MockEngine`.

## Tema visual
- Acento verde lima `#D3FA6E` (`VelokTheme.colores.acento`). Para texto/íconos sobre fondo claro usar
  `acentoTexto` (variante más oscura, por legibilidad).
- App oscura: fondo `fondo` (#171819) y `fondoProfundo` (#000) en sesión, editor y detalle de
  ejercicio. Tarjetas claras (`superficie`) con esquinas redondeadas.
- Encabezado plano con el logo `logo_velok_encabezado.png` (copia sin cambios de
  `docs/diseno/Header.png`), campana con punto rojo y flecha de "atrás" en pantallas de detalle
  (iOS no tiene botón de atrás del sistema).
- Logos: `logo_velok_aguila.xml` (trazos de `docs/diseno/logo_velok.svg` rellenos, para tamaños
  grandes), `logo_velok_texto.png` (recorte exacto de Header.png) y `LogoHorizontal` (Header.png).
  En Android, el splash del sistema usa la misma águila (`androidApp/.../splash_aguila.xml`).
- Azul `informativo` solo en sesión y editor (nombres de ejercicio en tablas y duración); el resto
  de gráficas y selectores, incluido el perfil, usan el verde lima `acento`. Ámbar `calentamiento`
  para las series "W".
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
- Repositorios: interfaz `XxxRepository`, implementaciones `LocalXxxRepository`, `MockXxxRepository`,
  `ApiXxxRepository`, etc. Tablas de Room: `XxxEntidad`; consultas: `XxxDao`.
- Componentes reutilizables: `XxxCard`, `VelokXxx` para piezas de marca (`VelokHeader`, `VelokBottomBar`).
- Rutas `RutaXxx` (`data object` serializable).
- Recursos: `nav_*`, `metrica_*`, `valor_*`, `categoria_*`, `tab_*`, `equipo_*`, `musculo_*`,
  `grupo_*`, `rango_*`, `error_*`, `pregunta_*`, `meta_*` para strings; `ic_*` para íconos; `categoria_<tipo>.jpg` para las imágenes
  de categoría.
