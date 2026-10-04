# CATBox — Retro Dialogue Framework

Mod para NeoForge 1.21.1 que permite mostrar diálogos de texto animados (estilo retro) en el juego. Hecho por **CAT**.

## Funciones

- **Diálogos por comando**: muestra una caja de diálogo con hablante, texto animado letra a letra, sonido, icono y color.
- **Area chat**: con `/textbox area chat true`, TUS mensajes del chat se convierten en textbox para los jugadores cercanos (radio configurable). Solo afecta a quien ejecuta el comando.
- **Configuración personal**: cada jugador personaliza el visual de su textbox de área (nombre, color, sonido, icono, posición, tiempo y radio).
- **Iconos flexibles**: ítem de Minecraft, cabeza de jugador (`@nombre`) o imagen por URL.
- **Teclas configurables** desde el menú *Controles*.

## Comandos

```
/textbox <jugadores> <hablante> <texto> <sonido> <icono> <posición> <segundos> <bloqueante> [tamaño]
```

Ejemplo:

```
/textbox @a Steve Hola minecraft:block.note_block.hat @Steve bottom 3 false
```

```
/textbox area chat true|false
```

Activa o desactiva TU area chat (solo operador; afecta únicamente a quien lo ejecuta).

## Teclas

- `V` — saltar la animación del texto.
- `B` — abrir la configuración de tu textbox de área.

## Colores

En el hablante o el texto se pueden usar códigos de color:

- `&#RRGGBB` (hexadecimal), por ejemplo `&#FFAA00`.
- Códigos de Minecraft como `&c` (rojo), `&a` (verde), etc.

## Configuración

La configuración personal se guarda en `config/textbox-area.json`.
