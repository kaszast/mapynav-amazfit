import os
from PIL import Image, ImageDraw, ImageFont

# Set up output directories
dirs = [
    'store_assets/screenshots/en',
    'store_assets/screenshots/hu',
    'store_assets/screenshots',
    'store_assets/screenshots_360'
]
for d in dirs:
    os.makedirs(d, exist_ok=True)

font_dist = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 50)
font_street = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 22)
font_clock = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 44)

w, h = 360, 360
ss = 4
ss_mask = Image.new('L', (w * ss, h * ss), 0)
draw_m = ImageDraw.Draw(ss_mask)
draw_m.ellipse((0, 0, w * ss, h * ss), fill=255)
mask = ss_mask.resize((w, h), Image.Resampling.LANCZOS)

def render_watch_screen(icon_path, dist, street, clock):
    img = Image.new('RGBA', (w, h), (10, 14, 20, 255))
    draw = ImageDraw.Draw(img)

    # 1. Turn Icon
    if os.path.exists(icon_path):
        icon_img = Image.open(icon_path).convert('RGBA')
        icon_img = icon_img.resize((158, 158), Image.Resampling.LANCZOS)
        img.paste(icon_img, (101, 8), icon_img)

    # 2. Distance text (White)
    bbox_d = draw.textbbox((0, 0), dist, font=font_dist)
    dw = bbox_d[2] - bbox_d[0]
    draw.text(((w - dw) // 2, 166), dist, font=font_dist, fill=(255, 255, 255))

    # 3. Street text (Amber / Yellow)
    bbox_s = draw.textbbox((0, 0), street, font=font_street)
    sw = bbox_s[2] - bbox_s[0]
    draw.text(((w - sw) // 2, 228), street, font=font_street, fill=(250, 204, 21))

    # 4. Clock text (White)
    bbox_c = draw.textbbox((0, 0), clock, font=font_clock)
    cw = bbox_c[2] - bbox_c[0]
    draw.text(((w - cw) // 2, 280), clock, font=font_clock, fill=(255, 255, 255))

    # 5. Apply circular mask
    img.putalpha(mask)
    return img

# English screens
en_screens = [
    {
        'num': 1,
        'icon': 'zeppos/assets/round.r/turn_left.png',
        'dist': '150 m',
        'street': 'Main Street',
        'clock': '14:25',
        'desc': 'Turn Left'
    },
    {
        'num': 2,
        'icon': 'zeppos/assets/round.r/roundabout.png',
        'dist': '250 m',
        'street': '2nd Exit: High St',
        'clock': '14:28',
        'desc': 'Roundabout'
    },
    {
        'num': 3,
        'icon': 'zeppos/assets/round.r/turn_right.png',
        'dist': '400 m',
        'street': 'Oak Avenue',
        'clock': '14:32',
        'desc': 'Turn Right'
    },
    {
        'num': 4,
        'icon': 'zeppos/assets/round.r/destination.png',
        'dist': '50 m',
        'street': 'Destination Ahead',
        'clock': '14:35',
        'desc': 'Destination'
    }
]

# Hungarian screens
hu_screens = [
    {
        'num': 1,
        'icon': 'zeppos/assets/round.r/turn_left.png',
        'dist': '150 m',
        'street': 'Kossuth Lajos utca',
        'clock': '14:25',
        'desc': 'Balra kanyarodás'
    },
    {
        'num': 2,
        'icon': 'zeppos/assets/round.r/roundabout.png',
        'dist': '250 m',
        'street': '2. kijárat: Fő út',
        'clock': '14:28',
        'desc': 'Körforgalom'
    },
    {
        'num': 3,
        'icon': 'zeppos/assets/round.r/turn_right.png',
        'dist': '400 m',
        'street': 'Petőfi Sándor utca',
        'clock': '14:32',
        'desc': 'Jobbra kanyarodás'
    },
    {
        'num': 4,
        'icon': 'zeppos/assets/round.r/destination.png',
        'dist': '50 m',
        'street': 'Célállomás elérve',
        'clock': '14:35',
        'desc': 'Megérkezés'
    }
]

print("--- Generating English Screenshots ---")
for s in en_screens:
    im = render_watch_screen(s['icon'], s['dist'], s['street'], s['clock'])
    path_num = f"store_assets/screenshots/en/screenshot_{s['num']}.png"
    im.save(path_num, 'PNG')
    print(f"Saved {path_num}: size={im.size}, mode={im.mode}, corner_alpha={im.getpixel((0,0))[3]}")

print("--- Generating Hungarian Screenshots ---")
for s in hu_screens:
    im = render_watch_screen(s['icon'], s['dist'], s['street'], s['clock'])
    path_num = f"store_assets/screenshots/hu/screenshot_{s['num']}.png"
    im.save(path_num, 'PNG')
    print(f"Saved {path_num}: size={im.size}, mode={im.mode}, corner_alpha={im.getpixel((0,0))[3]}")

# Also populate default root and screenshots_360 with descriptive names
names = [
    ('screenshot_1_turn_left.png', en_screens[0]),
    ('screenshot_2_roundabout.png', en_screens[1]),
    ('screenshot_3_turn_right.png', en_screens[2]),
    ('screenshot_4_destination.png', en_screens[3]),
]
for fname, s in names:
    im = render_watch_screen(s['icon'], s['dist'], s['street'], s['clock'])
    p1 = f"store_assets/screenshots/{fname}"
    p2 = f"store_assets/screenshots_360/{fname}"
    im.save(p1, 'PNG')
    im.save(p2, 'PNG')
