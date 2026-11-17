# KingOfTheHillNK

Plugin de eventos **King of the Hill** para **NukkitPetteriM1Edition (PM1E)**.

- **Autor:** BrunoJaegerK
- **Java:** 17
- **Build:** Maven
- **Repositorio:** https://github.com/BrunoJaegerK/KothNK

## Funciones implementadas

- Múltiples arenas independientes, cada una con mundo, cuboide, punto de captura, spawns, radio, duración y límite de participantes.
- Modalidades individual y equipos.
- Captura individual y por mayoría de los miembros vivos del grupo.
- Disputa real: si dos grupos rivales están dentro del punto, la captura se pausa hasta que solo quede un grupo.
- Equipos internos por arena: asignación administrativa, elección de equipo por jugador y balance automático red/blue.
- Conteo regresivo, tiempo máximo del evento y scheduler por zona horaria/días.
- Recompensas de victoria individual, victoria de equipo, participación y eliminación. Se admiten comandos de consola y objetos por ID/meta/cantidad.
- Estadísticas persistentes: victorias, derrotas, capturas, kills, muertes, participaciones, racha actual, mejor racha y segundos capturando.
- BossBar, sidebar nativa de PM1E, popup, partículas y sonidos configurables.
- Protección de cuboide activo: romper/colocar bloques, interacción con bloques, cubos, flujo de líquidos, explosiones y teletransportes fuera de la arena.
- Daño PvP restringido al mismo evento, control de fuego amigo y detección de proyectiles disparados por jugadores.
- Modo espectador y restauración de ubicación/modo de juego al salir o terminar un evento.
- Limpieza de tareas, HUD y estados temporales al deshabilitar o recargar el plugin.

## Requisitos

- NukkitPetteriM1Edition basado en la API Nukkit.
- Java 17 o superior.
- Maven 3.8.6 o superior.
- No requiere EconomyAPI, LlamaEconomy, Vault ni FakeInventories.
- ClanSystemMX es opcional y no es necesario para compilar ni ejecutar.

## Compilación

Desde la raíz del proyecto:

```bash
mvn clean package
```

El JAR se genera en:

```
target/KingOfTheHillNK-1.0.0-SNAPSHOT.jar
```

La API se declara con scope `provided`, por lo que no se empaqueta dentro del plugin.

**Compilar contra PM1E exacto:** coloca una copia del JAR del servidor en `libs/server.jar`. Maven activa automáticamente el perfil `pm1e-local` y compila contra ese JAR. El archivo está excluido de Git.

**Compilación sin JAR local:** el perfil por defecto usa `cn.nukkit:nukkit:1.0-SNAPSHOT` desde OpenCollab. Es una API de compilación compatible a nivel de Nukkit, pero para verificar la distribución exacta del servidor se recomienda el perfil local.

El workflow de GitHub Actions está configurado para compilar con Java 17 en cada push a `main` y en pull requests.

## Instalación

1. Ejecuta `mvn clean package`.
2. Copia el JAR de `target/` a la carpeta `plugins/` del servidor.
3. Inicia el servidor.
4. Configura los archivos de `plugins/KingOfTheHillNK/`.
5. Ejecuta `/koth help`.

## Crear y configurar una arena

Ejemplo para una arena llamada `castle`:

```text
/koth create castle
/koth pos1 castle
/koth pos2 castle
/koth setcenter castle
/koth setspawn castle
/koth setspectator castle
/koth setmode castle teams
/koth setcapture castle majority
/koth settime castle 30
/koth setradius castle 8
/koth setmaxplayers castle 30
/koth start castle
```

- `pos1` y `pos2`: esquinas del cuboide protegido.
- `setcenter`: centro del punto de captura.
- `setspawn`: spawn de participantes.
- `setspectator`: spawn de espectadores.
- El radio predeterminado es 6 bloques; duración predeterminada 30 segundos y máximo 50 participantes. Los valores globales se aplican al crear una arena; después cada arena guarda su propia configuración.
- Todas las posiciones de una arena deben estar en el mismo mundo.

## Comandos

### Jugadores

| Comando | Descripción |
|---|---|
| `/koth` o `/koth help` | Ayuda |
| `/koth list` | Lista arenas |
| `/koth join <arena>` | Participa en un evento activo |
| `/koth leave` | Abandona el evento |
| `/koth spectate <arena>` | Entra como espectador |
| `/koth teamjoin <arena> <equipo>` | Elige equipo durante el evento |
| `/koth teamleave <arena>` | Sale del equipo asignado |
| `/koth stats` | Consulta estadísticas personales |
| `/koth top` | Top global de victorias |

### Administración — permiso `koth.admin`

| Comando | Descripción |
|---|---|
| `/koth create <arena>` | Crea arena |
| `/koth delete <arena>` | Elimina arena |
| `/koth pos1 <arena>` / `pos2` | Define las esquinas |
| `/koth setcenter <arena>` | Define centro de captura |
| `/koth setspawn <arena>` | Define spawn de participantes |
| `/koth setspectator <arena>` | Define spawn de espectadores |
| `/koth setmode <arena> <individual|teams>` | Selecciona modalidad |
| `/koth setcapture <arena> <individual|majority>` | Selecciona método |
| `/koth settime <arena> <segundos>` | Duración de captura |
| `/koth setradius <arena> <bloques>` | Radio de captura, entre 1 y 64 |
| `/koth setmaxplayers <arena> <cantidad>` | Límite de participantes |
| `/koth team <arena> <jugador> <equipo>` | Asigna equipo a un jugador conectado |
| `/koth autobalance <arena>` | Divide participantes en red y blue |
| `/koth start <arena>` | Inicia cuenta regresiva |
| `/koth stop <arena>` | Detiene evento |
| `/koth schedule <arena> <HH:mm> <DÍAS>` | Programa evento |
| `/koth unschedule <arena>` | Elimina horario |
| `/koth setreward <arena>` | Muestra ubicación de configuración de recompensas |
| `/koth reload` | Recarga configuración y detiene eventos activos |

Los días se indican en inglés: `MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY`.

## Modalidades y reglas de captura

### Individual

Cada jugador es un grupo independiente. Con método `individual`, solo captura cuando hay exactamente un jugador elegible dentro del punto. Si entra otro participante rival, se disputa.

### Equipos

Los jugadores con el mismo equipo asignado comparten grupo. Los equipos rivales que coincidan en el punto disputan el control. Los jugadores sin asignación forman grupos individuales, salvo que `teams.require-assignment: true`, en cuyo caso no cuentan para capturar.

### Método individual

Exige exactamente un jugador dentro del punto y ningún grupo rival presente.

### Método majority

Más de la mitad de los miembros vivos y participantes del grupo deben estar dentro del punto. Si aparece cualquier grupo rival, la captura se pausa hasta que se resuelva el combate o abandone la zona.

## Recompensas

Edita `rewards.yml`:

```yaml
default:
  winner:
    commands:
      - "give {player} diamond 5"
      - "tell {player} Ganaste {arena}"
    items:
      - id: 264
        meta: 0
        amount: 5
  team-winner:
    commands:
      - "tell {player} Tu equipo {team} ganó {arena}"
    items: []
  participation:
    commands:
      - "tell {player} Gracias por participar en {arena}"
    items: []
  kill:
    commands:
      - "tell {player} Eliminación en {arena}"
    items: []
arenas: {}
```

Las recompensas específicas por arena se pueden colocar en `arenas.<nombre>.<tipo>`; si no existen, se usan las de `default`.

Variables disponibles: `{player}`, `{uuid}`, `{arena}` y `{team}`. Los comandos se ejecutan desde consola. Los objetos se agregan al inventario y los sobrantes se dejan caer junto al jugador.

No hay dependencia económica: las recompensas de dinero se pueden ejecutar mediante comandos de otro plugin si el servidor lo utiliza.

## Programación automática

`schedule.yml`:

```yaml
enabled: true
timezone: "America/Guayaquil"
events:
  castle:
    time: "20:00"
    days: ["MONDAY", "WEDNESDAY", "FRIDAY"]
```

El scheduler comprueba la hora local una vez por minuto y evita ejecutar dos veces la misma arena el mismo día.

## Configuración general

`config.yml`:

```yaml
event:
  countdown-seconds: 60
  max-duration-seconds: 1800
  capture-seconds: 30
  capture-radius: 6
  max-players: 50
  reset-progress-when-empty: false
  show-actionbar: true
  show-bossbar: true
  show-scoreboard: true
  protect-region: true
  protect-interactions: true
  protect-liquids: true
  protect-explosions: true
  protect-teleports: true
  block-break: true
  block-place: true
  friendly-fire: false
effects:
  particles: true
  sounds: true
teams:
  require-assignment: false
```

Los valores de captura, radio y límite se copian a cada arena al crearla. Los cambios globales posteriores no sobrescriben arenas existentes.

## Estadísticas

`stats.yml` guarda por UUID: victorias, derrotas, capturas, kills, muertes, participaciones, racha actual, mejor racha y segundos acumulados capturando. Se guarda periódicamente y al cerrar, detener o finalizar eventos.

## Integración con ClanSystemMX

El plugin declara ClanSystemMX como `softdepend`, pero no enlaza clases inventadas. El API.md compartido documenta `ClanSystemAPI.playerIsInClan(String)` y `ClanSystemAPI.getPlayer(String, Consumer)`, pero no especifica el paquete Java ni expone un método documentado para obtener el identificador/tag del clan desde el objeto devuelto. Por eso la agrupación automática por clanes queda pendiente de un contrato Java verificable. Los equipos internos funcionan sin ClanSystemMX.

## Estructura del código

```
com.brunojaegerk.koth
├── KingOfTheHillNK.java
├── arena/       # Arenas, regiones y persistencia
├── clan/        # Adaptador opcional ClanSystemMX
├── command/     # Comandos
├── effect/      # Partículas y sonidos
├── event/       # Estados, captura, combate y ciclo de vida
├── listener/    # Eventos de PM1E
├── reward/      # Recompensas
├── scheduler/   # Horarios
├── scoreboard/  # BossBar y sidebar
├── statistics/  # Estadísticas persistentes
└── team/        # Equipos por arena
```

## Pendientes conocidos

- Integración automática de clanes: falta que ClanSystemMX documente el paquete Java y el acceso al ID/tag del clan.
- La protección de contenedores se realiza cancelando interacciones de bloque; no se interceptan de forma general todos los paquetes de inventario enviados por clientes modificados.
- No hay integración nativa con economía ni con plugins de crates; se pueden ejecutar comandos externos desde recompensas.
- La compilación contra el JAR exacto PM1E requiere disponer del archivo `libs/server.jar`. El perfil público de Nukkit sirve como alternativa de compilación, pero la verificación final debe hacerse contra la misma distribución del servidor objetivo.

