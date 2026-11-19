# Infinite Dungeon EasyNPC

Populates Infinite Dungeon's fixed lobby with EasyNPC characters: the floor-1
receptionist at the counter, the guild master in the office, and adventurer
NPCs around the guild, the shops and on floor 5. NPCs are characters only —
every transaction stays a Lightman's Currency trader, so EasyNPC never sells
or buys. Without EasyNPC the lobby is simply empty: no NPCs, Infinite Dungeon
unaffected.

## What it does

- Imports the lobby's authored NPC presets at their fixed positions when
  EasyNPC is present.
- The floor-1 counter receptionist handles registration and tasks.
- The guild master in the floor-1 office and the adventurer ambience ship in
  the sibling issues (2 and 3).

## Dependencies

| | Mod | Why |
|---|---|---|
| Required | Minecraft | — |
| Required | NeoForge | — |
| Optional | EasyNPC | The NPCs themselves; absent -> empty rooms, module inert |
| Optional | Infinite Dungeon | The fixed lobby this module populates; absent -> nothing to place |

## In-game config editing

Mods built with this template can be edited in-game with
[Configured](https://www.curseforge.com/minecraft/mc-mods/configured) - install
it like any other mod; it adds no code to this one. Open the mod list, select a
mod, press Config, and change values directly: Configured writes through
NeoForge's config system, so a reload applies the new values without restarting
the game.

This template pre-wires the config-screen background in
`src/main/templates/META-INF/neoforge.mods.toml`
(`[modproperties.${mod_id}] configuredBackground`) - point it at any texture you
like. Note Configured only shows a menu for mods that actually register a
config: a template-derived mod with no registered settings has nothing to show.

## Module layout

Derived from the org's `neoforge-mod-template` (bootstrap PR), with one
deliberate deviation: the mod identity lives in **`identity.properties`**,
loaded by `build.gradle` into the project extras. `gradle.properties` keeps
only the CI-owned version slot — the version-bump check forbids bundling
`gradle.properties` with other changes, while a rename must ship its identity
with its code. A rename therefore touches:

1. **`identity.properties`**: `mod_id`, `mod_name`, `mod_group_id`,
   `mod_authors`, `mod_description`. This is the single source of truth for
   identity: `build.gradle` reads these into the extras the
   `neoforge.mods.toml` placeholders are filled from. The leftover template
   identity keys in `gradle.properties` are inert (shadowed by the extras).
2. **`settings.gradle`**: `rootProject.name` to the same value as `mod_id`.
3. **Java package**: `src/main/java` and `src/gametest/java` under the new
   `mod_group_id` path, with updated `package` declarations.
4. **`InfiniteDungeonEasyNpc.MOD_ID`**: the `@Mod` id, which must match
   `mod_id` exactly.
5. **`agents/REPO.md`**: repo header, dependencies, test line, gotchas. Never
   edit `agents/AGENTS.md` — it is pulled verbatim from the template repo.
6. **Publishing block in `build.gradle`**: the GitHub Packages URL
   (`https://maven.pkg.github.com/HexaHexes/<repo-name>`).
7. **`NOTICE`**: the project name. The file is mandatory and must never be
   deleted.

Search for leftovers before your first build:

```
examplemod
Example Mod
org.bitwisemadness.examplemod
```

All three should return nothing once the rename is complete.

## Credits

- Built on [NeoForge](https://neoforged.net/).

## License

GNU Lesser General Public License v2.1 — see [`LICENSE`](LICENSE).
Copyright (c) 2026 Bitwise Madness.

## AI disclosure

Parts of this project were written with the assistance of AI coding tools.
AI-assisted code, data and documentation ship only after human review by the
maintainers.
