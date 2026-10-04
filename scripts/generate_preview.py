import os
from PIL import Image, ImageDraw, ImageFont

font_title = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 24)
font_sub = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 16)
font_badge = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 13)

candidates = [
    ('1. Sport Chrono', 'Kardmutatók & percosztás', 'candidate_1_sport_bezel.png'),
    ('2. Minimal AMOLED', 'Neon mutatók & glória', 'candidate_2_minimal_amoled.png'),
    ('3. Outdoor Rugged', 'Taktikai skeleton mutatók', 'candidate_3_outdoor_rugged.png'),
    ('4. Heritage Diver', 'Búvármutatók & zöld lünetta', 'candidate_4_brand_ring.png'),
    ('5. HUD Instrument', 'Műszermutatók & radar HUD', 'candidate_5_hud_instrument.png')
]

w_card = 340
h_card = 460
total_w = len(candidates) * w_card + (len(candidates) + 1) * 20
total_h = h_card + 40

canvas = Image.new('RGB', (total_w, total_h), (15, 23, 42))
draw = ImageDraw.Draw(canvas)

for idx, (title, sub, fname) in enumerate(candidates):
    x_card = 20 + idx * (w_card + 20)
    y_card = 20
    
    # Card background
    draw.rounded_rectangle((x_card, y_card, x_card + w_card, y_card + h_card), radius=16, fill=(30, 41, 59), outline=(51, 65, 85), width=2)
    
    # Icon image (260x260)
    icon_path = os.path.join('store_assets/icon_candidates', fname)
    if os.path.exists(icon_path):
        icon = Image.open(icon_path).convert('RGBA').resize((260, 260), Image.Resampling.LANCZOS)
        canvas.paste(icon, (x_card + (w_card - 260) // 2, y_card + 25), icon)
        
    # Title
    b_t = draw.textbbox((0, 0), title, font=font_title)
    draw.text((x_card + (w_card - (b_t[2] - b_t[0])) // 2, y_card + 315), title, font=font_title, fill=(255, 255, 255))
    
    # Subtitle
    b_s = draw.textbbox((0, 0), sub, font=font_sub)
    draw.text((x_card + (w_card - (b_s[2] - b_s[0])) // 2, y_card + 355), sub, font=font_sub, fill=(50, 208, 17))
    
    # Selection badge at bottom
    badge_txt = f"{idx + 1}. VÁLTOZAT"
    b_b = draw.textbbox((0, 0), badge_txt, font=font_badge)
    bw = (b_b[2] - b_b[0]) + 24
    bh = 32
    bx = x_card + (w_card - bw) // 2
    by = y_card + 400
    draw.rounded_rectangle((bx, by, bx + bw, by + bh), radius=8, fill=(15, 23, 42), outline=(50, 208, 17), width=1)
    draw.text((bx + 12, by + 8), badge_txt, font=font_badge, fill=(240, 240, 240))

out_path = 'store_assets/icon_candidates/preview_all_5.png'
canvas.save(out_path)
print(f"Generated composite preview: {out_path} ({total_w}, {total_h})")
