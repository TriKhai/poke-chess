# Poke Auto Chess ME 1.4.0

Stable offline release for Java ME CLDC 1.0 / MIDP 2.0.

## Artifacts

- Full: `PokeAutoChess-Full.jar` — 35,677,072 bytes
  - SHA-256: `19C1F4BA4D32DA272EA68B54DBBD5D3EAB4F56715C180DB9F83F582DA6CB6501`
- Lite: `PokeAutoChess-Lite.jar` — 2,989,947 bytes
  - SHA-256: `C7070967FD8E94904D0E79A0080034E79464F3DA634EF209BA07D332A4FFD6FC`

Both JAD files declare vendor `Kdic`, `MIDlet-Version: 1.4.0` and
`PAC-Build: 1.4.0`.

## Automated release gate

- CLDC compilation and preverification passed.
- Responsive matrix passed at 128x160, 176x208, 240x320 and 320x240.
- Corrupt/truncated run saves are rejected safely.
- Deterministic battle pairs passed.
- 126 base battles and 55 item battles passed.
- Full contains raw/dex animation, skill effects and maps.
- Lite excludes raw, dexraw, SFX and maps while retaining fallback art.
- Both builds contain the original five-frame purchase SPAWN animation.
- JAR manifest and JAD sizes/versions were validated against the final files.

## Device note

The release gate validates code and packages on the configured Java ME SDK.
A final 30–60 minute soak test on the target Nokia E72 is still recommended
before public distribution because emulator results cannot prove device RAM,
firmware-specific RMS behavior or real keypad timing.
