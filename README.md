# KingOfTheHillNK

Plugin de eventos **King of the Hill** para **NukkitPetteriM1Edition (PM1E)**. Autor: **BrunoJaegerK**.

> Estado del desarrollo: primera implementación del núcleo publicada en `main`. El proyecto apunta a Java 17, igual que la rama `release` actual del repositorio PM1E consultado para esta implementación.

## Características incluidas

- Arenas independientes guardadas en YAML.
- Región rectangular mediante dos esquinas y zona de captura circular alrededor de un centro.
- Modos individual y equipos.
- Captura individual (un solo jugador en el punto) y por mayoría estricta (un grupo debe superar la mitad de los jugadores dentro).
- Estado de disputa cuando no existe un capturador elegible.
- Progreso de captura, cuenta regresiva y límite de duración.
- Asignación manual de equipos por arena.
- Recompensas por victoria y participación: comandos de consola y objetos por ID/meta/cantidad.
- Estadísticas persistentes: victorias, derrotas, capturas, eliminaciones, muertes, participaciones, rachas y tiempo acumulado de captura.
- Recompensas configurables por eliminaciones.
- Horarios automáticos por zona horaria, hora y días de la semana.
- Protección configurable de bloques, interacciones, cubos, líquidos, explosiones y teletransportes fuera de la arena.
- Control de daño entre aliados, disputas entre bandos y restricción de teletransportes de participantes fuera del cuboide.
- Protección de líquidos, explosiones y cubos mediante eventos reales de PM1E.
- Modo espectador y restauración del modo de juego y ubicación al salir/finalizar.
- HUD de progreso con BossBar nativa, sidebar de scoreboard y popup de Bedrock.
- Anillo de partículas configurable y sonidos nativos para inicio, disputa y victoria.

## Requisitos

- NukkitPetteriM1Edition basado en la API Nukkit.
- Java 17 o superior.
- Maven 3.8+.
- No requiere EconomyAPI, LlamaEconomy, Vault ni FakeInventories.
- ClanSystemMX es opcional; no es requisito de compilación ni ejecución.

## Compilar

Desde la raíz del proyecto:

```bash
mvn clean package
```

El JAR se genera en:

```
target/KingOfTheHillNK-1.0.0-SNAPSHOT.jar
```

La API está declarada con scope `provided`: no se empaqueta dentro del plugin. Por defecto Maven resuelve `cn.nukkit:nukkit:1.0-SNAPSHOT` desde OpenCollab. Para compilar contra el servidor exacto PM1E, coloca una copia del JAR del servidor en `libs/server.jar`; el perfil `pm1e-local` se activa automáticamente y compila contra ese archivo. El JAR del servidor está excluido de Git. No empaquetes la API dentro del plugin.

## Instalación

1. Ejecuta `mvn clean package`.
2. Copia el JAR de `target/` a `plugins/`.
3. Inicia el servidor.
4. Edita los archivos creados en `plugins/KingOfTheHillNK/`.
5. Usa `/koth help`.

## Crear una arena

Ejemplo para una arena llamada `castle`:

```
/koth create castle
/koth pos1 castle
/koth pos2 castle
/koth setcenter castle
/koth setspawn castle
/koth setspectator castle
/koth setmode castle teams
/koth setcapture castle majority
/koth settime castle 30
/koth start castle
```

- `pos1` y `pos2`: límites de la región protegida.
- `setcenter`: centro del punto de captura.
- `setspawn`: aparición de participantes.
- `setspectator`: aparición de espectadores.
- El radio inicial de captura es 6 bloques y se puede cambiar con `/koth setradius castle 8`. El límite de jugadores se cambia con `/koth setmaxplayers castle 30`.

## Comandos

### Jugadores

| Comando | Descripción |
|---|---|
| `/koth` o `/koth help` | Ayuda |
| `/koth list` | Lista las arenas |
| `/koth join <arena>` | Participa en un evento activo |
| `/koth leave` | Abandona el evento |
| `/koth spectate <arena>` | Entra como espectador |
| `/koth stats` | Consulta tus estadísticas |
| `/koth top` | Muestra el top de victorias |

### Administración (`koth.admin`)

| Comando | Descripción |
|---|---|
| `/koth create <arena>` | Crea una arena |
| `/koth delete <arena>` | Elimina una arena |
| `/koth pos1 <arena>` / `pos2` | Define esquinas |
| `/koth setcenter <arena>` | Define el centro de captura |
| `/koth setspawn <arena>` | Define spawn de participantes |
| `/koth setspectator <arena>` | Define spawn de espectadores |
| `/koth setmode <arena> <individual|teams>` | Cambia modalidad |
| `/koth setcapture <arena> <individual|majority>` | Cambia método |
| `/koth settime <arena> <segundos>` | Cambia duración de captura |
| `/koth setradius <arena> <bloques>` | Cambia radio de captura |
| `/koth setmaxplayers <arena> <cantidad>` | Cambia límite de participantes |
| `/koth setradius <arena> <bloques>` | Cambia radio de captura |
| `/koth setmaxplayers <arena> <cantidad>` | Cambia límite de participantes |
| `/koth setradius <arena> <bloques>` | Cambia radio del punto de captura (1–64) |
| `/koth setmaxplayers <arena> <cantidad>` | Cambia límite de participantes |
| `/koth team <arena> <jugador> <equipo>` | Asigna equipo manual |
| `/koth teamjoin <arena> <equipo>` | El jugador elige equipo durante el evento |
| `/koth teamleave <arena>` | El jugador abandona su equipo |
| `/koth autobalance <arena>` | Reparte participantes entre red y blue |
| `/koth start <arena>` | Inicia cuenta regresiva |
| `/koth stop <arena>` | Detiene el evento |
| `/koth schedule <arena> <HH:mm> <DÍAS>` | Programa horario |
| `/koth unschedule <arena>` | Elimina horario |
| `/koth setreward <arena>` | Indica cómo editar recompensas |
| `/koth reload` | Recarga configuraciones |

Días de programación: `MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY`. La zona horaria se configura en `schedule.yml`.

## Modalidades y captura

### Individual
Cada jugador es su propio grupo. Con el método `individual`, solo puede capturar si es el único participante vivo dentro del punto.

### Equipos
Los jugadores asignados al mismo equipo mediante `/koth team` comparten grupo. Los jugadores sin asignación cuentan como grupos individuales, salvo que `teams.require-assignment: true`, que los excluye de la captura.

### Método individual
Exige que haya exactamente un jugador elegible dentro de la zona.

### Método majority
El grupo debe tener más de la mitad de sus miembros vivos y participantes dentro del punto. Si hay jugadores de otro grupo dentro de la zona, el punto queda disputado y el progreso se pausa hasta que solo quede un bando. En modo individual cada jugador es su propio grupo.

## Recompensas

Edita `rewards.yml`:

```yaml
default:
  winner:
    commands:
      - "give {player} diamond 5"
      - "tell {player} ¡Ganaste {arena}!"
    items:
      - id: 264
        meta: 0
        amount: 5
  participation:
    commands:
      - "tell {player} Gracias por participar en {arena}"
    items: []
  team-winner:
    commands: []
    items: []
  kill:
    commands:
      - "tell {player} Has eliminado a un rival en {arena}"
    items: []
arenas: {}
```

Variables disponibles: `{player}`, `{uuid}`, `{arena}`, `{team}` y `{position}`. Los comandos se ejecutan desde consola. Los objetos se entregan directamente al inventario.

## Horarios

```yaml
enabled: true
timezone: "America/Guayaquil"
events:
  castle:
    time: "20:00"
    days: ["MONDAY", "WEDNESDAY", "FRIDAY"]
```

El scheduler comprueba los horarios una vez por minuto y evita ejecutar dos veces el mismo evento el mismo día.

## Configuración general

```yaml
event:
  countdown-seconds: 60
  max-duration-seconds: 1800
  capture-seconds: 30
  capture-radius: 6
  max-players: 50
  reset-progress-when-empty: false
  show-actionbar: true
  protect-region: true
  block-break: true
  block-place: true
  friendly-fire: false
teams:
  require-assignment: false
```

Los valores globales se aplican como defaults; cada arena guarda su propio tiempo, modalidad y método de captura.

## Integración con ClanSystemMX

El plugin declara ClanSystemMX como dependencia opcional (`softdepend`) y no necesita su JAR para compilar. Se revisó el archivo API.md enlazado por el proyecto: documenta `ClanSystemAPI.playerIsInClan(String)` y `ClanSystemAPI.getPlayer(String, Consumer)`, pero no especifica el paquete Java de la clase API ni el acceso documentado desde el objeto de jugador al identificador/tag del clan. Sin esos datos no es seguro agrupar jugadores por clan. Por eso la integración automática queda deliberadamente desactivada y el sistema de equipos propio funciona con `/koth team`. No se incluyen imports ni getters inventados.

## Arquitectura

```
com.brunojaegerk.koth
├── KingOfTheHillNK.java
├── arena/       # Arenas y persistencia
├── command/     # Enrutamiento de comandos
├── event/       # Estado y ciclo de vida de eventos
├── listener/    # Eventos de Nukkit
├── reward/      # Objetos y comandos de recompensa
├── scheduler/   # Horarios
├── statistics/  # Estadísticas persistentes
├── team/        # Equipos por arena
└── clan/        # Adaptador opcional de ClanSystemMX
```

## Limitaciones conocidas de esta primera entrega

- No se implementa todavía una sidebar persistente; el estado se muestra en BossBar y popup.
- La protección cubre colocación y rotura de bloques; no intercepta todavía cada interacción de inventario, líquidos, explosiones o teletransportes.
- La API pública disponible de ClanSystemMX no basta para resolver el ID de clan de forma verificable; se mantiene el proveedor manual.
- La captura por mayoría y la disputa se resuelven con mayoría absoluta; el combate PvP está permitido entre equipos rivales dentro del mismo evento.

Estas limitaciones se documentan para no presentar como implementado lo que todavía no existe. Las funciones se pueden ampliar sin introducir dependencias obligatorias.
