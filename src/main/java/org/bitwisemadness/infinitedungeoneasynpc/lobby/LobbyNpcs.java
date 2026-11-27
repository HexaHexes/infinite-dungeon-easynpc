package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * The lobby NPC placement contract (issue 1 and siblings).
 *
 * <p>Everything that is provable without a live EasyNPC instance lives here:
 * the eligibility gate, the slot table (one authored character per slot, on
 * its fixed lobby floor per the upstream floor contract), and the idempotent
 * orchestration seam. The EasyNPC-touching work is isolated in
 * {@link EasyNpcImport} so this class - and every class it reaches while
 * EasyNPC is absent - carries zero EasyNPC references (the class-loading
 * hazard in agents/REPO.md).</p>
 *
 * <p>Eligibility requires both EasyNPC and Infinite Dungeon to be loaded; with
 * either absent the lobby simply stays empty (the degrade story). The slot
 * positions are authored content relative to the fixed lobby, confirmed by the
 * in-game check for issue 1 - the coordinates cannot be proven headless.</p>
 */
public final class LobbyNpcs {

    /** The authored receptionist preset (data-driven content, not code). */
    public static final ResourceLocation RECEPTIONIST_PRESET =
            ResourceLocation.fromNamespaceAndPath(
                    "infinite_dungeon_easynpc", "easy_npc/preset/receptionist.npc.snbt");

    /**
     * The floor-0 counter position the receptionist stands at, as authored
     * content in the fixed lobby's local coordinates (infinite-dungeon #300,
     * the committed 7x21x7 lobby building): three cells south of the counter
     * (lobby-local 2..4, 1, 2), facing it. The world position resolves from
     * the lobby origin at the in-game check; the import never guesses.
     */
    public static final Vec3 RECEPTIONIST_POSITION = new Vec3(3.0, 1.0, 3.0);

    /** The authored guild master preset (issue 2, data-driven content, not code). */
    public static final ResourceLocation GUILD_MASTER_PRESET =
            ResourceLocation.fromNamespaceAndPath(
                    "infinite_dungeon_easynpc", "easy_npc/preset/guild_master.npc.snbt");

    /**
     * The guild master's office position (issue 2): floor-0, back-left of the
     * reception, lobby-local per the #300 geometry.
     */
    public static final Vec3 GUILD_MASTER_POSITION = new Vec3(1.0, 1.0, 4.0);

    /** The authored adventurer preset (issue 3, data-driven content, not code). */
    public static final ResourceLocation ADVENTURER_PRESET =
            ResourceLocation.fromNamespaceAndPath(
                    "infinite_dungeon_easynpc", "easy_npc/preset/adventurer.npc.snbt");

    /**
     * The adventurer's waystone-room position (issue 3): floor-4, in front of
     * the waystone pedestal (lobby-local 3, 16, 3), per the #300 geometry.
     */
    public static final Vec3 ADVENTURER_POSITION = new Vec3(3.0, 17.0, 2.0);

    /** The essentials-shopkeeper position: floor-1, in front of the counter
     *  rows (lobby-local 3, 5, 3), per the #301 geometry. */
    public static final Vec3 ESSENTIALS_SHOPKEEPER_POSITION = new Vec3(3.0, 5.0, 3.0);

    /** The specialised-shopkeeper position: floor-2, in front of the spruce
     *  counter row (lobby-local 3, 9, 3), per the #302 geometry. */
    public static final Vec3 SPECIALISED_SHOPKEEPER_POSITION = new Vec3(3.0, 9.0, 3.0);

    /** The advanced-shopkeeper position: floor-3, in front of the dark-oak
     *  counter row (lobby-local 3, 13, 3), per the #302 geometry. */
    public static final Vec3 ADVANCED_SHOPKEEPER_POSITION = new Vec3(3.0, 13.0, 3.0);

    /** The slot table: one authored character per slot, on its fixed floor. */
    public static final LobbySlot RECEPTIONIST = new LobbySlot(
            "lobby_receptionist", LobbyFloor.GUILD_RECEPTION, Optional.of(RECEPTIONIST_PRESET));

    /** The guild master slot (issue 2): the floor-1 office, with its authored preset. */
    public static final LobbySlot GUILD_MASTER = new LobbySlot(
            "lobby_guild_master", LobbyFloor.GUILD_RECEPTION, Optional.of(GUILD_MASTER_PRESET));

    /** The adventurer slot (issue 3): the floor-5 waystone room, with its authored preset. */
    public static final LobbySlot ADVENTURER = new LobbySlot(
            "lobby_adventurer", LobbyFloor.WAYSTONE_ROOM, Optional.of(ADVENTURER_PRESET));

    /** The essentials-shopkeeper slot: floor 1, behind the counter (declared
     *  placement awaiting its authored content - the shopkeeper is flavour
     *  behind the counter, the transaction is the Lightman's trader block). */
    public static final LobbySlot ESSENTIALS_SHOPKEEPER = new LobbySlot(
            "lobby_essentials_shopkeeper", LobbyFloor.GUILD_SHOPS, Optional.empty());

    /** The specialised-shopkeeper slot: floor 2, behind the counter. */
    public static final LobbySlot SPECIALISED_SHOPKEEPER = new LobbySlot(
            "lobby_specialised_shopkeeper", LobbyFloor.SPECIALISED_SHOPS, Optional.empty());

    /** The advanced-shopkeeper slot: floor 3, behind the counter. */
    public static final LobbySlot ADVANCED_SHOPKEEPER = new LobbySlot(
            "lobby_advanced_shopkeeper", LobbyFloor.ADVANCED_SHOPS, Optional.empty());

    private LobbyNpcs() {
    }

    /**
     * Whether the lobby can carry its NPCs. Both mods must be present: the
     * EasyNPC classes for the characters, and Infinite Dungeon for the lobby
     * itself. Anything else leaves the lobby empty by design.
     */
    public static boolean isEligible() {
        return ModList.get().isLoaded("easynpc") && ModList.get().isLoaded("infinite_dungeon");
    }

    /**
     * Populates the lobby's authored characters exactly once per world. Called
     * at server start (NPC persistence makes re-entry idempotent); a no-op
     * whenever eligibility fails, so the absent-mod world is untouched.
     */
    public static void ensureLobbyPopulated(MinecraftServer server) {
        if (!isEligible()) {
            return;
        }
        EasyNpcImport.ensureReceptionist(server.overworld());
    }

    /** The server-start hook wiring {@link #ensureLobbyPopulated}. */
    public static void onServerStarted(ServerStartedEvent event) {
        ensureLobbyPopulated(event.getServer());
    }
}
