from PIL import Image, ImageDraw

SIZE = 64
SLICE = 8  # grosor del borde (esquinas de 8x8)

GREEN = (34, 197, 94, 255)          # 22C55E (marca)
GREEN_SOFT = (74, 222, 128, 255)    # 4ADE80 (bordes)
CENTER = (13, 13, 20, 235)          # fondo oscuro semitransparente
GUIDE = (255, 255, 255, 160)        # guías

img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
d = ImageDraw.Draw(img)

def rect(x0, y0, x1, y1, color):
    d.rectangle([x0, y0, x1, y1], fill=color)

# Esquinas (8x8)
rect(0, 0, SLICE - 1, SLICE - 1, GREEN)
rect(SIZE - SLICE, 0, SIZE - 1, SLICE - 1, GREEN)
rect(0, SIZE - SLICE, SLICE - 1, SIZE - 1, GREEN)
rect(SIZE - SLICE, SIZE - SLICE, SIZE - 1, SIZE - 1, GREEN)

# Bordes (se estiran)
rect(SLICE, 0, SIZE - SLICE - 1, SLICE - 1, GREEN_SOFT)                    # arriba
rect(SLICE, SIZE - SLICE, SIZE - SLICE - 1, SIZE - 1, GREEN_SOFT)          # abajo
rect(0, SLICE, SLICE - 1, SIZE - SLICE - 1, GREEN_SOFT)                    # izquierda
rect(SIZE - SLICE, SLICE, SIZE - 1, SIZE - SLICE - 1, GREEN_SOFT)          # derecha

# Centro (se estira)
rect(SLICE, SLICE, SIZE - SLICE - 1, SIZE - SLICE - 1, CENTER)

# Guías (límite del borde)
for x in (SLICE - 0.5, SIZE - SLICE - 0.5):
    d.line([(x, 0), (x, SIZE - 1)], fill=GUIDE, width=1)
for y in (SLICE - 0.5, SIZE - SLICE - 0.5):
    d.line([(0, y), (SIZE - 1, y)], fill=GUIDE, width=1)

out = r"C:\Users\USUARIO\Desktop\textbox\docs\box_texture_template.png"
img.save(out)
print("Guardado:", out)
