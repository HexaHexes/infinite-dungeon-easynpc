package org.bitwisemadness.infinitedungeoneasynpc.gametest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.bitwisemadness.infinitedungeoneasynpc.InfiniteDungeonEasyNpc;
import org.bitwisemadness.infinitedungeoneasynpc.lobby.LobbyNpcs;

/**
 * The lobby NPC contract (issue 1): what the harness CAN prove headless. The
 * eligibility gate must keep the module inert without EasyNPC, and the
 * authored receptionist preset must carry the trading ruling - Open Default
 * Dialog on interaction, Open Trading stripped - so the character is flavour
 * and dialog only and every transaction stays a Lightman's Currency trader.
 * The live import itself needs EasyNPC and an in-game check (agents/REPO.md).
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

    /**
     * Without EasyNPC the lobby stays empty: the gate must refuse before any
     * EasyNPC class can load (the harness runs with EasyNPC absent).
     */
    @GameTest(template = "empty")
    public static void lobby_npcs_inert_without_easynpc(GameTestHelper helper) {
        helper.assertTrue(!LobbyNpcs.isEligible(),
                "the harness has no EasyNPC, so the module must be inert");
        helper.succeed();
    }

    /**
     * The authored receptionist preset carries the ruling: OPEN_DEFAULT_DIALOG
     * on interaction and NO OPEN_TRADING_SCREEN - characters never sell or buy.
     */
    @GameTest(template = "empty")
    public static void receptionist_preset_carries_the_trading_ruling(GameTestHelper helper) {
        CompoundTag root = loadPreset(helper, LobbyNpcs.RECEPTIONIST_PRESET);
        helper.assertTrue(root.contains("data", Tag.TAG_COMPOUND),
                "the preset must wrap its entity data under 'data'");
        ListTag interactions = root.getCompound("data")
                .getCompound("ActionData")
                .getCompound("ActionEventSet")
                .getList("ON_INTERACTION", Tag.TAG_COMPOUND);
        List<String> types = new ArrayList<>();
        for (Tag action : interactions) {
            types.add(((CompoundTag) action).getString("Type"));
        }
        helper.assertTrue(types.contains("OPEN_DEFAULT_DIALOG"),
                "interaction must open the default dialog, got " + types);
        helper.assertTrue(!types.contains("OPEN_TRADING_SCREEN"),
                "the trading action must be stripped (Lightman's Currency ruling), got " + types);
        helper.succeed();
    }

    private static CompoundTag loadPreset(GameTestHelper helper, ResourceLocation location) {
        var resource = helper.getLevel().getServer().getResourceManager().getResource(location);
        helper.assertTrue(resource.isPresent(), "the preset resource must ship: " + location);
        try (InputStream input = resource.get().open()) {
            String snbt = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return TagParser.parseTag(snbt);
        } catch (IOException | com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            throw new AssertionError("the shipped preset must parse as SNBT: " + location, exception);
        }
    }
}
