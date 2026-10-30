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
- `desktopMain`: `LiveClientDataSource` (polling a 127.0.0.1:2999 con cliente que acepta el cert local).
- `androidMain` / `iosMain`: `SpectatorDataSource` (búsqueda por Riot ID).
- Interfaz común `GameDataSource` con implementaciones por plataforma (`expect/actual` o inyección con Koin).
- Caché en memoria al inicio. SQLDelight solo si hace falta.

## Roadmap (marcar al completar)

### Fase 1 — Desktop MVP
- [ ] Cliente Ktor para Live Client Data API (cert local aceptado solo en 127.0.0.1)
- [ ] Modelos `@Serializable` para `allgamedata` (jugador activo, `allPlayers`, equipos, ítems, `gameData`)
- [ ] Polling cada ~2-5 s; estados: sin partida / en partida / error
- [ ] Pantalla: mi campeón, aliados, enemigos (con imágenes de Data Dragon)

### Fase 2 — Recomendaciones
- [ ] Carga y caché de datos de Data Dragon (campeones, ítems)
- [ ] Clasificar daño enemigo (AD/AP/mixto) y sugerir ítems por reglas
- [ ] Pantalla de recomendaciones (build + notas)

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
- Commits pequeños por tarea del roadmap.
- Antes de agregar una dependencia, preguntar o justificarla.

## Primera tarea al abrir el repo

1. Revisar la estructura actual del proyecto KMP (módulos, `libs.versions.toml`, targets).
2. Proponer las dependencias faltantes (Ktor Client, kotlinx.serialization, Koin, coroutines) antes de instalarlas.
3. Empezar la Fase 1.
