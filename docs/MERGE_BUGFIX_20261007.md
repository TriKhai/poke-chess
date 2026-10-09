# Merge regression: Magmar / Electabuzz and special markers

The later-generation linker compared a core family's normalized starter dex
(Magby/Elekid) against the later roster's canonical family dex (Magmar/Electabuzz).
As a result the last evolution edge was missing despite the UI listing three tiers.
It now compares resolved runtime family IDs. Magmar -> Magmortar and
Electabuzz -> Electivire are restored without changing sprites or merge costs.

Earlier generated merge tests only enumerated existing Data.evo edges, so they
could not prove that a required edge was present. Independent expected pairs now
cover Magnezone, Lickilicky, Rhyperior, Tangrowth, Electivire, Magmortar, Togekiss,
Yanmega, Gliscor, Mamoswine and Porygon-Z. Tests include three copies on the bench,
split board/bench, shiny/boost retention, save/load and nine shop purchases of Magby.
The general Gen1/2 suite now exercises 231 existing normal/full-bench routes.

Loading an old run rechecks merges after all unit state has been restored. Three
previously stranded Magmar evolve on resume; a pending choice still pauses merges.

Special red/cyan markers no longer include regional or cosmetic forms. Pichu Spiky
previously marked the entire Pikachu family red. Mega and item-only special upgrades
retain red availability and cyan transformed markers; tier dots remain yellow/green.

Release tests and packaged-resource checks passed. No emulator visual inspection
or real-device input testing was performed in this fix.
