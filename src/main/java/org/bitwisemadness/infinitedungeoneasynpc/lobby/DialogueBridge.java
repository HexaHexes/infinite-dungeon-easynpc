package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import java.util.List;
import java.util.UUID;
import org.bitwisemadness.infinitedungeonapi.dialogue.DialogueMessage;
import org.bitwisemadness.infinitedungeonapi.dialogue.DialogueSession;
import org.bitwisemadness.infinitedungeonapi.dialogue.DialogueSessions;

/**
 * Commits an NPC's spoken line to the API's dialogue session port - the
 * premise of issue #117, made concrete: when an EasyNPC dialog opens, the
 * player's session gains the NPC's line as an assistant message, so the
 * research's conversational chain (which loads the same store) sees the NPC's
 * dialogue in its session context.
 *
 * <p>Pure by construction: only the API's plain dialogue types, no EasyNPC and
 * no world state, so the routing is JUnit-testable. The EasyNPC event wiring
 * that calls this lives in {@link EasyNpcDialogBridge}, gated behind
 * eligibility like every EasyNPC touch.</p>
 */
public final class DialogueBridge {

    /** The role the NPC's spoken line takes in the shared session. */
    public static final String NPC_ROLE = "assistant";

    private DialogueBridge() {
    }

    /**
     * Appends the NPC's line to the player's session (load-or-fresh, append,
     * save), mirroring the conversational service's own session handling.
     */
    public static void recordNpcLine(UUID playerId, String content) {
        DialogueSession session = DialogueSessions.load(playerId)
                .filter(s -> !s.isExpired(System.nanoTime()))
                .orElseGet(() -> DialogueSession.withTtl(
                        List.of(), DialogueSessions.DEFAULT_TTL_MILLIS));
        DialogueSessions.save(playerId, session.append(new DialogueMessage(NPC_ROLE, content)));
    }
}
