package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import de.markusbordihn.easynpc.api.event.EasyNPCEventRegistry;
import net.minecraft.server.level.ServerPlayer;

/**
 * The EasyNPC-side wiring of the dialogue bridge: a dialog event listener that
 * routes every opened NPC dialog into the API's session store through
 * {@link DialogueBridge}. Like {@link EasyNpcImport}, this class names EasyNPC
 * types and is reached ONLY from the eligibility-gated branch of
 * {@link LobbyNpcs}, so it never loads while EasyNPC is absent.
 *
 * <p>Registration is idempotent per JVM: the event registry takes a listener
 * instance, and the module registers exactly one at construction when
 * eligible.</p>
 */
public final class EasyNpcDialogBridge {

    private EasyNpcDialogBridge() {
    }

    /** Registers the dialog listener; safe to call only when eligible. */
    public static void register() {
        EasyNPCEventRegistry.registerDialogEventListener(
                (easyNpc, serverPlayer, dialog) -> record(easyNpc, serverPlayer, dialog));
    }

    private static void record(
            de.markusbordihn.easynpc.entity.easynpc.EasyNPC<?> easyNpc,
            ServerPlayer serverPlayer,
            de.markusbordihn.easynpc.data.dialog.DialogDataEntry dialog) {
        DialogueBridge.recordNpcLine(serverPlayer.getUUID(), dialog.getText());
    }
}
