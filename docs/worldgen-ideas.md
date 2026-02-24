# Worldgen ideas: flat End + structures

## Current state
- `simple_life` and `complex_life` already use a flat End with 4 layers of end stone and no structure overrides.
- `simple` uses vanilla noise End terrain.
- `complex_life` overworld is a single plains biome with flat layers and structure overrides for strongholds/villages/outposts.

## Feasibility notes
- Adding End structures to flat End presets is straightforward via `structure_overrides`, but structure biome rules still apply.
- End Cities generally depend on End Highlands biome context; with a fixed `minecraft:the_end` biome, they may not spawn unless additional data overrides are added.
- End towers/spikes are feature-driven rather than simple structure overrides, so enabling them on fully flat presets may need biome/feature changes.
- Shipwrecks in `complex_life` without adding ocean biomes are not straightforward, because vanilla placement checks biome validity. A custom structure or biome tag override is likely required.

## Practical implementation order
1. Try `structure_overrides` updates for End presets.
2. If End Cities do not spawn, add a datapack-level override for valid biomes/structure set.
3. For shipwrecks in plains-only worlds, provide custom structure JSON + biome tag override.
