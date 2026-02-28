# Limited-Last Life Addon

This folder is a standalone Fabric addon mod project. Build it into its own jar and place it next to the main `lifeseries` mod jar.

## What it adds
- `limitedlastlife` season implementation.
- Limited-Last Life team/color behavior:
  - `1 = Dark Red`
  - `2 = Red`
  - `3 = Yellow`
  - `4 = Green`
  - `5 = Dark Green`
  - `6 = Blue`
- Team backup/restore when switching in/out of the season.
- Default randomized time lives and extra color thresholds (`0-1h dark red`, `32h + 1s blue`).

## Why this works
LifeSeries core resolves `limitedlastlife` by reflecting the class
`net.mat0u5.lifeseries.seasons.season.limitedlastlife.LimitedLastLife`.
When this addon is installed, that class exists and the season is enabled.

## Build
From repository root:

```bash
bash ./gradlew -p addons/limitedlastlife-addon build -Plifeseries_jar=/absolute/path/to/lifeseries.jar
```

- `lifeseries_jar` must point to the main mod jar for compile-time symbols.
- Output jar: `addons/limitedlastlife-addon/build/libs/`

## Install
Put both jars in your server/client `mods/` folder:
1. `lifeseries` main mod jar
2. `lifeseries-limitedlastlife-addon` jar built from this folder
