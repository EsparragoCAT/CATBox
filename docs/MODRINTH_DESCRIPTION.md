# CATBox

### Retro Dialogue Framework

**CATBox** añade **cuadros de diálogo animados estilo retro** a Minecraft. El texto se escribe letra a letra, con hablante, sonido, icono, colores y tamaño totalmente personalizables. Además incluye un modo **area chat** que convierte los mensajes del chat en diálogos locales para los jugadores cercanos.

![CATBox](https://raw.githubusercontent.com/EsparragoCAT/CATBox/main/docs/logo.png)

---

## ✨ Funciones

- 💬 **Diálogos animados** — el texto aparece letra a letra, con sonido de escritura.
- 🎨 **Totalmente personalizable** — hablante, color del borde, sonido, posición, tiempo y tamaño.
- 🗣️ **Area chat** — tus mensajes de chat se muestran como textbox a los jugadores cercanos, con radio configurable.
- 🖼️ **Iconos flexibles** — ítem de Minecraft, cabeza de jugador (`@jugador`) o imagen por URL.
- 🧩 **Texturas de caja** — fondo personalizado con texturas 9-slice.
- 🔐 **Permisos** — compatible con LuckPerms.
- 🌐 **Multi-idioma** — inglés y español.

---

## 🎮 Comandos

### Mostrar un diálogo

```
/textbox <jugadores> <hablante> <texto> <sonido> <icono> <posición> <segundos> <bloqueante> [tamaño]
```

**Ejemplo:**

```
/textbox @a Steve Hola minecraft:block.note_block.hat @Steve bottom 3 false 80
```

| Argumento | Descripción |
|---|---|
| `jugadores` | A quién se muestra (`@a`, `@p`, nombre). |
| `hablante` | Nombre del que habla (admite colores). |
| `texto` | El mensaje del diálogo. |
| `sonido` | ID de sonido al escribir (`minecraft:air` = sin sonido). |
| `icono` | Ítem, `@jugador` o URL de imagen. |
| `posición` | `bottom` (sobre la vida) o `top` (bajo la bossbar). |
| `segundos` | Duración de la animación de escritura. |
| `bloqueante` | `true` = pantalla completa; `false` = overlay. |
| `tamaño` | *(opcional)* `100`, `90`, `80` o `70`. |

### Activar el area chat

```
/textbox area chat true|false
```

Activa o desactiva **tu** area chat (solo operador; afecta únicamente a quien lo ejecuta).

---

## ⌨️ Teclas

| Tecla | Acción |
|---|---|
| `V` | Saltar la animación del texto. |
| `B` | Abrir la configuración de tu textbox de área. |

Ambas se pueden cambiar en **Opciones → Controles → CATBox**.

---

## ⚙️ Configuración

Cada jugador personaliza su textbox de área desde la interfaz (tecla `B`):

`Nombre` · `Color` · `Sonido` · `Icono` · `Textura de caja` · `Posición` · `Bloqueante` · `Radio` · `Tiempo` · `Tamaño` · `Area`

La configuración se guarda en `config/textbox-area.json`.

---

## 🔐 Permisos

Nodo de permiso: **`textbox.area`** (por defecto: operadores, nivel 2).

```
/lp group <grupo> permission set textbox.area true
```

---

## 🎨 Colores

Puedes usar colores en el hablante y en el texto:

- **Hex**: `&#RRGGBB` — por ejemplo `&#FFAA00`.
- **Códigos de Minecraft**: `&c` (rojo), `&a` (verde), etc.

El color del borde se toma del color del hablante.

---

## 📥 Instalación

1. Instala **NeoForge** para **Minecraft 1.21.1**.
2. Copia el archivo `.jar` en la carpeta `mods/` (cliente y servidor).
3. (Opcional) Para permisos, instala **LuckPerms 5.4.150 o superior**.

---

## 🔗 Enlaces

- [Repositorio en GitHub](https://github.com/EsparragoCAT/CATBox)
- [Manual completo](https://github.com/EsparragoCAT/CATBox/blob/main/docs/MANUAL.md)

---

*Una firma CAT.*
