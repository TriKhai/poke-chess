# Gen 1 retest — 2026-10-07

Automated coverage: all 151 Gen 1 species, normal/shiny, save/load, base stats
and boosts, supported Mega/regional forms, battle type consistency and avatar
resource routing. Existing combat and form suites run alongside this audit.

Fixed both battle-result and end-of-run portraits: shiny, Mega + shiny, and
special/regional form + shiny now use the corresponding avatar route. Tiny
result portraits use a bounded cache; no per-frame resizing on cache hits.

Evolution choices after a three-copy tier-2 merge:

- Gloom -> Vileplume / Bellossom.
- Poliwhirl -> Poliwrath / Politoed.

These are actual species branches, not regional-form IDs. Popup rows show
the correct avatar, name and game synergy types. Two-touch confirmation is
retained. Pending choice survives save/load; shiny and boosts are preserved.
Both alternatives are tier 3 in their starter family. They count as distinct
branches for synergies; lower-tier starter copies do not add another count.
The test-room and detail previews also include the alternative species.

Tests exercise both choices with and without shiny, before/after save, moves,
battle construction, and mixed tier-2 + both tier-3 branch synergy counts.

Not covered: visual/emulator inspection of every animation or real-device
touch/FPS testing. Automated avatar path checks are not a visual screenshot
verification.
