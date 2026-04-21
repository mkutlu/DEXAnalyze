# DEXAnalyze — Android Logo Package

A complete, drop-in launcher-icon package for the DEXAnalyze Android app.

## Design Concept

The mark is a stylized **letter D** constructed from horizontal scan bars.
The bars' lengths trace the curve of the D, so the logo simultaneously reads as:

- the **D** in DEXAnalyze (brand identity)
- a **stack of body-composition data bars** (the product — DEXA scan output)
- a **bar chart / readout** (the purpose — analysis)

The mark sits on a deep navy → teal diagonal gradient that conveys trust,
health, and technology.

**Palette**

| Role | Hex |
|---|---|
| Background (dark) | `#0A2540` |
| Background (mid) | `#0E4C75` |
| Background (light/teal) | `#1A8FA8` |
| Mark gradient (top) | `#B7F5EA` |
| Mark gradient (bottom) | `#38BDF8` |

## Package Contents

```
dexanalyze-logo/
├── mipmap-mdpi/              48x48 legacy + 108x108 adaptive fg
├── mipmap-hdpi/              72x72 legacy + 162x162 adaptive fg
├── mipmap-xhdpi/             96x96 legacy + 216x216 adaptive fg
├── mipmap-xxhdpi/           144x144 legacy + 324x324 adaptive fg
├── mipmap-xxxhdpi/          192x192 legacy + 432x432 adaptive fg
├── mipmap-anydpi-v26/
│   ├── ic_launcher.xml           adaptive icon definition
│   └── ic_launcher_round.xml     round variant (same definition)
├── values/
│   └── ic_launcher_colors.xml    <color name="ic_launcher_background">
├── playstore/
│   └── ic_launcher_playstore.png 512x512 Play Store listing icon
├── preview_512.png           full-resolution preview
└── generate.py               source generator (Python + PIL)
```

## Installation

1. Copy every `mipmap-*` folder into your project's `app/src/main/res/`.
   If folders already exist, merge the files.

2. Copy `values/ic_launcher_colors.xml` into `app/src/main/res/values/`.
   If you already have a `colors.xml`, just copy the single `<color>` line
   into it instead:

   ```xml
   <color name="ic_launcher_background">#0E4C75</color>
   ```

3. In your `AndroidManifest.xml`, the `<application>` element should reference
   the icons (this is usually already set by Android Studio):

   ```xml
   <application
       android:icon="@mipmap/ic_launcher"
       android:roundIcon="@mipmap/ic_launcher_round"
       ...>
   ```

4. Use `playstore/ic_launcher_playstore.png` when you upload the app to the
   Google Play Console ("Main store listing" → "App icon"). Do **not** ship
   this file inside the APK/AAB.

## How the Adaptive Icon Works

On Android 8.0+ (API 26+) launchers apply their own mask to your icon
(circle, squircle, rounded square, teardrop, etc.). The icon is composed
from two layers:

- **Background**: the solid color `#0E4C75` (defined in `ic_launcher_colors.xml`).
- **Foreground**: `ic_launcher_foreground.png` — the D mark on a transparent
  canvas, sized so the important content stays inside the 66dp safe zone.

On Android < 8.0 the launcher falls back to the legacy square PNGs
(`ic_launcher.png` / `ic_launcher_round.png`), which include the gradient
background baked in.

## Regenerating or Tweaking

`generate.py` is the full source. It's a single Python file with no
dependencies beyond Pillow:

```bash
pip install Pillow
python3 generate.py
```

Edit the color constants at the top of `render_logo()` to re-theme, or
tweak `draw_d_bars()` to change bar thickness, gap, or the D's proportions.
Re-run to regenerate every size.
