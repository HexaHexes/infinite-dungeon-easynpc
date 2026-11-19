package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/**
 * The lobby NPC placement contract (issue 1 and siblings).
 *
 * <p>Everything that is provable without a live EasyNPC instance lives here:
 * the eligibility gate, the slot table (one authored character per slot, with
 * its preset resource and its fixed lobby position), and the idempotent
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

    /** The receptionist's custom identifier: the idempotency key EasyNPC persists. */
    public static final ResourceLocation RECEPTIONIST_ID =
            ResourceLocation.fromNamespaceAndPath("infinite_dungeon_easynpc", "lobby_receptionist");

    /** The authored receptionist preset (data-driven content, not code). */
    public static final ResourceLocation RECEPTIONIST_PRESET =
            ResourceLocation.fromNamespaceAndPath(
                    "infinite_dungeon_easynpc", "easy_npc/preset/receptionist.npc.snbt");

    /**
     * The floor-1 counter position the receptionist stands at, as authored
     * content. Pending the floor-1 room layout (infinite-dungeon issue 138)
     * this is a documented constant the in-game check confirms; the import
     * never guesses, it uses exactly this.
     */
    public static final Vec3 RECEPTIONIST_POSITION = new Vec3(0.0, 0.0, 0.0);

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
