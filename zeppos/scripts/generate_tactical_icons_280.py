import os
from PIL import Image, ImageDraw

assets_base = "/home/maci/androidstudio projects/MapyNav-Amazfit/zeppos/assets"
dirs = [
    assets_base,
    os.path.join(assets_base, "monaco"),
    os.path.join(assets_base, "lyon")
]
for d in dirs:
    os.makedirs(d, exist_ok=True)

size = (280, 280)
green = (74, 222, 128, 255)  # #4ADE80 Tactical Green
white = (248, 250, 252, 255)
yellow = (250, 204, 21, 255) # #FACC15
stroke_w = 24  # Massive, thick 24px strokes

def create_base():
    return Image.new("RGBA", size, (0, 0, 0, 0))

def save_all(img, filename):
    for d in dirs:
        img.save(os.path.join(d, filename))

# 1. Turn Right (Giant Tactical Outline)
img = create_base()
d = ImageDraw.Draw(img)
# Stem coming from bottom up to turn
d.line([(80, 250), (80, 120)], fill=green, width=stroke_w)
# Corner turning right
d.line([(70, 120), (190, 120)], fill=green, width=stroke_w)
# Massive chevron head
d.line([(160, 70), (235, 120)], fill=green, width=stroke_w)
d.line([(235, 120), (160, 170)], fill=green, width=stroke_w)
# Tactical tick marks
d.line([(40, 120), (58, 120)], fill=white, width=6)
d.line([(80, 160), (80, 185)], fill=white, width=6)
save_all(img, "turn_right.png")

# 2. Turn Left (Flip of Turn Right)
img_left = img.transpose(Image.FLIP_LEFT_RIGHT)
save_all(img_left, "turn_left.png")

# 3. Straight (Giant Tactical Arrow)
img = create_base()
d = ImageDraw.Draw(img)
# Main vertical stem
d.line([(140, 255), (140, 85)], fill=green, width=stroke_w)
# Chevron head
d.line([(90, 120), (140, 45)], fill=green, width=stroke_w)
d.line([(140, 45), (190, 120)], fill=green, width=stroke_w)
# Tactical horizontal dashes
d.line([(105, 185), (175, 185)], fill=white, width=6)
d.line([(115, 215), (165, 215)], fill=white, width=6)
save_all(img, "straight.png")

# 4. Slight Right
img = create_base()
d = ImageDraw.Draw(img)
d.line([(100, 255), (100, 155)], fill=green, width=stroke_w)
d.line([(90, 155), (185, 75)], fill=green, width=stroke_w)
d.line([(150, 45), (215, 65)], fill=green, width=stroke_w)
d.line([(215, 65), (200, 130)], fill=green, width=stroke_w)
d.line([(65, 155), (85, 155)], fill=white, width=6)
save_all(img, "slight_right.png")

# 5. Slight Left
img_sleft = img.transpose(Image.FLIP_LEFT_RIGHT)
save_all(img_sleft, "slight_left.png")

# 6. Sharp Right
img = create_base()
d = ImageDraw.Draw(img)
d.line([(85, 255), (85, 135)], fill=green, width=stroke_w)
d.line([(75, 135), (190, 55)], fill=green, width=stroke_w)
d.line([(135, 45), (210, 45)], fill=green, width=stroke_w)
d.line([(210, 45), (210, 120)], fill=green, width=stroke_w)
d.line([(55, 135), (70, 135)], fill=white, width=6)
save_all(img, "sharp_right.png")

# 7. Sharp Left
img_shleft = img.transpose(Image.FLIP_LEFT_RIGHT)
save_all(img_shleft, "sharp_left.png")

# 8. U-Turn
img = create_base()
d = ImageDraw.Draw(img)
d.arc([70, 45, 210, 185], start=180, end=0, fill=green, width=stroke_w)
d.line([(200, 115), (200, 245)], fill=green, width=stroke_w)
d.line([(80, 115), (80, 215)], fill=green, width=stroke_w)
d.line([(45, 185), (80, 245)], fill=green, width=stroke_w)
d.line([(80, 245), (115, 185)], fill=green, width=stroke_w)
d.line([(200, 175), (225, 175)], fill=white, width=6)
save_all(img, "u_turn.png")

# 9. Roundabout
img = create_base()
d = ImageDraw.Draw(img)
# Circular radar ring
d.arc([55, 55, 225, 225], start=45, end=315, fill=green, width=stroke_w)
# Outward exit arrow
d.line([(195, 80), (250, 80)], fill=green, width=stroke_w)
d.line([(220, 55), (250, 80)], fill=green, width=stroke_w)
d.line([(220, 105), (250, 80)], fill=green, width=stroke_w)
# Center target cross
d.line([(125, 140), (155, 140)], fill=white, width=6)
d.line([(140, 125), (140, 155)], fill=white, width=6)
save_all(img, "roundabout.png")

# 10. Destination Reached
img = create_base()
d = ImageDraw.Draw(img)
d.ellipse([60, 60, 220, 220], outline=yellow, width=stroke_w)
d.ellipse([105, 105, 175, 175], outline=white, width=8)
d.ellipse([130, 130, 150, 150], fill=yellow)
# Crosshairs
d.line([(30, 140), (55, 140)], fill=yellow, width=8)
d.line([(225, 140), (250, 140)], fill=yellow, width=8)
d.line([(140, 30), (140, 55)], fill=yellow, width=8)
d.line([(140, 225), (140, 250)], fill=yellow, width=8)
save_all(img, "destination.png")

# 11. Unknown / Standby
img = create_base()
d = ImageDraw.Draw(img)
d.ellipse([55, 55, 225, 225], outline=(100, 116, 139), width=10)
d.line([(140, 75), (140, 205)], fill=(100, 116, 139), width=8)
d.line([(75, 140), (205, 140)], fill=(100, 116, 139), width=8)
d.line([(105, 105), (175, 105)], fill=green, width=8)
d.line([(140, 105), (140, 140)], fill=green, width=8)
save_all(img, "unknown.png")

# 12. App Icon (256x256 high-res icon)
icon_img = Image.new("RGBA", (256, 256), (15, 23, 42, 255))
d_icon = ImageDraw.Draw(icon_img)
d_icon.ellipse([20, 20, 236, 236], outline=green, width=14)
d_icon.polygon([(128, 38), (170, 128), (128, 106), (86, 128)], fill=green)
d_icon.polygon([(128, 218), (170, 128), (128, 106), (86, 128)], fill=white)
save_all(icon_img, "icon.png")

print("Generated massive 280x280 icons with 24px stroke width across all folders.")
