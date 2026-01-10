from PIL import Image
import sys

# Charger l'image
img = Image.open(sys.argv[1]).convert("RGBA")  # RGBA pour gérer l'alpha

# Couleurs à remplacer (R, G, B)
old_colors = [(87, 62, 42), (66, 44, 31)]  # bleu
new_colors = [(226, 188, 116), (199, 156, 75)]  # rouge

pixels = img.load()
width, height = img.size

for i in range(len(old_colors)):
    old_color = old_colors[i]
    new_color = new_colors[i]
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            tolerance = 8
            if (
                abs(r - old_color[0]) < tolerance
                and abs(g - old_color[1]) < tolerance
                and abs(b - old_color[2]) < tolerance
            ):
                pixels[x, y] = (new_color[0], new_color[1], new_color[2], a)

# Sauvegarder
img.save(sys.argv[2])
print("Done!")
