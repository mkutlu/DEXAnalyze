"""
DEXAnalyze Logo — Abstract Mark

Concept: A 'D' monogram constructed entirely from horizontal scan bars.
The stack of bars simultaneously reads as the letter D (brand) AND as
a DEXA body-composition data readout / chart (product purpose).
"""

from PIL import Image, ImageDraw
import os
import math

OUT_DIR = "/home/claude/dexanalyze-logo"


def lerp(a, b, t): return a + (b - a) * t
def lerp_color(c1, c2, t): return tuple(int(lerp(c1[i], c2[i], t)) for i in range(3))


def make_diagonal_gradient(size, c1, c2, c3):
    img = Image.new("RGB", (size, size))
    px = img.load()
    max_d = (size - 1) * 2
    for y in range(size):
        for x in range(size):
            d = (x + y) / max_d
            px[x, y] = lerp_color(c1, c2, d * 2) if d < 0.5 else lerp_color(c2, c3, (d - 0.5) * 2)
    return img


def rounded_mask(size, radius):
    m = Image.new("L", (size, size), 0)
    ImageDraw.Draw(m).rounded_rectangle([0, 0, size - 1, size - 1], radius=radius, fill=255)
    return m


def vertical_gradient_rgba(wh, top, bottom):
    w, h = wh
    img = Image.new("RGBA", (w, h))
    px = img.load()
    for y in range(h):
        t = y / max(1, h - 1)
        c = lerp_color(top, bottom, t)
        for x in range(w):
            px[x, y] = (c[0], c[1], c[2], 255)
    return img


def radial_glow(size, center, radius, color, max_alpha):
    layer = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    px = layer.load()
    cx, cy = center
    r2 = radius * radius
    for y in range(max(0, cy - radius), min(size, cy + radius)):
        for x in range(max(0, cx - radius), min(size, cx + radius)):
            dx, dy = x - cx, y - cy
            d2 = dx * dx + dy * dy
            if d2 < r2:
                t = 1 - math.sqrt(d2) / radius
                t = t * t
                px[x, y] = (*color, int(max_alpha * t))
    return layer


def draw_d_bars(draw, s):
    """Draw the 'D' formed by left vertical stroke + horizontal bars
    whose lengths trace a half-ellipse (the curved right edge of the D).
    All coordinates in a 512 reference grid.

    D box roughly centered: horizontally spans [148..364], vertically [112..400].
    """
    # Horizontal: center the full D (left stroke + curve) in the 512 canvas.
    # Total D width = left_stroke_w + rx = 34 + 132 = 166
    # Center it: left = (512 - 166) / 2 = 173
    left = 148
    top = 112
    bottom = 400
    height = bottom - top           # 288
    center_y = (top + bottom) / 2

    left_stroke_w = 34
    left_stroke_x2 = left + left_stroke_w

    rx = 132                        # horizontal radius of the D's curve
    ry = height / 2
    curve_cx = left_stroke_x2

    bar_height = 16
    bar_gap = 8
    bar_unit = bar_height + bar_gap

    pad = 12
    available = height - 2 * pad
    num_bars = int(available // bar_unit)
    total_bars_h = num_bars * bar_unit - bar_gap
    bars_top = top + (height - total_bars_h) / 2

    # Left vertical stroke
    draw.rounded_rectangle(
        [s(left), s(top), s(left_stroke_x2), s(bottom)],
        radius=s(8), fill=(255, 255, 255, 255)
    )

    # Horizontal bars tracing the right curve.
    # Inset factor makes bars slightly shorter than the exact ellipse so the
    # curve reads as a bar-graph silhouette rather than a filled D.
    for i in range(num_bars):
        y1 = bars_top + i * bar_unit
        y2 = y1 + bar_height
        by_center = (y1 + y2) / 2
        dy = by_center - center_y
        ratio = abs(dy) / ry
        if ratio >= 1:
            continue
        x_extent = rx * math.sqrt(1 - ratio * ratio)
        bar_x1 = left_stroke_x2 + 6
        bar_x2 = curve_cx + x_extent
        if bar_x2 - bar_x1 < 10:
            continue
        draw.rounded_rectangle(
            [s(bar_x1), s(y1), s(bar_x2), s(y2)],
            radius=s(bar_height / 2),
            fill=(255, 255, 255, 255)
        )


def render_logo(size, include_background=True, foreground_only=False):
    S = size
    def s(v): return int(round(v * S / 512))

    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))

    if include_background:
        grad = make_diagonal_gradient(
            S,
            (10, 37, 64),    # #0A2540
            (14, 76, 117),   # #0E4C75
            (26, 143, 168),  # #1A8FA8
        ).convert("RGBA")
        img.paste(grad, (0, 0), rounded_mask(S, s(112)))

    if include_background and not foreground_only:
        glow = radial_glow(S, (s(256), s(256)), s(200),
                           color=(127, 231, 220), max_alpha=70)
        img.alpha_composite(glow)

    # Draw the D into a white mask
    mark_mask = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    md = ImageDraw.Draw(mark_mask)
    draw_d_bars(md, s)

    # Tint with cyan→blue gradient
    grad_fill = vertical_gradient_rgba(
        (S, S),
        top=(183, 245, 234),     # light cyan
        bottom=(56, 189, 248),   # bright blue
    )
    alpha = mark_mask.split()[3]
    tinted = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    tinted.paste(grad_fill, (0, 0), alpha)
    img.alpha_composite(tinted)

    return img


def main():
    # Legacy square launcher icons
    legacy = {"mipmap-mdpi": 48, "mipmap-hdpi": 72, "mipmap-xhdpi": 96,
              "mipmap-xxhdpi": 144, "mipmap-xxxhdpi": 192}
    for folder, sz in legacy.items():
        out = os.path.join(OUT_DIR, folder)
        os.makedirs(out, exist_ok=True)
        big = render_logo(sz * 4)
        small = big.resize((sz, sz), Image.LANCZOS)
        small.save(os.path.join(out, "ic_launcher.png"), "PNG")
        small.save(os.path.join(out, "ic_launcher_round.png"), "PNG")
        print(f"  ✓ {folder}/ic_launcher.png ({sz}x{sz})")

    # Adaptive foregrounds
    adaptive = {"mipmap-mdpi": 108, "mipmap-hdpi": 162, "mipmap-xhdpi": 216,
                "mipmap-xxhdpi": 324, "mipmap-xxxhdpi": 432}
    for folder, sz in adaptive.items():
        out = os.path.join(OUT_DIR, folder)
        fg = render_logo(sz * 2, include_background=False, foreground_only=True)
        fg = fg.resize((sz, sz), Image.LANCZOS)
        fg.save(os.path.join(out, "ic_launcher_foreground.png"), "PNG")
        print(f"  ✓ {folder}/ic_launcher_foreground.png ({sz}x{sz})")

    # Play Store
    play = render_logo(1024).resize((512, 512), Image.LANCZOS)
    os.makedirs(os.path.join(OUT_DIR, "playstore"), exist_ok=True)
    play.save(os.path.join(OUT_DIR, "playstore", "ic_launcher_playstore.png"), "PNG")
    print("  ✓ playstore/ic_launcher_playstore.png (512x512)")

    render_logo(512).save(os.path.join(OUT_DIR, "preview_512.png"), "PNG")
    print("  ✓ preview_512.png")


if __name__ == "__main__":
    main()
