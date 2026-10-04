import os
from PIL import Image, ImageDraw

assets_dir = "/home/maci/androidstudio projects/MapyNav-Amazfit/zeppos/assets"
os.makedirs(assets_dir, exist_ok=True)

size = (120, 120)
green = (34, 197, 94, 255)
yellow = (234, 179, 8, 255)
white = (248, 250, 252, 255)

def create_base():
    return Image.new("RGBA", size, (0, 0, 0, 0))

# 1. Straight arrow
img = create_base()
d = ImageDraw.Draw(img)
d.polygon([(60, 15), (25, 55), (45, 55), (45, 105), (75, 105), (75, 55), (95, 55)], fill=green)
img.save(os.path.join(assets_dir, "straight.png"))

# 2. Turn Right
img = create_base()
d = ImageDraw.Draw(img)
# Stem going up then turning right
d.polygon([(35, 105), (65, 105), (65, 65), (75, 65), (75, 80), (105, 50), (75, 20), (75, 35), (35, 35)], fill=green)
img.save(os.path.join(assets_dir, "turn_right.png"))

# 3. Turn Left (horizontal flip of turn right)
img_left = img.transpose(Image.FLIP_LEFT_RIGHT)
img_left.save(os.path.join(assets_dir, "turn_left.png"))

# 4. Slight Right
img = create_base()
d = ImageDraw.Draw(img)
d.polygon([(40, 105), (70, 105), (70, 70), (85, 55), (80, 40), (110, 30), (100, 60), (85, 55), (55, 85), (40, 105)], fill=green)
img.save(os.path.join(assets_dir, "slight_right.png"))

# 5. Slight Left
img_sleft = img.transpose(Image.FLIP_LEFT_RIGHT)
img_sleft.save(os.path.join(assets_dir, "slight_left.png"))

# 6. Sharp Right
img = create_base()
d = ImageDraw.Draw(img)
d.polygon([(35, 105), (60, 105), (60, 60), (85, 35), (75, 25), (110, 20), (105, 55), (95, 45), (45, 95)], fill=green)
img.save(os.path.join(assets_dir, "sharp_right.png"))

# 7. Sharp Left
img_shleft = img.transpose(Image.FLIP_LEFT_RIGHT)
img_shleft.save(os.path.join(assets_dir, "sharp_left.png"))

# 8. U-Turn
img = create_base()
d = ImageDraw.Draw(img)
d.arc([25, 20, 95, 90], start=180, end=0, fill=green, width=22)
d.rectangle([73, 50, 95, 105], fill=green)
d.polygon([(25, 105), (5, 75), (45, 75)], fill=green)
d.rectangle([25, 50, 47, 80], fill=green)
img.save(os.path.join(assets_dir, "u_turn.png"))

# 9. Roundabout
img = create_base()
d = ImageDraw.Draw(img)
d.arc([20, 20, 100, 100], start=45, end=315, fill=green, width=16)
d.polygon([(90, 80), (115, 60), (110, 95)], fill=green)
d.ellipse([50, 50, 70, 70], fill=green)
img.save(os.path.join(assets_dir, "roundabout.png"))

# 10. Destination reached
img = create_base()
d = ImageDraw.Draw(img)
d.ellipse([40, 15, 80, 55], fill=yellow)
d.polygon([(40, 45), (80, 45), (60, 105)], fill=yellow)
d.ellipse([52, 27, 68, 43], fill=(0, 0, 0, 255))
img.save(os.path.join(assets_dir, "destination.png"))

# 11. Unknown / Compass
img = create_base()
d = ImageDraw.Draw(img)
d.ellipse([20, 20, 100, 100], outline=white, width=6)
d.polygon([(60, 25), (75, 60), (60, 50), (45, 60)], fill=green)
d.polygon([(60, 95), (75, 60), (60, 50), (45, 60)], fill=white)
img.save(os.path.join(assets_dir, "unknown.png"))

# 12. App Icon (100x100)
icon_img = Image.new("RGBA", (100, 100), (15, 23, 42, 255))
d_icon = ImageDraw.Draw(icon_img)
d_icon.ellipse([10, 10, 90, 90], outline=green, width=4)
d_icon.polygon([(50, 18), (68, 55), (50, 45), (32, 55)], fill=green)
d_icon.polygon([(50, 82), (68, 55), (50, 45), (32, 55)], fill=white)
icon_img.save(os.path.join(assets_dir, "icon.png"))

print("All assets generated successfully in", assets_dir)
