# Repo rules — infinite-dungeon-easynpc

Repo-specific rules and notes. The org-wide `agents/AGENTS.md` beside this file is
maintained in [`neoforge-mod-template`](https://github.com/HexaHexes/neoforge-mod-template)
and pulled in verbatim — **never hand-edit it.** Everything in this file is this
repo's own and is edited freely; keep it aligned with the org rules rather than
restating them.

**Repo:** `HexaHexes/infinite-dungeon-easynpc` · **Mod ID:** `infinite_dungeon_easynpc` · **Branch:** `main`
**Platform:** Minecraft 1.21.1 / NeoForge 21.1.259 / JDK 21 · **Jar:** `build/libs/infinite_dungeon_easynpc-<version>.jar`

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
- The NPC dialog/task content is EasyNPC authored content (in-game GUI +
  presets), which cannot be proven headless — the honest scope for such pieces
  is "build green + the user's in-game check", per the org rules.
- Never reference an EasyNPC class in a code path reachable while EasyNPC is
  absent (class-loading hazard): every touch goes through a ModList gate.
- The dev-run dependency closure for GameTests must stay absent of EasyNPC
  unless a test explicitly needs it — the harness world must not carry NPCs.