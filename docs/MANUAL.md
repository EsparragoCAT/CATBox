# CATBox — Retro Dialogue Framework

Manual de usuario y referencia del mod.

**CATBox** es un mod para NeoForge 1.21.1 que añade cuadros de diálogo animados
estilo retro: el texto se escribe letra a letra, con hablante, sonido, icono,
colores, tamaño y textura personalizables.

---

## Índice

1. [Requisitos e instalación](#requisitos-e-instalación)
2. [Cómo funciona](#cómo-funciona)
3. [Comandos](#comandos)
4. [Teclas](#teclas)
5. [Interfaz de configuración (tecla B)](#interfaz-de-configuración-tecla-b)
6. [Permisos](#permisos)
7. [Colores](#colores)
8. [Iconos](#iconos)
9. [Textura de caja](#textura-de-caja)
10. [Archivo de configuración](#archivo-de-configuración)
11. [Idiomas](#idiomas)
12. [Limitaciones conocidas](#limitaciones-conocidas)

---

## Requisitos e instalación

- **Minecraft 1.21.1** con **NeoForge** (build 21.1.252 o compatible).
- Para permisos con LuckPerms: **LuckPerms 5.4.150 o superior** (la 5.4.140 tiene
  un bug conocido que impide entrar al servidor; se soluciona actualizando).

**Instalación:** copia el archivo `.jar` del mod en la carpeta `mods/`
(del cliente y del servidor, si juegas en multijugador).

---

## Cómo funciona

El mod tiene dos formas de mostrar diálogos:

1. **Por comando** (`/textbox`): muestras un diálogo a uno o varios jugadores.
2. **Area chat**: cada jugador puede activar que *sus propios mensajes de chat*
   se conviertan en textbox para los jugadores cercanos, usando su visual personal.

La textbox aparece como un *overlay* (sobre el mundo) o como *pantalla completa*
(bloqueante), según la configuración.

---

## Comandos

### `/textbox` — mostrar un diálogo

```
/textbox <jugadores> <hablante> <texto> <sonido> <icono> <posición> <segundos> <bloqueante> [tamaño]
```

| Argumento | Descripción | Ejemplo |
|---|---|---|
| `jugadores` | A quién se muestra (`@a`, `@p`, nombre). | `@a` |
| `hablante` | Nombre del que habla (admite colores). | `Steve` |
| `texto` | El mensaje del diálogo. | `Hola` |
| `sonido` | ID de sonido al escribir (o `minecraft:air` para ninguno). | `minecraft:block.note_block.hat` |
| `icono` | Ítem, `@jugador` o URL de imagen. | `@Steve` |
| `posición` | `bottom` (sobre la vida) o `top` (bajo la bossbar). | `bottom` |
| `segundos` | Tiempo de la animación de escritura (mín. 0.1). | `3` |
| `bloqueante` | `true` = pantalla completa; `false` = overlay. | `false` |
| `tamaño` *(opcional)* | `100`, `90`, `80` o `70`. | `80` |

**Ejemplo:**

```
/textbox @a Steve Hola minecraft:block.note_block.hat @Steve bottom 3 false 80
```

> ⚠️ Por ahora `hablante` y `texto` son **una sola palabra** (sin espacios).
> Las frases con espacios requieren usar "Hola Hola" por como funciona el comando.

### `/textbox area chat` — activar/desactivar TU area de chat

```
/textbox area chat true|false
```

- Requiere **permiso de operador (nivel 2)**.
- Afecta **solo al jugador que lo ejecuta**.
- Cuando está en `true`, tus mensajes de chat se convierten en textbox para los
  jugadores dentro de tu radio (configurable), y **no** se envían al chat normal.

> También puedes activarlo/desactivarlo desde la interfaz con el botón **Area**.

---

## Teclas

| Tecla (por defecto) | Acción |
|---|---|
| `V` | Saltar la animación del texto. |
| `B` | Abrir la interfaz de configuración (requiere permiso). |

Ambas se pueden cambiar en **Opciones → Controles → CATBox**.

---

## Interfaz de configuración (tecla B)

Al pulsar `B` (con permiso) se abre la pantalla de configuración de tu textbox
de área. Cada campo tiene un *tooltip* (pasa el ratón por encima).

| Campo | Descripción | Rango / valores |
|---|---|---|
| **Nombre** | Nombre que se muestra como hablante. Vacío = tu nombre real. | texto |
| **Color** | Color del borde en hexadecimal. | `RRGGBB` (ej. `22C55E`) |
| **Sonido** | Sonido al escribir. Vacío = sin sonido. | ID de sonido |
| **Icono** | Ítem, `@jugador` o URL de imagen. Vacío = tu cabeza. | texto/URL |
| **Textura de caja** | URL de la textura de la caja (64×64, 9-slice). Vacío = caja por defecto. | URL |
| **Posición** | `arriba` (bajo la bossbar) o `abajo` (sobre la vida). | ciclo |
| **Bloqueante** | `Sí` = pantalla completa; `No` = overlay. | ciclo |
| **Radio** | Radio en bloques donde se ve tu textbox. | 4–24 (def. 8) |
| **Tiempo** | Segundos que tarda en escribirse el texto. | 0.5–30 (def. 3) |
| **Tamaño** | Tamaño del textbox. | 100 / 90 / 80 / 70 % |
| **Area** | Activa/desactiva tu area chat al instante. | Sí / No |

Al pulsar **Hecho** se guarda la config y se sincroniza con el servidor.

---

## Permisos

El nodo de permiso es **`textbox.area`**.

- **Por defecto**: lo tienen los jugadores con **nivel de OP 2 o superior**.
- En **singleplayer** siempre lo tienes (eres OP).
- Con **LuckPerms** puedes darlo a un grupo:

```
/lp group <grupo> permission set textbox.area true
```

Sin el permiso, al pulsar `B` aparece:
*"No puedes acceder a esta interfaz sin permiso de administración."*

---

## Colores

Puedes usar colores en el `hablante` y en el `texto`:

- **Hex**: `&#RRGGBB` → ej. `&#FF0000` (rojo).
- **Códigos de Minecraft**: `&0` a `&f` → ej. `&c` (rojo), `&a` (verde).

El **color del borde** se toma del color del **hablante** (el primer código de
color que encuentre). Ejemplo: `&#FF0000Steve` → borde rojo y "Steve" en rojo.

En el area chat, el color del borde se define con el campo **Color** de la
interfaz (que aplica ese color al hablante automáticamente).

---

## Iconos

El campo **Icono** (y el argumento `icono` del comando) acepta:

| Valor | Resultado |
|---|---|
| `minecraft:diamond` | El ítem correspondiente. |
| `@Steve` | La cabeza del jugador `Steve`. |
| `https://...png` | Una imagen cargada desde la URL (se descarga y cachea). |

> Las imágenes se dibujan como cuadrado (se estiran si no son cuadradas) y se
> descargan una vez por cliente. Usa hosts permanentes (ibb.co); los enlaces de
> adjuntos de **Discord caducan**.

---

## Textura de caja

Puedes darle a la caja un fondo personalizado con una textura **9-slice**.

- **Tamaño obligatorio**: `64 × 64` píxeles.
- **Borde (slice)**: `8` píxeles.
- Regiones: 4 esquinas (8×8), 4 bordes (se estiran), 1 centro (se estira en ambos ejes).

Plantilla de referencia: `docs/box_texture_template.png`
Especificación completa: `docs/BOX_TEXTURE_SPEC.md`

Se carga por **URL** (igual que el icono). Si el campo está vacío, se usa la caja
por defecto.

---

## Archivo de configuración

Tu config personal se guarda automáticamente en:

```
config/textbox-area.json
```

Ejemplo:

```json
{
  "name": "",
  "color": "22C55E",
  "sound": "minecraft:ui.button.click",
  "icon": "",
  "boxTexture": "",
  "position": "bottom",
  "time": 3.0,
  "blocking": false,
  "radius": 8,
  "size": 1.0
}
```

> No hace falta editarla a mano; es más fácil usar la interfaz (tecla B).
> Si la editas y dejas algo inválido, el mod lo corrige solo al cargarla.

---

## Idiomas

El mod incluye:

- **Inglés** (`en_us`)
- **Español** (`es_es`)

La interfaz y los mensajes se muestran según el idioma del juego.

---

## Limitaciones conocidas

- El comando `/textbox` no admite **frases con espacios** en `hablante` y `texto`.
- Las **imágenes** (icono y textura) se descargan por cliente (requieren internet).
- La textura de caja debe ser **exactamente 64×64**.
- El area chat se **apaga al reconectar** (no persiste entre sesiones).
- El toggle del area por comando no actualiza el estado mostrado en la interfaz
  al instante (usa el botón **Area** para mantenerlo sincronizado).
