# Repo rules — infinite-dungeon-easynpc

Repo-specific rules and notes. The org-wide `agents/AGENTS.md` beside this file is
maintained in [`neoforge-mod-template`](https://github.com/HexaHexes/neoforge-mod-template)
and pulled in verbatim — **never hand-edit it.** Everything in this file is this
repo's own and is edited freely; keep it aligned with the org rules rather than
restating them.

**Repo:** `HexaHexes/infinite-dungeon-easynpc` · **Mod ID:** `examplemod` · **Branch:** `neoforge-1.21.1` (the loader line; this is the default branch - no `main`)
**Platform:** Minecraft 1.21.1 / NeoForge 21.1.259 / JDK 21 · **Jar:** `build/libs/examplemod-neoforge-1.21.1-<version>.jar`

The optional EasyNPC module for Infinite Dungeon: the floor-1 counter
receptionist (issue 1), the guild master in the floor-1 office (issue 2) and
the adventurer NPCs around the guild, the shops and floor 5 (issue 3) that
populate the fixed-construction lobby. NPCs are characters only — every
transaction is a Lightman's Currency trader (user ruling), so EasyNPC never
sells or buys (trading mode None, default Open Trading action stripped). The
lobby degrades gracefully without EasyNPC or without Infinite Dungeon: empty
rooms, no NPCs, core unaffected.

**Dependencies**
- Required: Minecraft, NeoForge.
- Optional: EasyNPC (`easynpc`, >= 7.17.1, the version the capability spike
  verified) — all code touching EasyNPC classes is gated by
  `ModList.get().isLoaded("easynpc")`, and the module is inert without it.
- Optional: Infinite Dungeon (`infinite_dungeon`, >= 1.0.0-alpha.60) — the
  lobby positions this module populates come from Infinite Dungeon's fixed
  construction; without it the module loads but places nothing.

**Test:** `./gradlew runGameTestServer` — `LobbyNpcGameTests` (1 test). `expectedTests = 1`
in `build.gradle` must match the `@GameTest` count.

**Gotchas**
- Zero mixins: this is a public-API addon (org doctrine). The template's mixins
  block and `examplemod.mixins.json` were removed on bootstrap.
- **Mod identity lives in `identity.properties`, not gradle.properties.** The
  version-bump check forbids bundling gradle.properties with other changes,
  while a rename must ship its identity with code — so build.gradle loads
  `identity.properties` into the project extras, and gradle.properties stays
  byte-identical (its leftover template identity keys are inert, shadowed by
  the extras). A rename edits `identity.properties`, `settings.gradle` and the
  code — never gradle.properties (the `-alpha` version slot there is
  CI/CD-owned).
- **`LobbyFloor` mirrors the upstream lobby contract** (infinite-dungeon #131,
  landed as `LobbyFloors` in #288): the five fixed floors and their roles. It
  is data, not a class reference, to keep this module decoupled from the
  optional infinite-dungeon artifact. If the upstream contract changes, update
  the mirror AND its GameTest (`lobby_floor_mirror_matches_the_upstream_contract`)
  in the same PR.
- The NPC dialog/task content is EasyNPC authored content (in-game GUI +
  presets), which cannot be proven headless — the honest scope for such pieces
  is "build green + the user's in-game check", per the org rules.
- Never reference an EasyNPC class in a code path reachable while EasyNPC is
  absent (class-loading hazard): every touch goes through a ModList gate.
- The dev-run dependency closure for GameTests must stay absent of EasyNPC
  unless a test explicitly needs it — the harness world must not carry NPCs.
