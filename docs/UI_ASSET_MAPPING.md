# UI asset mapping

The v1.0.7 HUD follows the paths referenced by the original `app` source.

| UI role | Original asset path |
|---|---|
| Gold | `assets/icons/money.svg` / `assets/ui/money-bg.png` |
| Player HP | `assets/ui/heart.png` |
| Team size | `assets/ui/pokeball.svg` |
| Reroll | `assets/ui/refresh.svg` |
| Shop lock | `assets/ui/lock-close.svg`, `lock-open.svg` |
| Stats | `assets/icons/ATK.png`, `DEF.png`, `HP.png`, `SHIELD.png` |
| Synergies | `assets/types/{TYPE}.svg` and supplied `types{tps}/{TYPE}.png` |
| Board cells | `assets/ui/cell.png`, `board_cell.png` |

SVG-only controls have a CLDC-safe primitive fallback. The supplied PNG/TPS type
pack is used directly so all 31 synergy icons render without an SVG library.
Map assets are intentionally not converted or modified by this UI update.
