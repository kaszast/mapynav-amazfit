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

size = (220, 220)
green = (74, 222, 128, 255)  # #4ADE80 Tactical Green
white = (248, 250, 252, 255)
yellow = (250, 204, 21, 255) # #FACC15
stroke_w = 16

def create_base():
    return Image.new("RGBA", size, (0, 0, 0, 0))

def save_all(img, filename):
    for d in dirs:
        img.save(os.path.join(d, filename))

# 1. Turn Right (Tactical Outline)
img = create_base()
d = ImageDraw.Draw(img)
# Stem coming from bottom up to turn
d.line([(65, 190), (65, 95)], fill=green, width=stroke_w)
# Corner turning right
d.line([(57, 95), (145, 95)], fill=green, width=stroke_w)
# Chevron head
d.line([(125, 55), (175, 95)], fill=green, width=stroke_w)
d.line([(175, 95), (125, 135)], fill=green, width=stroke_w)
# Tactical tick marks
d.line([(35, 95), (48, 95)], fill=white, width=4)
d.line([(65, 125), (65, 140)], fill=white, width=4)
save_all(img, "turn_right.png")

# 2. Turn Left (Flip of Turn Right)
img_left = img.transpose(Image.FLIP_LEFT_RIGHT)
save_all(img_left, "turn_left.png")

# 3. Straight (Tactical Arrow)
img = create_base()
d = ImageDraw.Draw(img)
# Main vertical stem
d.line([(110, 195), (110, 70)], fill=green, width=stroke_w)
# Chevron head
d.line([(70, 95), (110, 35)], fill=green, width=stroke_w)
d.line([(110, 35), (150, 95)], fill=green, width=stroke_w)
# Tactical horizontal dashes
d.line([(85, 145), (135, 145)], fill=white, width=4)
d.line([(95, 165), (125, 165)], fill=white, width=4)
save_all(img, "straight.png")

# 4. Slight Right
img = create_base()
d = ImageDraw.Draw(img)
d.line([(80, 195), (80, 125)], fill=green, width=stroke_w)
d.line([(74, 125), (145, 65)], fill=green, width=stroke_w)
d.line([(120, 40), (168, 55)], fill=green, width=stroke_w)
d.line([(168, 55), (155, 105)], fill=green, width=stroke_w)
d.line([(55, 125), (68, 125)], fill=white, width=4)
save_all(img, "slight_right.png")

# 5. Slight Left
img_sleft = img.transpose(Image.FLIP_LEFT_RIGHT)
save_all(img_sleft, "slight_left.png")

# 6. Sharp Right
img = create_base()
d = ImageDraw.Draw(img)
d.line([(70, 195), (70, 105)], fill=green, width=stroke_w)
d.line([(64, 105), (145, 45)], fill=green, width=stroke_w)
d.line([(100, 35), (160, 35)], fill=green, width=stroke_w)
d.line([(160, 35), (160, 95)], fill=green, width=stroke_w)
d.line([(45, 105), (58, 105)], fill=white, width=4)
save_all(img, "sharp_right.png")

# 7. Sharp Left
img_shleft = img.transpose(Image.FLIP_LEFT_RIGHT)
save_all(img_shleft, "sharp_left.png")

# 8. U-Turn
img = create_base()
d = ImageDraw.Draw(img)
d.arc([55, 35, 165, 145], start=180, end=0, fill=green, width=stroke_w)
d.line([(157, 90), (157, 190)], fill=green, width=stroke_w)
d.line([(63, 90), (63, 165)], fill=green, width=stroke_w)
d.line([(35, 145), (63, 185)], fill=green, width=stroke_w)
d.line([(63, 185), (91, 145)], fill=green, width=stroke_w)
d.line([(157, 130), (175, 130)], fill=white, width=4)
save_all(img, "u_turn.png")

# 9. Roundabout
img = create_base()
d = ImageDraw.Draw(img)
# Circular dotted radar ring
d.arc([45, 45, 175, 175], start=45, end=315, fill=green, width=stroke_w)
# Outward exit arrow
d.line([(155, 65), (195, 65)], fill=green, width=stroke_w)
d.line([(175, 45), (195, 65)], fill=green, width=stroke_w)
d.line([(175, 85), (195, 65)], fill=green, width=stroke_w)
# Center target cross
d.line([(100, 110), (120, 110)], fill=white, width=4)
d.line([(110, 100), (110, 120)], fill=white, width=4)
save_all(img, "roundabout.png")

# 10. Destination Reached
img = create_base()
d = ImageDraw.Draw(img)
# Target Reticle
d.ellipse([50, 50, 170, 170], outline=yellow, width=stroke_w)
d.ellipse([85, 85, 135, 135], outline=white, width=6)
d.ellipse([102, 102, 118, 118], fill=yellow)
# Crosshairs
d.line([(25, 110), (45, 110)], fill=yellow, width=6)
d.line([(175, 110), (195, 110)], fill=yellow, width=6)
d.line([(110, 25), (110, 45)], fill=yellow, width=6)
d.line([(110, 175), (110, 195)], fill=yellow, width=6)
save_all(img, "destination.png")

# 11. Unknown / Standby
img = create_base()
d = ImageDraw.Draw(img)
d.ellipse([45, 45, 175, 175], outline=(100, 116, 139), width=8)
d.line([(110, 60), (110, 160)], fill=(100, 116, 139), width=6)
d.line([(60, 110), (160, 110)], fill=(100, 116, 139), width=6)
d.line([(85, 85), (135, 85)], fill=green, width=6)
d.line([(110, 85), (110, 110)], fill=green, width=6)
save_all(img, "unknown.png")

# 12. App Icon (256x256 high-res icon)
icon_img = Image.new("RGBA", (256, 256), (15, 23, 42, 255))
d_icon = ImageDraw.Draw(icon_img)
d_icon.ellipse([20, 20, 236, 236], outline=green, width=12)
# Tactical Compass needle
d_icon.polygon([(128, 40), (165, 128), (128, 108), (91, 128)], fill=green)
d_icon.polygon([(128, 216), (165, 128), (128, 108), (91, 128)], fill=white)
save_all(icon_img, "icon.png")

print("All 220x220 Tactical HUD icons generated across assets folders.")
