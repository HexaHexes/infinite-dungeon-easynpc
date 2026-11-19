package org.bitwisemadness.infinitedungeoneasynpc.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.bitwisemadness.infinitedungeoneasynpc.InfiniteDungeonEasyNpc;

/**
 * Smoke test: proves the module loads and the GameTest harness runs.
 *
 * <p>Keep the {@code expectedTests} count in {@code build.gradle} in sync with
 * the number of {@code @GameTest} methods here, or the harness fails the
 * run.</p>
 */
@GameTestHolder(InfiniteDungeonEasyNpc.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LobbyNpcGameTests {

    private LobbyNpcGameTests() {
    }

    /** The module is loaded and the empty structure template resolves. */
    @GameTest(template = "empty")
    public static void mod_loads(GameTestHelper helper) {
        helper.succeed();
    }
}
