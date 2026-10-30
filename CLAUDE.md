# LoL Live Coach (nombre provisional)

App multiplataforma (Desktop, Android, iOS) en **Kotlin Multiplatform + Compose Multiplatform**.
Dada una partida de League of Legends en vivo, recomienda builds según el campeón propio, aliados y enemigos, y da tips (del campeón, de la partida y generales).

**Alcance: uso personal / muy pocos usuarios. No se publica en tiendas.**

**Prioridad: Desktop primero.** Móvil queda para después y solo se aborda al terminar las Fases 1-3. Aun así, diseñar desde ya para que añadir móvil sea barato: usar la interfaz `GameDataSource` y un modelo de partida común donde los datos en vivo (ítems, nivel, oro) sean **opcionales**, porque `spectator-v5` no los entrega.

## Decisiones ya tomadas

- **Sin backend.** Las API keys viven en un archivo local ignorado por git (`local.properties` o `secrets.properties`). Nunca commitear keys.
- **No usar op.gg** (sin API pública, scraping frágil y contra sus términos).
- **Identidad del jugador = Riot ID** (`Nombre#TAG`), no "nickname". Se resuelve a PUUID con `account-v1`.
- **Datos estáticos** (campeones, ítems, runas, imágenes): Data Dragon (`https://ddragon.leagueoflegends.com/api/versions.json` para la versión vigente).
- **Builds:** reglas simples sobre datos estáticos primero (ej. mucho daño AP enemigo → resistencia mágica). Tips narrativos con la API de Claude, enviando contexto estructurado (campeón, aliados, enemigos, ítems, minuto).
- **No mostrar información que el juego no entrega normalmente** (respetar políticas de Riot).

## Fuentes de datos

| Plataforma | Fuente | Notas |
|---|---|---|
| Desktop | Live Client Data API `https://127.0.0.1:2999/liveclientdata/allgamedata` | Sin API key. Certificado autofirmado: aceptarlo **solo** para `127.0.0.1`. Endpoints útiles: `allgamedata`, `activeplayer`, `playerlist`, `gamestats`, `eventdata`. Solo responde con partida en curso. |
| Móvil (y fallback desktop) | `account-v1` → `spectator-v5` | Header `X-Riot-Token`. `account-v1` usa host regional (americas/europe/asia); `spectator-v5` usa host de plataforma (la1, la2, na1, euw1...). |
| Todas | Data Dragon | Imágenes e info estática. |

La key de desarrollo de Riot caduca cada 24 h. Alternativa: registrar una personal API key (verificar opciones vigentes en developer.riotgames.com).

## Arquitectura propuesta

- `commonMain`: modelos (`kotlinx.serialization`), Ktor Client, repositorios, motor de recomendaciones, ViewModels, UI Compose.
- `jvmMain` (Desktop): cliente HTTP de Live Client (confía solo en el certificado raíz de Riot, `riotgames.pem`, y solo para 127.0.0.1). DTOs, mapper y `LiveClientGameRepository` viven en `commonMain`.
- `androidMain` / `iosMain`: implementación de `LiveGameRepository` con `spectator-v5` (búsqueda por Riot ID).
- Interfaz de dominio `LiveGameRepository` con implementación por plataforma, enlazada en `platformModule` (`expect`/`actual`) de Koin.
- Caché en memoria al inicio. SQLDelight solo si hace falta.

### Módulos y paquetes

- Se mantienen solo los módulos actuales: `shared`, `desktopApp`, `androidApp`, `iosApp`. No crear módulos Gradle nuevos sin una razón concreta (tiempos de build, reutilización real).
- Dentro de `shared`, organizar **por feature**: `game/`, `recommendations/`, `tips/`, `staticdata/`, `core/` (red, DI, utilidades).
- Cada feature separa capas en `presentation/`, `domain/` y `data/`:
  - `domain`: modelos de dominio puros (sin `@Serializable` ni Ktor), interfaces de repositorio y casos de uso. No depende de `data` ni de `presentation`.
  - `data`: DTOs `@Serializable`, data sources, implementaciones de repositorio y mappers DTO → dominio.
  - `presentation`: ViewModels MVI y pantallas Compose. Depende solo de `domain`.
- Los mappers DTO → dominio son obligatorios: Live Client y `spectator-v5` entregan formas distintas y deben converger en el mismo modelo de partida.

### Patrón de presentación: MVI

- Cada pantalla tiene un `XxxViewModel` que expone un único `StateFlow<XxxState>` y recibe acciones vía `fun onIntent(intent: XxxIntent)`.
- `XxxState`: `data class` inmutable (o `sealed interface` cuando los estados son excluyentes: sin partida / en partida / error).
- `XxxIntent`: `sealed interface` con las acciones del usuario.
- Efectos de una sola vez (navegación, toasts) con `Channel`/`Flow` de `XxxEffect` **solo si la pantalla los necesita**.
- Sin librería MVI (Orbit, MVIKotlin...): `ViewModel` + `StateFlow` + `update {}` bastan.
- La UI Compose es una función de `state` + lambda `onIntent`; sin lógica.

## Estilo de código

- El código debe entenderse sin comentarios: nombres descriptivos, funciones cortas con una sola responsabilidad. Comentar solo el *porqué* de algo no obvio (p. ej. por qué se acepta el cert de 127.0.0.1).
- SOLID con sentido práctico: interfaces solo donde haya más de una implementación real o se necesite un doble en tests (ej. `LiveGameRepository`). No crear `Interface` + `InterfaceImpl` por defecto.
- Evitar boilerplate: preferir `data class`, `sealed interface`, funciones de extensión y valores por defecto sobre builders o jerarquías.
- Casos de uso: una clase por acción con `operator fun invoke(...)`, nombrados con verbo (`ObserveLiveGame`, `RecommendBuild`). **Siempre** hay caso de uso entre ViewModel y repositorio: los ViewModels nunca dependen de repositorios directamente.
- DI con **Koin** (`koin-core`, `koin-compose-viewmodel`): inyección por constructor, un módulo Koin por feature más uno por plataforma; los ViewModels se obtienen con `koinViewModel()`.

## Roadmap (marcar al completar)

### Fase 1 — Desktop MVP
- [x] Cliente Ktor para Live Client Data API (cert local aceptado solo en 127.0.0.1)
- [x] Modelos `@Serializable` para `allgamedata` (jugador activo, `allPlayers`, equipos, ítems, `gameData`)
- [x] Polling cada ~2-5 s; estados: sin partida / en partida / error
- [x] Pantalla: mi campeón, aliados, enemigos
- [x] Imágenes de campeones de Data Dragon en la pantalla (Coil 3)

### Fase 2 — Recomendaciones
- [x] Carga y caché de datos de Data Dragon (campeones, ítems)
- [x] Clasificar daño enemigo (AD/AP/mixto) y sugerir ítems por reglas
- [x] Pantalla de recomendaciones (build + notas)

### Fase 3 — Tips con IA
- [ ] Cliente de la API de Claude (key local)
- [ ] Prompt con contexto estructurado de la partida
- [ ] Mostrar tips: campeón / partida / generales

### Fase 4 — Móvil (POSTERGADA, no iniciar sin pedirlo)
- [ ] Pantalla de búsqueda: Riot ID + región
- [ ] `account-v1` → PUUID → `spectator-v5`
- [ ] Reutilizar pantallas y motor de recomendaciones de commonMain

## Convenciones

- Kotlin idiomático, corrutinas + `Flow`, sin lógica de negocio en la UI.
- Todo lo posible en `commonMain`; código de plataforma solo para fuentes de datos.
- Commits pequeños por tarea del roadmap, con Conventional Commits (`feat:`, `fix:`, `build:`, `docs:`, `refactor:`, `test:`).
- Antes de agregar una dependencia, preguntar o justificarla.

## Flujo de ramas (Gitflow)

- `main`: solo versiones estables. Nunca se commitea directo.
- `develop`: rama de integración. Nunca se commitea directo; todo entra por PR.
- `feature/<descripcion-corta>`: sale de `develop`, PR hacia `develop`. Una por tarea o grupo de tareas del roadmap (ej. `feature/phase-2-damage-rules`).
- `release/<version>`: sale de `develop` al cerrar una fase; solo ajustes finales. PR hacia `main` y merge de vuelta a `develop`. Etiquetar `main` con `v<version>`.
- `hotfix/<descripcion-corta>`: sale de `main` para corregir algo urgente; PR hacia `main` y merge de vuelta a `develop`.
- Antes de empezar trabajo nuevo: actualizar `develop` y crear la rama correspondiente.

## Primera tarea al abrir el repo

1. Revisar la estructura actual del proyecto KMP (módulos, `libs.versions.toml`, targets).
2. Proponer las dependencias faltantes (Ktor Client, kotlinx.serialization, Koin, coroutines) antes de instalarlas.
3. Empezar la Fase 1.
