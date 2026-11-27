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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.bitwisemadness.infinitedungeoneasynpc.InfiniteDungeonEasyNpc;
import org.bitwisemadness.infinitedungeoneasynpc.lobby.LobbyFloor;
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

    /**
     * The floor mirror matches the upstream lobby contract (infinite-dungeon
     * #131, landed as LobbyFloors in #288): exactly five fixed floors, each
     * with its authoritative role, top-anchored.
     */
    @GameTest(template = "empty")
    public static void lobby_floor_mirror_matches_the_upstream_contract(GameTestHelper helper) {
        LobbyFloor[] floors = LobbyFloor.values();
        helper.assertTrue(floors.length == 5,
                "the mirror must carry exactly five fixed floors, got " + floors.length);
        for (int i = 0; i < floors.length; i++) {
            helper.assertTrue(floors[i].index() == i,
                    floors[i] + " must be floor " + i + ", got " + floors[i].index());
        }
        helper.assertTrue(floors[0] == LobbyFloor.GUILD_RECEPTION
                        && floors[1] == LobbyFloor.GUILD_SHOPS
                        && floors[2] == LobbyFloor.SPECIALISED_SHOPS
                        && floors[3] == LobbyFloor.ADVANCED_SHOPS
                        && floors[4] == LobbyFloor.WAYSTONE_ROOM,
                "the mirror's floor roles must match the upstream contract");
        helper.succeed();
    }

    /** The slot table places every NPC on its authoritative floor, preset or not. */
    @GameTest(template = "empty")
    public static void slot_table_places_each_npc_at_its_authoritative_floor(GameTestHelper helper) {
        helper.assertTrue(LobbyNpcs.RECEPTIONIST.floor() == LobbyFloor.GUILD_RECEPTION
                        && LobbyNpcs.RECEPTIONIST.hasPreset(),
                "the receptionist stands on floor 0 (guild reception) with authored content");
        helper.assertTrue(LobbyNpcs.GUILD_MASTER.floor() == LobbyFloor.GUILD_RECEPTION
                        && LobbyNpcs.GUILD_MASTER.hasPreset(),
                "the guild master (issue 2) is on floor 0's office with authored content");
        helper.assertTrue(LobbyNpcs.ADVENTURER.floor() == LobbyFloor.WAYSTONE_ROOM
                        && LobbyNpcs.ADVENTURER.hasPreset(),
                "the adventurers (issue 3) are on floor 4's waystone room with authored content");
        helper.assertTrue(LobbyNpcs.ESSENTIALS_SHOPKEEPER.floor() == LobbyFloor.GUILD_SHOPS
                        && !LobbyNpcs.ESSENTIALS_SHOPKEEPER.hasPreset(),
                "the essentials shopkeeper is on floor 1, declared pending its authored content");
        helper.assertTrue(LobbyNpcs.SPECIALISED_SHOPKEEPER.floor() == LobbyFloor.SPECIALISED_SHOPS
                        && !LobbyNpcs.SPECIALISED_SHOPKEEPER.hasPreset(),
                "the specialised shopkeeper is on floor 2, declared pending its authored content");
        helper.assertTrue(LobbyNpcs.ADVANCED_SHOPKEEPER.floor() == LobbyFloor.ADVANCED_SHOPS
                        && !LobbyNpcs.ADVANCED_SHOPKEEPER.hasPreset(),
                "the advanced shopkeeper is on floor 3, declared pending its authored content");
        helper.succeed();
    }

    /**
     * The receptionist carries authored dialog content using EasyNPC's
     * expandable variables (@initiator for the player, @npc for the character)
     * so the stored template is shared and cacheable - the per-player
     * expansion happens at display, never in the content. The live dialog
     * screen is an in-game check; the shipped structure is pinned here.
     */
    @GameTest(template = "empty")
    public static void receptionist_preset_carries_the_registration_dialog(GameTestHelper helper) {
        CompoundTag root = loadPreset(helper, LobbyNpcs.RECEPTIONIST_PRESET);
        CompoundTag dialogData = root.getCompound("data").getCompound("DialogData");
        ListTag dialogs = dialogData.getList("DialogDataSet", Tag.TAG_COMPOUND);
        helper.assertTrue(!dialogs.isEmpty(),
                "the receptionist must carry authored dialog content");
        boolean welcome = false;
        boolean usesInitiator = false;
        boolean usesNpc = false;
        for (Tag dialog : dialogs) {
            CompoundTag entry = (CompoundTag) dialog;
            ListTag texts = entry.getList("Texts", Tag.TAG_COMPOUND);
            if ("welcome".equals(entry.getString("Name"))) {
                welcome = !texts.isEmpty();
            }
            for (Tag text : texts) {
                String line = ((CompoundTag) text).getString("Text");
                usesInitiator |= line.contains("@initiator");
                usesNpc |= line.contains("@npc");
            }
        }
        helper.assertTrue(welcome,
                "the welcome dialog must exist and carry a text (registration and tasks)");
        helper.assertTrue(usesInitiator,
                "the dialog must address the player via @initiator, not a hardcoded name");
        helper.assertTrue(usesNpc,
                "the dialog must reference the character via @npc, not a hardcoded name");
        helper.succeed();
    }

    /**
     * The guild master carries the same contract as the receptionist: dialog
     * on interaction, trading stripped (characters never sell or buy), and
     * authored dialog content with a welcome entry.
     */
    @GameTest(template = "empty")
    public static void guild_master_preset_carries_the_ruling(GameTestHelper helper) {
        CompoundTag root = loadPreset(helper, LobbyNpcs.GUILD_MASTER_PRESET);
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
        ListTag dialogs = root.getCompound("data")
                .getCompound("DialogData")
                .getList("DialogDataSet", Tag.TAG_COMPOUND);
        boolean welcome = false;
        for (Tag dialog : dialogs) {
            CompoundTag entry = (CompoundTag) dialog;
            if ("welcome".equals(entry.getString("Name"))) {
                welcome = !entry.getList("Texts", Tag.TAG_COMPOUND).isEmpty();
            }
        }
        helper.assertTrue(welcome,
                "the guild master's welcome dialog must exist and carry a text");
        helper.succeed();
    }

    /** The adventurer carries the same contract: dialog on interaction, trading stripped, authored welcome. */
    @GameTest(template = "empty")
    public static void adventurer_preset_carries_the_ruling(GameTestHelper helper) {
        CompoundTag root = loadPreset(helper, LobbyNpcs.ADVENTURER_PRESET);
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
        ListTag dialogs = root.getCompound("data")
                .getCompound("DialogData")
                .getList("DialogDataSet", Tag.TAG_COMPOUND);
        boolean welcome = false;
        for (Tag dialog : dialogs) {
            CompoundTag entry = (CompoundTag) dialog;
            if ("welcome".equals(entry.getString("Name"))) {
                welcome = !entry.getList("Texts", Tag.TAG_COMPOUND).isEmpty();
            }
        }
        helper.assertTrue(welcome,
                "the adventurer's welcome dialog must exist and carry a text");
        helper.succeed();
    }

    /**
     * The authored positions sit on the authoritative floors of the committed
     * lobby geometry (infinite-dungeon #300, the 7x21x7 building with 4-block
     * floors): the receptionist and the guild master on floor 0, the
     * adventurer on floor 4. Pinned here so the authored content cannot drift
     * off the upstream construction.
     */
    @GameTest(template = "empty")
    public static void authored_positions_sit_on_the_authoritative_floor(GameTestHelper helper) {
        helper.assertTrue(floorOf(LobbyNpcs.RECEPTIONIST_POSITION) == LobbyFloor.GUILD_RECEPTION.index(),
                "the receptionist must stand on floor 0, got " + LobbyNpcs.RECEPTIONIST_POSITION);
        helper.assertTrue(floorOf(LobbyNpcs.GUILD_MASTER_POSITION) == LobbyFloor.GUILD_RECEPTION.index(),
                "the guild master must stand on floor 0, got " + LobbyNpcs.GUILD_MASTER_POSITION);
        helper.assertTrue(floorOf(LobbyNpcs.ADVENTURER_POSITION) == LobbyFloor.WAYSTONE_ROOM.index(),
                "the adventurer must stand on floor 4, got " + LobbyNpcs.ADVENTURER_POSITION);
        helper.assertTrue(floorOf(LobbyNpcs.ESSENTIALS_SHOPKEEPER_POSITION) == LobbyFloor.GUILD_SHOPS.index(),
                "the essentials shopkeeper must stand on floor 1, got " + LobbyNpcs.ESSENTIALS_SHOPKEEPER_POSITION);
        helper.assertTrue(floorOf(LobbyNpcs.SPECIALISED_SHOPKEEPER_POSITION) == LobbyFloor.SPECIALISED_SHOPS.index(),
                "the specialised shopkeeper must stand on floor 2, got " + LobbyNpcs.SPECIALISED_SHOPKEEPER_POSITION);
        helper.assertTrue(floorOf(LobbyNpcs.ADVANCED_SHOPKEEPER_POSITION) == LobbyFloor.ADVANCED_SHOPS.index(),
                "the advanced shopkeeper must stand on floor 3, got " + LobbyNpcs.ADVANCED_SHOPKEEPER_POSITION);
        helper.succeed();
    }

    private static int floorOf(Vec3 position) {
        return (int) position.y / 4; // the #300 lobby geometry: 4 blocks per floor
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
