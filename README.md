# Pokédex Planta

Laboratorio integrador 11–12 · Fase 2 · Daniella Leon.

Catálogo de Pokémon de tipo Planta. Ambos roles pueden consultar fichas, guardar favoritos y cerrar sesión. El administrador puede ver los totales, sincronizar y limpiar la caché del dispositivo.

## Abrir el proyecto

1. Abre la raíz de este proyecto en Android Studio con soporte para AGP 9.1.1 y Android SDK 37. Gradle utiliza Java 21. El mínimo para instalar la app es Android 7.0 (API 24).
2. Registra una app Android con el paquete exacto `com.tecsup.pokemon` en tu proyecto de Firebase.
3. Coloca tu archivo `google-services.json` en `app/`. Está excluido de Git.
4. Habilita correo y contraseña en Firebase Authentication y crea dos cuentas de prueba. Copia el UID de cada una.
5. En Firestore crea `usuarios/{uid}` para cada cuenta. Añade el campo `nombre` y el campo `rol`, con el valor exacto `admin` o `usuario`, ambos de tipo texto.
6. Publica el contenido de `firestore.rules` en Firebase Console → Firestore Database → Reglas.
7. Sincroniza Gradle y ejecuta en un emulador con Google Play o un dispositivo conectado.

Se puede compilar sin el archivo JSON. La pantalla de login indica que falta la configuración al intentar ingresar. El catálogo, los roles y las pruebas con las cuentas reales se habilitan al conectar Firebase.

## Fuentes de datos

- Lista: [PokéAPI /type/grass](https://pokeapi.co/api/v2/type/grass).
- Ficha de ejemplo: [PokéAPI /pokemon/lilligant](https://pokeapi.co/api/v2/pokemon/lilligant).
- Las ilustraciones se cargan desde los recursos de PokéAPI. Las imágenes descargadas pueden permanecer en la caché del cargador de imágenes.
- `ItemDto` recibe la API, `ItemEntity` guarda datos locales e `Item` es el modelo que consumen las pantallas.
- Compose observa el ViewModel. El ViewModel depende de interfaces y Room alimenta el catálogo y los favoritos. Retrofit actualiza Room. La actualización conserva favoritos y fichas descargadas.
- El rol proviene del documento Firestore del usuario autenticado. El administrador opera sobre los datos locales de esta instalación.

## Recorrido de prueba

1. Intenta entrar con campos vacíos y con una contraseña incorrecta. Comprueba que aparece un mensaje y que sigues en el login.
2. Entra como usuario. Confirma la lista de Planta y abre una ficha. No debe aparecer Admin.
3. Guarda un favorito y reabre la app. Comprueba que se conserva la sesión y que el favorito sigue en su pantalla.
4. Con datos guardados, desconecta internet y vuelve a abrir el catálogo. Debe mostrar la lista y el aviso. Si no hay datos, debe aparecer el error con Reintentar.
5. Entra como admin. Revisa los dos totales. Sincroniza y confirma que el favorito sobrevive. Limpia la caché y confirma que los dos totales quedan en cero. Vuelve a sincronizar para recuperar el catálogo.
6. En Android 13 o posterior, concede el permiso de notificaciones. Después de sincronizar debe aparecer «Se guardaron N elementos» y al tocar el aviso se abre la app. Al negar el permiso, la app sigue funcionando.
7. Gira el teléfono durante el recorrido. La pantalla actual y su ViewModel deben conservarse. Cierra sesión y reabre para confirmar que vuelve al login.

Autoría individual: Daniella Leon. La fase 1 vive en `main` y la fase 2 en `mejora-ia`.

## Capturas de la fase 1

[Login](docs/capturas/01_login.png) · [Catálogo](docs/capturas/02_catalogo.png) · [Detalle](docs/capturas/03_detalle.png) · [Favoritos](docs/capturas/04_favoritos.png) · [Panel Admin](docs/capturas/05_panel_admin.png) · [Notificación de Android](docs/capturas/06_notificacion.png)

Las capturas del catálogo y del panel se tomaron con la aplicación ejecutándose, datos reales de PokéAPI y una sesión de revisión simulada. Se comprobó el catálogo de 156 entradas, la ficha de Bulbasaur, la conservación de favoritos tras sincronizar, el borrado de la caché y la publicación del aviso de Android. El acceso con las cuentas reales queda pendiente de configurar Firebase.

## Cambios de la fase 2

- Acceso con ilustración de invernadero y controles de vidrio.
- Catálogo con imágenes, búsqueda por nombre o número y favoritos.
- Ficha botánica con tipos, peso y altura.
- Panel de administrador con totales locales, sincronización y confirmación para limpiar la caché.
- Tres pruebas de ListaViewModel. El recorrido de emulador comprueba navegación, favoritos, aviso de sincronización y lectura sin red.

[Informe](docs/informe/Pokedex_Planta_Fase2_Daniella_Leon.pdf) · [Prompts](PROMPTS.md) · [Guía de reproducción](docs/reproduccion/GUIA.md)

[Acceso](docs/capturas_fase2/01_login.png) · [Catálogo](docs/capturas_fase2/02_catalogo.png) · [Detalle](docs/capturas_fase2/03_detalle.png) · [Favoritos](docs/capturas_fase2/04_favoritos.png) · [Favoritos vacíos](docs/capturas_fase2/05_favoritos_vacios.png) · [Búsqueda](docs/capturas_fase2/06_busqueda.png) · [Panel Admin](docs/capturas_fase2/07_admin.png) · [Sin conexión](docs/capturas_fase2/08_sin_red.png)

Las capturas de la versión base se hicieron en una sesión de revisión. La captura sin red fuerza un fallo de red para comprobar la caché. Firebase Authentication y las reglas publicadas se documentan por separado en el informe. El informe incluye la prueba de inicio de sesión con las cuentas de Firebase.

## Comparador de Pokémon

Abre una ficha, toca **Elegir para comparar** y elige otro Pokémon. **Comparar con el primero** abre una vista con sus tipos, alturas y pesos. Las barras comparten escala por medida. El botón sobre las imágenes intercambia sus posiciones. **Elegir otros Pokémon** vuelve al catálogo y limpia la selección.

La demostración usa `daniella.leon@example.com` para el catálogo y `admin@example.com` para el panel de administración. Las contraseñas están en la copia de entrega del informe.
