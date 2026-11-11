# Prompts

Estos prompts se pueden utilizar uno por uno siguiendo este orden para replicar los resultados. Parte de la fase 1 en una rama llamada `mejora-ia`. Consigue los archivos de referencia y recursos de esta rama y sigue [la guía de reproducción](docs/reproduccion/GUIA.md). Ejecuta un bloque a la vez y compila antes de continuar.

## 01 · Pruebas de catálogo

Compórtate como un experto en Android con Kotlin, MVVM y corrutinas. Ayúdame a reproducir el avance 01 de Pokédex Planta. Parte de la fase 1. Lee docs/reproduccion/GUIA.md y usa los tres archivos de docs/reproduccion/cambios/01 en sus mismas rutas. Agrega kotlinx-coroutines-test 1.9.0 al catálogo y a testImplementation. En ListaViewModelTest usa un ItemRepository falso, StandardTestDispatcher y un Notificador que registre mensajes. Verifica carga hasta que llegan datos y un solo aviso, error sin caché con reintento, y lectura de caché con favorito conservado cuando falla la red. Ejecuta :app:testDebugUnitTest y muestra los tres resultados.

**Diferencia.** La lista tiene tres pruebas automatizadas de su comportamiento.

**Reflexión.** Separar el repositorio permite reproducir fallos de red sin depender de PokéAPI.

## 02 · Identidad botánica y acceso

Compórtate como un experto en Jetpack Compose y accesibilidad. Ayúdame a reproducir el avance 02. Traslada los archivos Kotlin y VectorDrawable de docs/reproduccion/cambios/02 a sus rutas homónimas. Copia invernadero.png, prado.png y flores_prensadas.png a drawable-nodpi y las tres fuentes Manrope a res/font, tal como indica GUIA.md. Usa marfil #F7F6EE, bosque #2E533D, salvia #DFE9DB y rosa #F4E2E5, con un esquema oscuro. En LoginScreen, la escena ocupa el 43 % de alto, acotado a 160–360 dp, y se funde con el fondo. Deja una sola acción principal con vidrio y presión elástica, campos etiquetados y botón mostrar contraseña. Conserva el LoginViewModel y Firebase. Compila y compara con 01_login.png.

**Diferencia.** El acceso pasa a una composición ilustrada y suma visibilidad opcional de la contraseña.

**Reflexión.** Poner la ilustración arriba deja el formulario claro y utilizable con teclado.

## 03 · Catálogo, búsqueda y favoritos

Compórtate como un experto en diseño de navegación con Jetpack Compose. Ayúdame a reproducir el avance 03. Aplica los archivos de docs/reproduccion/cambios/03 y copia los nueve Pokémon locales de GUIA.md a drawable-nodpi. Crea una cuadrícula adaptable de mínimo 145 dp por columna, márgenes de 20 dp y separación de 12 dp. Una ficha contiene imagen, número, nombre y un corazón de 48 dp. La lupa abre un filtro por nombre o número y se puede cerrar. Favoritos usa las mismas fichas y muestra flores_prensadas.png con un botón Explorar catálogo cuando está vacío. La barra tiene dos destinos para usuario y tres para admin, con lente deslizante. Lleva Cerrar sesión al menú. Conserva los ViewModels, Room y las restricciones de rol. Compila y compara 02_catalogo.png, 04_favoritos.png y 06_busqueda.png.

**Diferencia.** La lista plana se convierte en una colección visual con búsqueda y favoritos accesibles.

**Reflexión.** Ocultar el campo hasta tocar la lupa deja más espacio para reconocer Pokémon.

## 04 · Ficha botánica

Compórtate como un experto en Compose y fichas de datos. Ayúdame a reproducir el avance 04. Aplica DetalleScreen.kt de docs/reproduccion/cambios/04. Conserva DetalleViewModel y las rutas. Usa prado.png a proporción 1.27 y superpone ArtePokemon centrado al 70 % del ancho y 77 % del alto, con corazón de vidrio arriba a la derecha. Debajo coloca el nombre en serif, número, tipos y las dos medidas con separadores. Convierte decímetros a metros y hectogramos a kilos. Si Room solo tiene el resumen, muestra la acción Reintentar para obtener la ficha. Compila y compara 03_detalle.png.

**Diferencia.** La ficha jerarquiza especie, tipos y medidas con una escena ilustrada.

**Reflexión.** Un área de imagen separada permite leer los datos sin que el fondo compita con ellos.

## 05 · Administración y estados

Compórtate como un experto en Material 3 y diseño de estados. Ayúdame a reproducir el avance 05. Aplica AdminScreen.kt y Estados.kt de docs/reproduccion/cambios/05. Usa una franja ilustrada de prado.png de 145 dp. Presenta los totales de Room en dos bloques de texto abiertos y un solo botón principal Sincronizar. Limpiar caché conserva confirmación y Cancelar. Muestra el progreso y anuncia el resultado con una región accesible. Mantén Reintentar en los errores recuperables. Comprueba que Admin sigue restringido al rol admin, que la caché funciona sin red y que la notificación se publica al sincronizar. Compara 07_admin.png y 08_sin_red.png.

**Diferencia.** El panel concentra la sincronización y separa la acción de borrar la caché.

**Reflexión.** Mostrar el progreso y confirmar el borrado evita repetir acciones por incertidumbre.

## 06 · Informe y reproducción

Compórtate como un experto en documentación de proyectos Android. Ayúdame a reproducir el avance 06 en mejora-ia. Conserva las capturas de fase 1 y agrega las verificadas a docs/capturas_fase2. Usa la guía y los manifiestos de docs/reproduccion para documentar las rutas exactas. Actualiza README.md con el recorrido, la configuración de Firebase y las pruebas. Incorpora este PROMPTS.md y el PDF en docs/informe. Identifica cualquier captura de un rol simulado o de un fallo de red controlado. Comprueba los enlaces, ejecuta :app:testDebugUnitTest y :app:assembleDebug y verifica que no se versionen configuraciones locales ni contraseñas.

**Diferencia.** El repositorio final incluye comparación, pruebas, recursos y seis prompts ordenados.

**Reflexión.** Indicar el origen de la evidencia permite repetir las pruebas y distinguir qué se verificó.
