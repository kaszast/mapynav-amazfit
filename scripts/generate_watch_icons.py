import os, math
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import numpy as np

os.makedirs('store_assets/icon_candidates', exist_ok=True)

font_bold_28 = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 28)
font_bold_22 = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 22)
font_bold_16 = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 16)
font_bold_14 = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 14)
font_bold_12 = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 12)

mapy_src = Image.open('zeppos/icon.png').convert('RGBA')
cx, cy = 256, 256

# Extract pure white emblem as transparent alpha image
arr = np.array(mapy_src)
white_mask = (arr[:, :, 0] > 230) & (arr[:, :, 1] > 230) & (arr[:, :, 2] > 230)
white_emblem = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
w_arr = np.array(white_emblem)
w_arr[white_mask] = [255, 255, 255, 255]
white_emblem = Image.fromarray(w_arr)

# Standard time 10:10
# In polar clock coordinates (0 rad = 12 o'clock, clockwise):
# Minute hand at 10 minutes: 10 * 6 deg = 60 deg = pi/3
# Hour hand at 10h 10m: 10 * 30 + 10 * 0.5 = 305 deg = -55 deg
ANG_MIN  = math.radians(60)
ANG_HOUR = math.radians(305)

def rotate_pts(pts, ang, ox=cx, oy=cy):
    """Rotate points around (ox, oy) where unrotated 0 rad is pointing towards 12 o'clock (0, -y)"""
    sin_a = math.sin(ang)
    cos_a = math.cos(ang)
    res = []
    for x, y in pts:
        dx = x - ox
        dy = y - oy
        # unrotated vector: dx is right (+X), dy is down (+Y).
        # We define canonical hand pointing straight up (dx=0, dy < 0).
        # When ang = 0, pt stays same.
        rx = dx * cos_a - dy * sin_a
        ry = dx * sin_a + dy * cos_a
        res.append((ox + rx, oy + ry))
    return res

def add_shadow(shadow_img, pts, offset=(4, 6)):
    ox, oy = offset
    s_pts = [(x + ox, y + oy) for x, y in pts]
    d = ImageDraw.Draw(shadow_img)
    d.polygon(s_pts, fill=(0, 0, 0, 200))

# -----------------------------------------------------------------------------
# CANDIDATE 1: Sport Chrono (Faceted Steel Sword Hands with Luminous Infill)
# -----------------------------------------------------------------------------
def gen_v1():
    img = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Outer bezel dark titanium (radius 245)
    draw.ellipse((11, 11, 501, 501), fill=(30, 36, 44), outline=(50, 58, 70), width=4)
    # Inner bezel ring (radius 225)
    draw.ellipse((31, 31, 481, 481), fill=(20, 24, 30), outline=(40, 48, 60), width=3)
    
    # Tick marks around bezel (60 ticks)
    for i in range(60):
        angle = i * (2 * math.pi / 60)
        is_major = (i % 5 == 0)
        r_outer = 240
        r_inner = 226 if is_major else 233
        color = (31, 176, 1, 255) if i == 0 else ((240, 240, 240, 255) if is_major else (120, 130, 145, 200))
        width = 4 if is_major else 2
        x1 = cx + r_inner * math.sin(angle)
        y1 = cy - r_inner * math.cos(angle)
        x2 = cx + r_outer * math.sin(angle)
        y2 = cy - r_outer * math.cos(angle)
        draw.line([(x1, y1), (x2, y2)], fill=color, width=width)
        
    # Bezel numbers at 12, 3, 6, 9
    for val, ang in [(12, 0), (3, math.pi/2), (6, math.pi), (9, 3*math.pi/2)]:
        r_txt = 210
        tx = cx + r_txt * math.sin(ang)
        ty = cy - r_txt * math.cos(ang)
        txt = str(val * 5) if val != 12 else '60'
        b = draw.textbbox((0, 0), txt, font=font_bold_14)
        col = (31, 176, 1) if val == 12 else (230, 235, 240)
        draw.text((tx - (b[2]-b[0])/2, ty - (b[3]-b[1])/2), txt, font=font_bold_14, fill=col)
        
    # Dial background (radius 195)
    draw.ellipse((61, 61, 451, 451), fill=(16, 20, 26), outline=(31, 176, 1, 160), width=2)
    # Dial inner chrono tracks
    draw.ellipse((85, 85, 427, 427), outline=(255, 255, 255, 30), width=1)
    draw.ellipse((120, 120, 392, 392), outline=(31, 176, 1, 50), width=1)
    
    # 12 Luminescent hour markers
    for i in range(12):
        ang = i * (2 * math.pi / 12)
        r1, r2 = 172, 190
        x1 = cx + r1 * math.sin(ang)
        y1 = cy - r1 * math.cos(ang)
        x2 = cx + r2 * math.sin(ang)
        y2 = cy - r2 * math.cos(ang)
        col = (31, 176, 1) if i == 0 else (240, 240, 240)
        draw.line([(x1, y1), (x2, y2)], fill=col, width=5 if i % 3 == 0 else 3)
    
    # Mapy Logo in Center (radius 220, perfectly sized)
    logo_size = 220
    logo_res = mapy_src.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
    mask = Image.new('L', (logo_size, logo_size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, logo_size - 1, logo_size - 1), fill=255)
    img.paste(logo_res, (cx - logo_size//2, cy - logo_size//2), mask)
    draw.ellipse((cx - logo_size//2, cy - logo_size//2, cx + logo_size//2, cy + logo_size//2), outline=(255, 255, 255, 180), width=2)

    # NOW DRAW TWO FACETED SWORD HANDS
    shadow_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    hands_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_hands = ImageDraw.Draw(hands_layer)
    
    def draw_sword_hand(ang, length, width, tail_len=24):
        # Canonical points pointing straight up (along -Y):
        # center is (cx, cy)
        # Left facet: (tail_left -> base_left -> shoulder_left -> tip -> center)
        # Right facet: (tail_right -> base_right -> shoulder_right -> tip -> center)
        hw = width / 2.0
        sh_y = cy - length * 0.85
        tip_y = cy - length
        tail_y = cy + tail_len
        
        pts_left = [(cx, tail_y), (cx - hw*0.5, cy + tail_len*0.3), (cx - hw, cy), (cx - hw, sh_y), (cx, tip_y), (cx, cy)]
        pts_right = [(cx, tail_y), (cx + hw*0.5, cy + tail_len*0.3), (cx + hw, cy), (cx + hw, sh_y), (cx, tip_y), (cx, cy)]
        
        r_left = rotate_pts(pts_left, ang)
        r_right = rotate_pts(pts_right, ang)
        
        # Shadow
        add_shadow(shadow_layer, r_left + r_right[::-1], offset=(4, 6))
        
        # Draw 3D Facets (Left lighter steel, Right darker steel)
        d_hands.polygon(r_left, fill=(245, 248, 252, 255), outline=(180, 190, 205, 255))
        d_hands.polygon(r_right, fill=(195, 202, 212, 255), outline=(150, 160, 175, 255))
        
        # Lume strip in center
        lume_w = width * 0.38
        lume_y1 = cy - length * 0.15
        lume_y2 = cy - length * 0.80
        pts_lume = [(cx - lume_w/2, lume_y1), (cx + lume_w/2, lume_y1), (cx + lume_w/2, lume_y2), (cx - lume_w/2, lume_y2)]
        r_lume = rotate_pts(pts_lume, ang)
        d_hands.polygon(r_lume, fill=(255, 255, 255, 240), outline=(31, 176, 1, 180))

    # Hour hand: length 110, width 18
    draw_sword_hand(ANG_HOUR, length=110, width=18, tail_len=26)
    # Minute hand: length 162, width 14
    draw_sword_hand(ANG_MIN, length=162, width=14, tail_len=30)
    
    # Paste shadows with blur
    shadow_layer = shadow_layer.filter(ImageFilter.GaussianBlur(radius=5))
    img.paste(shadow_layer, (0, 0), shadow_layer)
    img.paste(hands_layer, (0, 0), hands_layer)
    
    # Center Pinion & Cap
    draw.ellipse((cx - 15, cy - 15, cx + 15, cy + 15), fill=(230, 235, 242), outline=(100, 110, 125), width=2)
    draw.ellipse((cx - 10, cy - 10, cx + 10, cy + 10), fill=(31, 176, 1), outline=(255, 255, 255), width=2)
    draw.ellipse((cx - 4, cy - 4, cx + 4, cy + 4), fill=(15, 20, 25))
    
    img.save('store_assets/icon_candidates/candidate_1_sport_bezel.png')
    print('V1 done')

# -----------------------------------------------------------------------------
# CANDIDATE 2: Minimalist AMOLED (Sleek Modern White & Emerald Neon Hands)
# -----------------------------------------------------------------------------
def gen_v2():
    img = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Emerald glow halo
    glow = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_glow = ImageDraw.Draw(glow)
    d_glow.ellipse((14, 14, 498, 498), outline=(31, 176, 1, 130), width=8)
    d_glow.ellipse((18, 18, 494, 494), outline=(50, 208, 17, 220), width=4)
    glow = glow.filter(ImageFilter.GaussianBlur(radius=4))
    img.paste(glow, (0, 0), glow)
    
    # Pure black watch glass (radius 235)
    draw.ellipse((21, 21, 491, 491), fill=(8, 10, 14), outline=(40, 50, 60), width=2)
    
    # 12 minimalist dot markers
    for i in range(12):
        angle = i * (2 * math.pi / 12)
        r_dot = 215
        dx = cx + r_dot * math.sin(angle)
        dy = cy - r_dot * math.cos(angle)
        r = 6 if i % 3 == 0 else 4
        col = (31, 176, 1, 255) if i == 0 else (220, 230, 240, 220)
        draw.ellipse((dx - r, dy - r, dx + r, dy + r), fill=col)
        
    # Concentric circles
    draw.ellipse((80, 80, 432, 432), outline=(31, 176, 1, 60), width=1)
    draw.ellipse((120, 120, 392, 392), outline=(255, 255, 255, 30), width=1)
    
    # Centered Mapy Logo Badge
    logo_size = 230
    logo_res = mapy_src.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
    mask = Image.new('L', (logo_size, logo_size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, logo_size - 1, logo_size - 1), fill=255)
    img.paste(logo_res, (cx - logo_size//2, cy - logo_size//2), mask)
    draw.ellipse((cx - logo_size//2, cy - logo_size//2, cx + logo_size//2, cy + logo_size//2), outline=(255, 255, 255, 200), width=3)
    
    # TWO ULTRA-MODERN SLEEK BATON HANDS
    shadow_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    hands_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_hands = ImageDraw.Draw(hands_layer)
    
    def draw_minimal_hand(ang, length, width, tip_len=28, tail_len=20):
        hw = width / 2.0
        # Main white stem
        stem_y1 = cy + tail_len
        stem_y2 = cy - length + tip_len
        pts_stem = [(cx - hw, stem_y1), (cx + hw, stem_y1), (cx + hw, stem_y2), (cx - hw, stem_y2)]
        
        # Neon green arrow/tip
        tip_y = cy - length
        pts_tip = [(cx - hw, stem_y2), (cx + hw, stem_y2), (cx, tip_y)]
        
        r_stem = rotate_pts(pts_stem, ang)
        r_tip = rotate_pts(pts_tip, ang)
        
        add_shadow(shadow_layer, r_stem + r_tip, offset=(3, 5))
        
        # Draw stem (crisp white with silver edge)
        d_hands.polygon(r_stem, fill=(255, 255, 255, 250), outline=(210, 220, 230, 255))
        # Draw tip (glowing neon Mapy green)
        d_hands.polygon(r_tip, fill=(50, 208, 17, 255), outline=(255, 255, 255, 200))
        
    draw_minimal_hand(ANG_HOUR, length=112, width=12, tip_len=26, tail_len=22)
    draw_minimal_hand(ANG_MIN, length=168, width=8, tip_len=32, tail_len=28)
    
    shadow_layer = shadow_layer.filter(ImageFilter.GaussianBlur(radius=5))
    img.paste(shadow_layer, (0, 0), shadow_layer)
    img.paste(hands_layer, (0, 0), hands_layer)
    
    # Modern center dot
    draw.ellipse((cx - 12, cy - 12, cx + 12, cy + 12), fill=(10, 15, 20), outline=(50, 208, 17), width=3)
    draw.ellipse((cx - 5, cy - 5, cx + 5, cy + 5), fill=(255, 255, 255))
    
    img.save('store_assets/icon_candidates/candidate_2_minimal_amoled.png')
    print('V2 done')

# -----------------------------------------------------------------------------
# CANDIDATE 3: Outdoor Rugged / T-Rex (Tactical Skeleton & Arrow Hands)
# -----------------------------------------------------------------------------
def gen_v3():
    img = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Rugged notched outer bezel (radius 246)
    draw.ellipse((10, 10, 502, 502), fill=(35, 40, 48), outline=(60, 70, 85), width=6)
    
    # 12 rugged grip teeth
    for i in range(12):
        ang = i * (2 * math.pi / 12)
        nx = cx + 242 * math.sin(ang)
        ny = cy - 242 * math.cos(ang)
        draw.ellipse((nx - 10, ny - 10, nx + 10, ny + 10), fill=(20, 24, 30), outline=(50, 60, 70), width=2)
        
    # Compass ring inside bezel
    draw.ellipse((35, 35, 477, 477), fill=(22, 26, 32), outline=(45, 52, 62), width=3)
    
    # Cardinal markers N, E, S, W
    cardinals = [('N', 0, (239, 68, 68)), ('E', math.pi/2, (240, 240, 240)), ('S', math.pi, (240, 240, 240)), ('W', 3*math.pi/2, (240, 240, 240))]
    for letter, ang, col in cardinals:
        rx = cx + 208 * math.sin(ang)
        ry = cy - 208 * math.cos(ang)
        b = draw.textbbox((0, 0), letter, font=font_bold_28)
        draw.text((rx - (b[2]-b[0])/2, ry - (b[3]-b[1])/2), letter, font=font_bold_28, fill=col)
        
    # Dial with topographic contour curves
    dial_box = (76, 76, 436, 436)
    draw.ellipse(dial_box, fill=(14, 18, 22), outline=(31, 176, 1, 150), width=2)
    
    # Topo lines
    topo_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_topo = ImageDraw.Draw(topo_layer)
    for r_topo in [90, 115, 140, 165, 185]:
        d_topo.ellipse((cx - r_topo, cy - r_topo, cx + r_topo, cy + r_topo), outline=(31, 176, 1, 45), width=2)
    mask_dial = Image.new('L', (512, 512), 0)
    ImageDraw.Draw(mask_dial).ellipse(dial_box, fill=255)
    img.paste(topo_layer, (0, 0), mask_dial)
    
    # Center Mapy logo
    logo_size = 220
    logo_res = mapy_src.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
    mask = Image.new('L', (logo_size, logo_size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, logo_size - 1, logo_size - 1), fill=255)
    img.paste(logo_res, (cx - logo_size//2, cy - logo_size//2), mask)
    draw.ellipse((cx - logo_size//2, cy - logo_size//2, cx + logo_size//2, cy + logo_size//2), outline=(255, 255, 255, 220), width=3)
    
    # TWO TACTICAL SKELETON ARROW HANDS
    shadow_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    hands_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_hands = ImageDraw.Draw(hands_layer)
    
    def draw_tactical_hand(ang, length, width, arrow_w, arrow_len, tail_len=26):
        # Outer tactical frame
        hw = width / 2.0
        ar_hw = arrow_w / 2.0
        tip_y = cy - length
        ar_base_y = cy - length + arrow_len
        tail_y = cy + tail_len
        
        # Frame points
        pts_frame = [
            (cx - hw, tail_y), (cx + hw, tail_y),
            (cx + hw, ar_base_y), (cx + ar_hw, ar_base_y),
            (cx, tip_y),
            (cx - ar_hw, ar_base_y), (cx - hw, ar_base_y)
        ]
        
        # Skeleton cutout inside the stem
        sk_w = hw * 0.45
        sk_y1 = cy - 5
        sk_y2 = ar_base_y + 12
        pts_cutout = [(cx - sk_w, sk_y1), (cx + sk_w, sk_y1), (cx + sk_w, sk_y2), (cx - sk_w, sk_y2)]
        
        # Lume arrowhead
        pts_arrow_lume = [(cx - ar_hw*0.75, ar_base_y), (cx + ar_hw*0.75, ar_base_y), (cx, tip_y + 5)]
        
        r_frame = rotate_pts(pts_frame, ang)
        r_cutout = rotate_pts(pts_cutout, ang)
        r_arrow = rotate_pts(pts_arrow_lume, ang)
        
        add_shadow(shadow_layer, r_frame, offset=(5, 7))
        
        # Draw frame (Matte dark tactical gunmetal with white borders)
        d_hands.polygon(r_frame, fill=(35, 42, 50, 255), outline=(240, 245, 250, 255))
        # Arrow lume
        d_hands.polygon(r_arrow, fill=(50, 208, 17, 255), outline=(255, 255, 255, 200))
        # Skeleton cutout (transparent/dark)
        d_hands.polygon(r_cutout, fill=(15, 20, 25, 220), outline=(200, 210, 225, 200))
        
    draw_tactical_hand(ANG_HOUR, length=114, width=18, arrow_w=28, arrow_len=36, tail_len=26)
    draw_tactical_hand(ANG_MIN, length=166, width=13, arrow_w=22, arrow_len=44, tail_len=30)
    
    shadow_layer = shadow_layer.filter(ImageFilter.GaussianBlur(radius=6))
    img.paste(shadow_layer, (0, 0), shadow_layer)
    img.paste(hands_layer, (0, 0), hands_layer)
    
    # Heavy-duty tactical center cap (hex bolt look)
    draw.ellipse((cx - 16, cy - 16, cx + 16, cy + 16), fill=(40, 48, 58), outline=(240, 245, 250), width=2)
    draw.polygon(rotate_pts([(cx-8, cy-12), (cx+8, cy-12), (cx+14, cy), (cx+8, cy+12), (cx-8, cy+12), (cx-14, cy)], 0), fill=(20, 25, 32), outline=(50, 208, 17))
    draw.ellipse((cx - 4, cy - 4, cx + 4, cy + 4), fill=(50, 208, 17))
    
    img.save('store_assets/icon_candidates/candidate_3_outdoor_rugged.png')
    print('V3 done')

# -----------------------------------------------------------------------------
# CANDIDATE 4: Mapy Green Heritage Diver (Classic Luxury Diver Watch Hands)
# -----------------------------------------------------------------------------
def gen_v4():
    img = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Thick vibrant Mapy Green outer bezel (radius 245)
    draw.ellipse((11, 11, 501, 501), fill=(31, 176, 1), outline=(50, 208, 17), width=4)
    
    # Engraved white ticks and dive markers
    for i in range(60):
        ang = i * (2 * math.pi / 60)
        is_maj = (i % 5 == 0)
        r1 = 222 if is_maj else 232
        r2 = 244
        draw.line([(cx + r1*math.sin(ang), cy - r1*math.cos(ang)), (cx + r2*math.sin(ang), cy - r2*math.cos(ang))], fill=(255, 255, 255, 240 if is_maj else 180), width=4 if is_maj else 2)
        
    # Diver triangle pip at 12
    draw.polygon([(cx, 222), (cx - 15, 244), (cx + 15, 244)], fill=(255, 255, 255))
    draw.ellipse((cx - 4, 235 - 4, cx + 4, 235 + 4), fill=(31, 176, 1))
    
    # Inner dark watch face (radius 200)
    draw.ellipse((56, 56, 456, 456), fill=(12, 16, 20), outline=(255, 255, 255, 200), width=3)
    
    # Diver round & baton lume markers
    for i in range(12):
        if i == 0: continue # 12 is top triangle
        ang = i * (2 * math.pi / 12)
        dx = cx + 175 * math.sin(ang)
        dy = cy - 175 * math.cos(ang)
        if i in [3, 6, 9]:
            # Baton
            draw.rectangle((dx - 6, dy - 10, dx + 6, dy + 10), fill=(255, 255, 255), outline=(31, 176, 1), width=2)
        else:
            # Circle
            draw.ellipse((dx - 7, dy - 7, dx + 7, dy + 7), fill=(255, 255, 255), outline=(31, 176, 1), width=2)
    
    # Centered White Mapy Emblem
    emb_size = 220
    emb_res = white_emblem.resize((emb_size, emb_size), Image.Resampling.LANCZOS)
    img.paste(emb_res, (cx - emb_size//2, cy - emb_size//2 + 5), emb_res)
    
    # TWO CLASSIC DIVER HANDS (Rolex/Omega style cathedral/diver hour hand & sword minute hand)
    shadow_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    hands_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_hands = ImageDraw.Draw(hands_layer)
    
    # Diver Hour Hand: stem + large circular eye with 3-spoke Mercedes/peace motif + pointed tip
    def draw_diver_hour(ang, length=110, width=14):
        tip_y = cy - length
        eye_cy = cy - length * 0.70
        eye_r = 17
        tail_y = cy + 24
        
        pts_stem = [(cx - width/2, tail_y), (cx + width/2, tail_y), (cx + width/2, eye_cy + eye_r), (cx - width/2, eye_cy + eye_r)]
        pts_tip = [(cx - width/2, eye_cy - eye_r + 2), (cx + width/2, eye_cy - eye_r + 2), (cx, tip_y)]
        
        r_stem = rotate_pts(pts_stem, ang)
        r_tip = rotate_pts(pts_tip, ang)
        eye_pt = rotate_pts([(cx, eye_cy)], ang)[0]
        
        add_shadow(shadow_layer, r_stem + r_tip, offset=(4, 6))
        # Add shadow for circular eye
        d_s = ImageDraw.Draw(shadow_layer)
        d_s.ellipse((eye_pt[0] - eye_r + 4, eye_pt[1] - eye_r + 6, eye_pt[0] + eye_r + 4, eye_pt[1] + eye_r + 6), fill=(0, 0, 0, 200))
        
        # Draw stem & tip in polished silver
        d_hands.polygon(r_stem, fill=(245, 248, 252), outline=(160, 170, 185))
        d_hands.polygon(r_tip, fill=(245, 248, 252), outline=(160, 170, 185))
        # Draw eye
        d_hands.ellipse((eye_pt[0] - eye_r, eye_pt[1] - eye_r, eye_pt[0] + eye_r, eye_pt[1] + eye_r), fill=(255, 255, 255), outline=(160, 170, 185), width=3)
        # Inner green accent dot
        d_hands.ellipse((eye_pt[0] - 6, eye_pt[1] - 6, eye_pt[0] + 6, eye_pt[1] + 6), fill=(31, 176, 1))

    # Diver Minute Hand: long broad sword hand with pointed tip and large luminous channel
    def draw_diver_minute(ang, length=166, width=15):
        tip_y = cy - length
        tail_y = cy + 28
        hw = width / 2.0
        pts_body = [(cx - hw, tail_y), (cx + hw, tail_y), (cx + hw, tip_y + 16), (cx, tip_y), (cx - hw, tip_y + 16)]
        r_body = rotate_pts(pts_body, ang)
        
        add_shadow(shadow_layer, r_body, offset=(4, 6))
        d_hands.polygon(r_body, fill=(245, 248, 252), outline=(160, 170, 185), width=2)
        
        # Large luminous white slot
        l_w = width * 0.45
        pts_lume = [(cx - l_w, cy - 10), (cx + l_w, cy - 10), (cx + l_w, tip_y + 24), (cx, tip_y + 8), (cx - l_w, tip_y + 24)]
        r_lume = rotate_pts(pts_lume, ang)
        d_hands.polygon(r_lume, fill=(255, 255, 255), outline=(31, 176, 1), width=2)

    draw_diver_hour(ANG_HOUR)
    draw_diver_minute(ANG_MIN)
    
    shadow_layer = shadow_layer.filter(ImageFilter.GaussianBlur(radius=5))
    img.paste(shadow_layer, (0, 0), shadow_layer)
    img.paste(hands_layer, (0, 0), hands_layer)
    
    # Polished steel center cap
    draw.ellipse((cx - 15, cy - 15, cx + 15, cy + 15), fill=(245, 248, 252), outline=(140, 150, 165), width=2)
    draw.ellipse((cx - 6, cy - 6, cx + 6, cy + 6), fill=(15, 20, 25))
    
    img.save('store_assets/icon_candidates/candidate_4_brand_ring.png')
    print('V4 done')

# -----------------------------------------------------------------------------
# CANDIDATE 5: Tactical Navigation HUD / Aviator (Instrument Gauge Needles)
# -----------------------------------------------------------------------------
def gen_v5():
    img = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Dark carbon outer ring
    draw.ellipse((10, 10, 502, 502), fill=(18, 22, 28), outline=(31, 176, 1, 220), width=4)
    
    # 360 degree ticks
    for i in range(36):
        ang = i * (2 * math.pi / 36)
        is_card = (i % 9 == 0)
        r1 = 222 if is_card else 232
        r2 = 244
        col = (50, 208, 17, 255) if is_card else (100, 120, 140, 180)
        w = 4 if is_card else 2
        draw.line([(cx + r1*math.sin(ang), cy - r1*math.cos(ang)), (cx + r2*math.sin(ang), cy - r2*math.cos(ang))], fill=col, width=w)
        
    # Angle text
    degs = [('000°', 0), ('090°', math.pi/2), ('180°', math.pi), ('270°', 3*math.pi/2)]
    for d_str, ang in degs:
        tx = cx + 205 * math.sin(ang)
        ty = cy - 205 * math.cos(ang)
        b = draw.textbbox((0, 0), d_str, font=font_bold_12)
        draw.text((tx - (b[2]-b[0])/2, ty - (b[3]-b[1])/2), d_str, font=font_bold_12, fill=(50, 208, 17))
        
    # HUD radar dial
    draw.ellipse((68, 68, 444, 444), fill=(6, 12, 18), outline=(31, 176, 1, 100), width=2)
    for r_rad in [110, 145, 175]:
        draw.ellipse((cx - r_rad, cy - r_rad, cx + r_rad, cy + r_rad), outline=(31, 176, 1, 60), width=1)
    draw.line([(cx, 75), (cx, 437)], fill=(31, 176, 1, 50), width=1)
    draw.line([(75, cy), (437, cy)], fill=(31, 176, 1, 50), width=1)
    
    # Central Mapy badge
    logo_size = 210
    logo_res = mapy_src.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
    mask = Image.new('L', (logo_size, logo_size), 0)
    ImageDraw.Draw(mask).ellipse((0, 0, logo_size - 1, logo_size - 1), fill=255)
    img.paste(logo_res, (cx - logo_size//2, cy - logo_size//2), mask)
    
    # Top heading arrow
    draw.polygon([(cx, 85), (cx - 18, 115), (cx + 18, 115)], fill=(50, 208, 17))
    draw.ellipse((cx - logo_size//2, cy - logo_size//2, cx + logo_size//2, cy + logo_size//2), outline=(50, 208, 17, 240), width=3)
    
    # TWO AVIATION GAUGE INSTRUMENT NEEDLES
    shadow_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    hands_layer = Image.new('RGBA', (512, 512), (0, 0, 0, 0))
    d_hands = ImageDraw.Draw(hands_layer)
    
    def draw_aviator_needle(ang, length, width, tip_len=36, tail_len=36):
        # Gauge needle with round counterweight tail, tapering body, high-vis striped tip
        hw = width / 2.0
        tip_y = cy - length
        tail_y = cy + tail_len
        body_top_y = cy - length + tip_len
        
        # Stem
        pts_stem = [(cx - hw, cy + 10), (cx + hw, cy + 10), (cx + hw*0.7, body_top_y), (cx - hw*0.7, body_top_y)]
        # Tip
        pts_tip = [(cx - hw*0.7, body_top_y), (cx + hw*0.7, body_top_y), (cx, tip_y)]
        
        # Counterweight disk on tail
        cw_cy = cy + tail_len * 0.65
        cw_r = hw * 1.5
        
        r_stem = rotate_pts(pts_stem, ang)
        r_tip = rotate_pts(pts_tip, ang)
        cw_pt = rotate_pts([(cx, cw_cy)], ang)[0]
        
        add_shadow(shadow_layer, r_stem + r_tip, offset=(4, 6))
        d_s = ImageDraw.Draw(shadow_layer)
        d_s.ellipse((cw_pt[0] - cw_r + 4, cw_pt[1] - cw_r + 6, cw_pt[0] + cw_r + 4, cw_pt[1] + cw_r + 6), fill=(0, 0, 0, 200))
        
        # Draw stem (Matte stealth black with silver spine)
        d_hands.polygon(r_stem, fill=(28, 34, 42), outline=(180, 190, 200), width=1)
        # Draw counterweight
        d_hands.ellipse((cw_pt[0] - cw_r, cw_pt[1] - cw_r, cw_pt[0] + cw_r, cw_pt[1] + cw_r), fill=(35, 42, 52), outline=(50, 208, 17), width=2)
        # Draw high-vis neon green flight instrument tip
        d_hands.polygon(r_tip, fill=(50, 208, 17), outline=(255, 255, 255), width=2)
        
    draw_aviator_needle(ANG_HOUR, length=118, width=14, tip_len=30, tail_len=32)
    draw_aviator_needle(ANG_MIN, length=170, width=10, tip_len=40, tail_len=38)
    
    shadow_layer = shadow_layer.filter(ImageFilter.GaussianBlur(radius=5))
    img.paste(shadow_layer, (0, 0), shadow_layer)
    img.paste(hands_layer, (0, 0), hands_layer)
    
    # Cockpit center hub screw
    draw.ellipse((cx - 15, cy - 15, cx + 15, cy + 15), fill=(25, 30, 38), outline=(50, 208, 17), width=3)
    draw.line([(cx - 7, cy), (cx + 7, cy)], fill=(240, 240, 240), width=2)
    draw.line([(cx, cy - 7), (cx, cy + 7)], fill=(240, 240, 240), width=2)
    
    img.save('store_assets/icon_candidates/candidate_5_hud_instrument.png')
    print('V5 done')

gen_v1()
gen_v2()
gen_v3()
gen_v4()
gen_v5()
print("ALL 5 CANDIDATES WITH 2 WATCH HANDS GENERATED SUCCESSFULLY")
