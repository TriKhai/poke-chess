# Evolution and shop audit — 2026-10-07

Scope: Gen 1 / Gen 2 starter families in the current runtime roster,
including the later-generation Eevee descendants already present in-game.
This is a code/resource-route audit, not a claim that all SpriteCollab forms
for all nine generations have been imported.

## Choice routes

| Merge | Choices |
| --- | --- |
| Gloom | Vileplume, Bellossom |
| Poliwhirl | Poliwrath, Politoed |
| Eevee | Vaporeon, Jolteon, Flareon, Espeon, Umbreon, Leafeon, Glaceon, Sylveon |
| Slowpoke | Slowbro, Galar Slowbro, Slowking, Galar Slowking |
| Tyrogue | Hitmonlee, Hitmonchan, Hitmontop |

Other supported regional evolutions retain their normal/regional choice.
Single-result stone evolutions remain the existing automatic merge route:
there is no redundant one-option popup. Stone evolution does not require
buying a synergy stone; synergy stones retain their existing team bonus.

The automated stone-family exclusion check includes Raichu, Sandslash,
Nidoqueen, Nidoking, Clefable, Ninetales, Wigglytuff, Vileplume, Arcanine,
Poliwrath, Victreebel, Cloyster, Exeggutor, Starmie, Bellossom and Sunflora;
all eight Eevee descendants are checked through the branch suite.

## Shop and purchase

- All evolved branch species share their starter family and are not base rolls.
- Existing saves clear evolved species from the shop and pool without removing
  already-owned Pokémon.
- Standalone regional base species, including Articuno, Zapdos and Moltres,
  open a form choice before purchase. Confirmation creates the chosen form;
  cancel leaves the shop, gold and bench unchanged.
- No three-copy pseudo-evolution for standalone birds.
- Regional Gen 2 intermediates still merge to their correct regional descendant.
- Detail avatars include all family members, not only the default branch.

Choices show two rows per page. Direction keys cycle all options; touch the
footer left/right to navigate, tap a row to hover and tap it again to confirm.
For a purchase, 0 / right softkey cancels; touching its footer also cancels.
Popup choice survives save/load for a completed merge. A not-yet-confirmed
purchase is UI-only and is abandoned when leaving the screen.

## Verification and remaining limits

Expanded regression covers 19 evolution choices, shiny/boost preservation,
pending/post-choice save/load, old-shop cleanup and both forms of all three
birds, including a full-bench purchase that must not charge gold.
The existing Gen 1, Gen 2, combat, synergy and packaged-resource suites also run.
No emulator screenshot, real-device touch test, or animation-by-animation
visual inspection has been performed.

Missing-animation Mega candidates remain disabled: Meganium, Feraligatr,
Ampharos, Scizor and Heracross. An avatar alone is not sufficient.
