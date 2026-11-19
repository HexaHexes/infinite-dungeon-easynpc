package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import de.markusbordihn.easynpc.api.handler.EasyNPCEntityHandler;
import de.markusbordihn.easynpc.data.preset.PresetType;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;

/**
 * The EasyNPC import glue (issue 1). Isolated in its own class - the ONLY
 * class in the module that names an EasyNPC type - so it is referenced
 * exclusively from the eligibility-gated branch of {@link LobbyNpcs} and never
 * loads while EasyNPC is absent (agents/REPO.md hazard).
 *
 * <p>The receptionist is imported from the module's authored preset
 * ({@code data/infinite_dungeon_easynpc/easynpc/preset/receptionist.npc.snbt}),
 * which carries the trading-mode ruling: the default Open Trading action is
 * stripped, so the character is flavour and dialog only - every transaction
 * stays a Lightman's Currency trader. Import is idempotent through the custom
 * identifier: EasyNPC persists the identifier in its world index, so a
 * re-import after a restart updates rather than duplicates.</p>
 */
public final class EasyNpcImport {

    private EasyNpcImport() {
    }

    /**
     * Places the receptionist at the counter exactly once. A world that
     * already has the character (custom identifier present) is left alone;
     * otherwise the authored preset is imported at the fixed position with a
     * fresh identity.
     */
    public static void ensureReceptionist(ServerLevel level) {
        if (!EasyNPCEntityHandler.getByCustomIdentifier(LobbyNpcs.RECEPTIONIST_ID).isEmpty()) {
            return;
        }
        EasyNPCEntityHandler.spawnFromPreset(
                PresetType.DATA,
                LobbyNpcs.RECEPTIONIST_PRESET,
                level,
                LobbyNpcs.RECEPTIONIST_POSITION,
                UUID.randomUUID(),
                null,
                LobbyNpcs.RECEPTIONIST_ID);
    }
}
