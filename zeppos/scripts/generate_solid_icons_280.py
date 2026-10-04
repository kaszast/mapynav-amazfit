import os
import subprocess
from PIL import Image

assets_base = "/home/maci/androidstudio projects/MapyNav-Amazfit/zeppos/assets"
dirs = [
    assets_base,
    os.path.join(assets_base, "monaco"),
    os.path.join(assets_base, "lyon")
]
for d in dirs:
    os.makedirs(d, exist_ok=True)

def render_svg_to_file(svg_str, filename, size=210):
    tmp_path = f"/tmp/gen_{filename}"
    cmd = [
        'convert',
        '-background', 'none',
        '-density', '200',
        '-resize', f'{size}x{size}',
        'svg:-',
        f'png32:{tmp_path}'
    ]
    p = subprocess.Popen(cmd, stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = p.communicate(svg_str.encode('utf-8'))
    if p.returncode != 0:
        raise RuntimeError(f"Error rendering {filename}: {stderr.decode('utf-8')}")

    img = Image.open(tmp_path)
    for d in dirs:
        img.save(os.path.join(d, filename))

def flip_and_save(src_filename, dst_filename):
    src_path = os.path.join(assets_base, src_filename)
    img = Image.open(src_path).transpose(Image.FLIP_LEFT_RIGHT)
    for d in dirs:
        img.save(os.path.join(d, dst_filename))

# 1. Straight (210x210)
svg_straight = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 100 180 V 70" fill="none" stroke="#22c55e" stroke-width="36" stroke-linecap="round"/>
  <polygon points="100,18 45,80 155,80" fill="#22c55e"/>
</svg>'''
render_svg_to_file(svg_straight, "straight.png")

# 2. Turn Right (210x210)
svg_turn_right = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 65 180 V 105 Q 65 70 100 70 H 125" fill="none" stroke="#22c55e" stroke-width="36" stroke-linecap="round" stroke-linejoin="round"/>
  <polygon points="182,70 120,15 120,125" fill="#22c55e"/>
</svg>'''
render_svg_to_file(svg_turn_right, "turn_right.png")

# 3. Turn Left (Flip)
flip_and_save("turn_right.png", "turn_left.png")

# 4. Slight Right (Bear Right, 210x210)
svg_slight_right = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 75 180 V 125 Q 75 80 125 45" fill="none" stroke="#22c55e" stroke-width="36" stroke-linecap="round" stroke-linejoin="round"/>
  <polygon points="180,30 115,15 145,85" fill="#22c55e"/>
</svg>'''
render_svg_to_file(svg_slight_right, "slight_right.png")

# 5. Slight Left (Flip)
flip_and_save("slight_right.png", "slight_left.png")

# 6. Sharp Right (Acute Angle, NOT U-shape, 210x210)
svg_sharp_right = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 60 180 L 60 70 L 130 125" fill="none" stroke="#22c55e" stroke-width="34" stroke-linecap="round" stroke-linejoin="miter"/>
  <polygon points="175,155 110,140 145,85" fill="#22c55e"/>
</svg>'''
render_svg_to_file(svg_sharp_right, "sharp_right.png")

# 7. Sharp Left (Flip)
flip_and_save("sharp_right.png", "sharp_left.png")

# 8. U-Turn (Massive unbroken loop, 210x210)
svg_u_turn = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 145 180 V 95 Q 145 35 102 35 Q 60 35 60 95 L 60 115" fill="none" stroke="#22c55e" stroke-width="34" stroke-linecap="round" stroke-linejoin="round"/>
  <polygon points="60,175 20,110 100,110" fill="#22c55e"/>
</svg>'''
render_svg_to_file(svg_u_turn, "u_turn.png")

# 9. Open Roundabout (Non-closed ring, 210x210)
svg_roundabout = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 100 170 A 62 62 0 1 1 125 46" fill="none" stroke="#22c55e" stroke-width="32" stroke-linecap="round"/>
  <polygon points="180,52 115,14 122,90" fill="#22c55e"/>
</svg>'''
render_svg_to_file(svg_roundabout, "roundabout.png")

# 10. Destination (210x210)
svg_destination = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <path d="M 100 20 C 65 20 40 48 40 85 C 40 130 100 185 100 185 C 100 185 160 130 160 85 C 160 48 135 20 100 20 Z" fill="#facc15"/>
  <circle cx="100" cy="80" r="28" fill="#000000"/>
  <circle cx="100" cy="80" r="14" fill="#facc15"/>
</svg>'''
render_svg_to_file(svg_destination, "destination.png")

# 11. Unknown / Standby (210x210)
svg_unknown = '''<svg width="200" height="200" viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
  <circle cx="100" cy="100" r="78" fill="none" stroke="#ffffff" stroke-width="12"/>
  <polygon points="100,32 135,100 100,82 65,100" fill="#22c55e"/>
  <polygon points="100,168 135,100 100,82 65,100" fill="#ffffff"/>
</svg>'''
render_svg_to_file(svg_unknown, "unknown.png")

# 12. App Icon (256x256)
svg_app_icon = '''<svg width="256" height="256" viewBox="0 0 256 256" xmlns="http://www.w3.org/2000/svg">
  <rect width="256" height="256" rx="60" fill="#0f172a"/>
  <circle cx="128" cy="128" r="95" fill="none" stroke="#22c55e" stroke-width="14"/>
  <polygon points="128,45 170,128 128,110 86,128" fill="#22c55e"/>
  <polygon points="128,211 170,128 128,110 86,128" fill="#ffffff"/>
</svg>'''
render_svg_to_file(svg_app_icon, "icon.png", size=256)

print("All 12 vector icons successfully rendered and saved.")
