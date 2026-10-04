# Plantilla de textura de caja (CATBox)

La textura de caja usa **9-slice** para que las esquinas y bordes se mantengan nítidos
al estirar la caja a cualquier ancho/alto.

## Especificación

- **Tamaño**: `64 x 64` píxeles (obligatorio).
- **Grosor del borde (slice)**: `8` píxeles.

### Regiones

| Región | Posición | Tamaño | Se estira |
|---|---|---|---|
| Esquina superior-izquierda | (0, 0) | 8×8 | No |
| Esquina superior-derecha | (56, 0) | 8×8 | No |
| Esquina inferior-izquierda | (0, 56) | 8×8 | No |
| Esquina inferior-derecha | (56, 56) | 8×8 | No |
| Borde superior | (8, 0) | 48×8 | Horizontal |
| Borde inferior | (8, 56) | 48×8 | Horizontal |
| Borde izquierdo | (0, 8) | 8×48 | Vertical |
| Borde derecho | (56, 8) | 8×48 | Vertical |
| Centro (fondo) | (8, 8) | 48×48 | Ambos ejes |

### Qué poner en cada zona
- **Esquinas (8×8)**: el marco de las esquinas, con su forma/borde.
- **Bordes**: el marco continuo (se estira horizontal o verticalmente). Deben ser un
  color/textura que aguante estirarse sin verse mal.
- **Centro**: el fondo de la caja (detrás del texto). Se estira en ambos ejes.

### Formato
- PNG (idealmente con transparencia si quieres el fondo que se vea detrás).
- La textura se carga por **URL** (igual que el icono), p. ej. una imagen subida a ibb.co.

### Archivo de referencia
- `docs/box_texture_template.png` — plantilla con colores de guía (verde = marco,
  verde claro = bordes, oscuro = centro) y líneas que marcan el límite de 8px.

> El verde de la plantilla es solo de guía; puedes usar el color que quieras.
