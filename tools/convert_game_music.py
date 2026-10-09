"""Transcribe user-provided MP3s into bounded-polyphony, GM piano MIDI previews.

Requires project-local basic-pitch, ONNX runtime and pretty_midi.
MP3 masters are never edited. This is approximate note transcription, not audio encoding.
"""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import numpy as np
import pretty_midi
from basic_pitch import build_icassp_2022_model_path, FilenameSuffix
from basic_pitch.inference import Model, predict


def convert(source, destination, model, work):
    wav = work / (destination.stem + ".wav")
    subprocess.run(["ffmpeg", "-v", "error", "-y", "-i", str(source),
                    "-ac", "1", "-ar", "22050", str(wav)], check=True)
    _, _, events = predict(wav, model, onset_threshold=0.55, frame_threshold=0.4,
                           minimum_note_length=140, minimum_frequency=100,
                           maximum_frequency=1500, multiple_pitch_bends=False)
    midi = pretty_midi.PrettyMIDI(initial_tempo=120)
    piano = pretty_midi.Instrument(program=0)
    ends = [0.0] * 4
    accepted = 0
    for start, end, pitch, amplitude, _ in sorted(events, key=lambda e: (e[0], -e[3])):
        start = round(float(start) / 0.05) * 0.05
        end = max(start + 0.15, round(float(end) / 0.05) * 0.05)
        free = [i for i, until in enumerate(ends) if until <= start]
        if not free or not 43 <= pitch <= 90:
            continue
        slot = free[0]
        ends[slot] = end
        piano.notes.append(pretty_midi.Note(
            velocity=max(38, min(90, int(amplitude * 100))), pitch=int(pitch),
            start=start, end=end))
        accepted += 1
    if accepted < 20:
        raise RuntimeError("Too few usable notes; refusing to package silent/invalid music")
    midi.instruments.append(piano)
    destination.parent.mkdir(parents=True, exist_ok=True)
    midi.write(str(destination))
    # Reparse the produced file, not only the in-memory transcription.
    verified = pretty_midi.PrettyMIDI(str(destination))
    report = dict(source=str(source), sha256=hashlib.sha256(source.read_bytes()).hexdigest(),
                  output=str(destination), bytes=destination.stat().st_size,
                  seconds=verified.get_end_time(), inferred_notes=len(events),
                  packaged_notes=accepted, max_voices=4, program="GM acoustic piano",
                  transcription="approximate; no remix timbre, vocals, drums or pitch bends")
    print(json.dumps(report, ensure_ascii=True), flush=True)
    return report


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--source", default="only_tham_khao/music")
    parser.add_argument("--work", default="outputs/music-20261009")
    parser.add_argument("--only", choices=["autochess", "explore", "menu"])
    args = parser.parse_args()
    root = Path(args.source)
    work = Path(args.work)
    work.mkdir(parents=True, exist_ok=True)
    model = Model(build_icassp_2022_model_path(FilenameSuffix.onnx))
    mappings = [("*[[]c4qHrexNRS8[]].mp3", Path("res/music/autochess.mid")),
                ("*[[]jV3w2PZn8z4[]].mp3", Path("res/music/explore.mid")),
                ("*[[]43QBA4OD1x4[]].mp3", Path("res/music/menu.mid"))]
    reports = []
    for pattern, output in mappings:
        if args.only and output.stem != args.only:
            continue
        matches = list(root.glob(pattern))
        if len(matches) != 1:
            raise RuntimeError("Expected exactly one source for " + pattern)
        reports.append(convert(matches[0], output, model, work))
    report_path = work / "report.json"
    if args.only and report_path.exists():
        previous = json.loads(report_path.read_text(encoding="utf-8"))
        reports = [r for r in previous if Path(r["output"]).stem != args.only] + reports
    report_path.write_text(json.dumps(reports, indent=2), encoding="utf-8")


if __name__ == "__main__":
    main()
