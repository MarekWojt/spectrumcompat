# Spectrum Compat

A compatibility mod that bridges [Spectrum](https://modrinth.com/mod/spectrum) with [The Aether](https://modrinth.com/mod/the-aether), [Deep Aether](https://modrinth.com/mod/deep-aether), [VanillaBackport](https://modrinth.com/mod/vanillabackport), [Copper Age Backport](https://modrinth.com/mod/copperagebackport) and [Better Archeology](https://modrinth.com/mod/better-archeology).

## How it works

Heads are real registered block-items tagged into `#c:skulls`, plus per-mob loot-modifier and recipe entries that plug into Spectrum's existing systems. A small set of mixins covers the cases where data alone isn't enough. Every integration is gated by mod-loaded conditions, so any combination of optional mods works out of the box.

Textures are referenced from the source mods' jars at runtime; this mod ships zero texture bytes (required by The Aether's license).

## Supported mods

| Mod                                                               | What's added                                                                                               |
| ----------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| [The Aether](https://modrinth.com/mod/the-aether)                 | 19 mob heads (incl. 3 Moa colours), anvil-crushing dye recipes for Aether flowers          |
| [Deep Aether](https://modrinth.com/mod/deep-aether)               | 11 mob heads (incl. 7 Quail variants), anvil-crushing dye recipes for DA flowers           |
| [VanillaBackport](https://modrinth.com/mod/vanillabackport)       | Happy Ghast head, warm/cold Chicken/Cow/Pig heads (memory variant-lock), anvil-crushing dyes for VB flowers |
| [Copper Age Backport](https://modrinth.com/mod/copperagebackport) | 8 Copper Golem heads (4 oxidation × waxed/unwaxed), honeycomb wax recipes                  |
| [Better Archeology](https://modrinth.com/mod/better-archeology)   | Enchanter recipes for `penetrating_strike`, `tunneling`, `soaring_winds`; disarming exclusive-set          |

Variant heads (Moa, Quail, Copper Golem, Chicken/Cow/Pig) are NBT-filtered, so the right variant drops from the right kill rather than rolling randomly.

A few mobs are deliberately skipped because they're either unkillable upstream (Whirlwinds, Gentle Wind, Slider, Creaking) or practically unreachable in survival (Windflies — no biome spawner entries). Bosses are excluded by Spectrum's own `#c:bosses` blacklist chain.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.226+
- [Spectrum](https://modrinth.com/mod/spectrum) 1.11+
- Any combination of the optional source mods listed above

## Installation

Drop the jar into your `mods/` folder alongside Spectrum and any subset of the supported mods you use.

## Building from source

Java 21 toolchain required. Uses the Gradle wrapper.

```bash
./gradlew build           # compile + test + jar
./gradlew runClient       # dev client
./gradlew runServer       # dev server
./gradlew runData         # regenerate datagen output
```

`runClient` / `runServer` will need additional `localRuntime` dependencies (Spectrum alone pulls Revelationary, Fractal, AEA, Arrowhead, Modonomicon, …) — add them to `build.gradle` as needed.

## License

See `gradle.properties` (`mod_license`). Head textures remain the property of their respective source mods and are loaded from those mods' jars at runtime — none of them are redistributed here.

## Credits

Based on [Spectrum](https://modrinth.com/mod/spectrum), [The Aether](https://modrinth.com/mod/the-aether), [Deep Aether](https://modrinth.com/mod/deep-aether), [VanillaBackport](https://modrinth.com/mod/vanillabackport), [Copper Age Backport](https://modrinth.com/mod/copperagebackport) and [Better Archeology](https://modrinth.com/mod/better-archeology). This mod is purely a compatibility layer; all gameplay systems and assets it hooks into are the work of those teams.
