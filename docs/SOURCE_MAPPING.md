# App source to J2ME mapping

The TypeScript project remains the gameplay specification. Keep generated data and
simulation rules separate from MIDP rendering so features can be ported incrementally.

| Original app source | J2ME destination | Responsibility |
|---|---|---|
| `config/game/battle.ts` | `CombatRules.java` | Tick, mana, crit and armor constants |
| `core/simulation.ts` | `Battle.java` | Deterministic fight loop and win conditions |
| `core/*-state.ts` | `Unit.state`, `Battle.act()` | Move, attack, cast, idle transitions |
| `core/pokemon-entity.ts` | `Unit.java` | Runtime stats and current target |
| `models/colyseus-models/status.ts` | `CombatStatus.java` | Timed crowd control and damage-over-time |
| `core/abilities/*` | `SkillEffects.java`, `Battle.cast()` | Ability strategy and secondary effects |
| `models/precomputed/pokemons-data.csv` | generated `Data.java` | Gen 1-3 source stats and ability names |
| `public/src/game` | `ChessScreen.java`, `Art.java` | Presentation only; no combat decisions |

Regenerate `Data.java` after changing source data:

```text
python tools/generate_gen123_roster.py pokemons-data.csv pokemon.ts src/pac/Data.java
```

Desktop smoke test files live under `test/` and are excluded from the Ant MIDlet build.
