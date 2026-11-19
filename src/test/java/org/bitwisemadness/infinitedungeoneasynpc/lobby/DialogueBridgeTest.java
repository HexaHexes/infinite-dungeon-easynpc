package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.bitwisemadness.infinitedungeonapi.dialogue.DialogueMessage;
import org.bitwisemadness.infinitedungeonapi.dialogue.DialogueSessions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** The dialogue bridge: an NPC line becomes an assistant message in the player's session. */
class DialogueBridgeTest {

    @AfterEach
    void resetSessions() {
        DialogueSessions.reset();
    }

    @Test
    void freshPlayerSessionGainsTheNpcLine() {
        UUID player = UUID.randomUUID();
        DialogueBridge.recordNpcLine(player, "Welcome to the guild!");
        List<DialogueMessage> messages =
                DialogueSessions.load(player).orElseThrow().messages();
        assertEquals(1, messages.size(), "the fresh session must gain exactly one message");
        assertEquals("assistant", messages.get(0).role(),
                "the NPC's line is the assistant role in the shared session");
        assertEquals("Welcome to the guild!", messages.get(0).content());
    }

    @Test
    void subsequentLinesAppendToTheExistingSession() {
        UUID player = UUID.randomUUID();
        DialogueBridge.recordNpcLine(player, "Welcome to the guild!");
        DialogueBridge.recordNpcLine(player, "Safe roads, and mind the depths.");
        List<DialogueMessage> messages =
                DialogueSessions.load(player).orElseThrow().messages();
        assertEquals(2, messages.size(), "a second line must append, never replace");
        assertEquals("Safe roads, and mind the depths.",
                messages.get(1).content(), "the newest line is last");
    }

    @Test
    void playerWithNoSessionIsTreatedAsFresh() {
        UUID player = UUID.randomUUID();
        assertTrue(DialogueSessions.load(player).isEmpty(),
                "no recorded line means no session - the research chain reads that as a fresh turn");
    }
}
