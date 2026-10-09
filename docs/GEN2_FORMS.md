# Gen 2 source-backed forms — 2026-10-07

Source: local `only_tham_khao/SpriteCollab-master`. Detailed per-entry audit:
`outputs/gen2-forms-audit/audit.md` (includes incomplete and excluded entries).

- Mega: Steelix and Houndoom retained; Skarmory and Tyranitar added.
- Regional: Typhlosion Hisui and Slowking Galar retained; Wooper Paldea,
  Qwilfish Hisui, Sneasel Hisui and Corsola Galar added via Memory Disc.
- Pichu Spiky, Shadow Lugia and Unown B–Z/!/?: Memory Disc. Reuse on Unown
  cycles the 27 imported symbols; no extra synergy identity or stat penalty.
- Galar Articuno/Zapdos/Moltres now have imported animation + portraits and
  a Memory Disc activation route. Their new types apply in prep and battle.
- Meganium, Feraligatr, Ampharos, Scizor and Heracross Mega are excluded:
  the supplied source has no AnimData.xml for those Mega folders.
- Cutscene, beta, altcolor and alternate artist renditions are documented,
  not treated as additional gameplay forms.
- Mega Skarmory, Mega Tyranitar and Shadow Lugia have no supplied shiny
  animation. Existing shiny status/bonuses remain; rendering falls back to
  the complete normal atlas. No shiny asset is fabricated.
- Normal and regional copies cannot merge together. Regional intermediate
  forms use their own successor when that species exists in the roster,
  never Quagsire/Weavile on the wrong branch.

New Mega values are game balance values, not official base stats.
Tests cover activation, types, prep/battle counts, save/load, shiny retention,
Unown cycle and regional merge isolation. Emulator visual testing pending.
