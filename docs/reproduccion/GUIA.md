# Reproducir la fase 2

Abre el estado final de fase 1 en Android Studio y crea la rama `mejora-ia`. Desde la entrega final de GitHub, copia `docs/reproduccion/` y `docs/capturas_fase2/` para que Gemini pueda consultar las referencias. Luego aplica los prompts en el orden indicado.

Las rutas de `cambios/01` a `cambios/05` son relativas a la raíz del proyecto. Copia cada archivo a esa misma ruta cuando el prompt lo indique.

## Imágenes y tipografía

Copia los archivos de la rama final a `app/src/main/res/drawable-nodpi/` manteniendo el nombre exacto.

- Avance 02: `invernadero.png`, `prado.png` y `flores_prensadas.png`.
- Avance 03: `pokemon_1.png`, `pokemon_2.png`, `pokemon_3.png`, `pokemon_43.png`, `pokemon_44.png`, `pokemon_45.png`, `pokemon_182.png`, `pokemon_549.png` y `pokemon_763.png`. Las ilustraciones de Pokémon proceden del arte oficial disponible en PokéAPI.

Copia también `manrope_regular.ttf`, `manrope_semibold.ttf` y `manrope_bold.ttf` a `app/src/main/res/font/`. La licencia está en `docs/licencias/Manrope-OFL.txt`.

## Verificación

Ejecuta `gradlew :app:testDebugUnitTest :app:assembleDebug` después de cada bloque. En Windows usa `gradlew.bat`. Compara el emulador con `docs/capturas_fase2/`. La guía de Firebase está en el README. El archivo `app/google-services.json` se guarda localmente.

## Archivos del avance 01

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/test/java/com/tecsup/pokemon/ListaViewModelTest.kt`

## Archivos del avance 02

- `app/src/main/java/com/tecsup/pokemon/ui/theme/Color.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/theme/Theme.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/theme/Type.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/components/Vidrio.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/login/LoginScreen.kt`
- `app/src/main/res/drawable-nodpi/invernadero.png`
- `app/src/main/res/drawable-nodpi/prado.png`
- `app/src/main/res/drawable-nodpi/flores_prensadas.png`
- `app/src/main/res/font/manrope_regular.ttf`
- `app/src/main/res/font/manrope_semibold.ttf`
- `app/src/main/res/font/manrope_bold.ttf`
- `docs/licencias/Manrope-OFL.txt`
- `app/src/main/res/drawable/poke_libro.xml`
- `app/src/main/res/drawable/poke_corazon.xml`
- `app/src/main/res/drawable/poke_corazon_lleno.xml`
- `app/src/main/res/drawable/poke_buscar.xml`
- `app/src/main/res/drawable/poke_mas.xml`
- `app/src/main/res/drawable/poke_volver.xml`
- `app/src/main/res/drawable/poke_admin.xml`
- `app/src/main/res/drawable/poke_cerrar.xml`
- `app/src/main/res/drawable/poke_ojo.xml`
- `app/src/main/res/drawable/poke_ojo_cerrado.xml`
- `app/src/main/res/drawable/poke_refrescar.xml`
- `app/src/main/res/drawable/poke_red.xml`

## Archivos del avance 03

- `app/src/main/java/com/tecsup/pokemon/ui/components/ArtePokemon.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/components/BarraVidrio.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/lista/Busqueda.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/lista/ListaScreen.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/favoritos/FavoritosScreen.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/navigation/AppNav.kt`
- `app/src/main/res/drawable-nodpi/pokemon_1.png`
- `app/src/main/res/drawable-nodpi/pokemon_2.png`
- `app/src/main/res/drawable-nodpi/pokemon_3.png`
- `app/src/main/res/drawable-nodpi/pokemon_43.png`
- `app/src/main/res/drawable-nodpi/pokemon_44.png`
- `app/src/main/res/drawable-nodpi/pokemon_45.png`
- `app/src/main/res/drawable-nodpi/pokemon_182.png`
- `app/src/main/res/drawable-nodpi/pokemon_549.png`
- `app/src/main/res/drawable-nodpi/pokemon_763.png`

## Archivos del avance 04

- `app/src/main/java/com/tecsup/pokemon/ui/detalle/DetalleScreen.kt`

## Archivos del avance 05

- `app/src/main/java/com/tecsup/pokemon/ui/admin/AdminScreen.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/components/Estados.kt`

## Archivos del avance 07

El comparador usa `prado.png` y los recursos de Pokémon que ya se agregaron. Aplica estos archivos.

- `app/src/main/java/com/tecsup/pokemon/ui/navigation/AppNav.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/detalle/DetalleScreen.kt`
- `app/src/main/java/com/tecsup/pokemon/ui/comparar/CompararScreen.kt`
- `app/src/main/res/drawable/poke_comparar.xml`
