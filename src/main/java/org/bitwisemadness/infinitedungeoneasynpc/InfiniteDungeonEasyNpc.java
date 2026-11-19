package org.bitwisemadness.infinitedungeoneasynpc;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.bitwisemadness.infinitedungeoneasynpc.lobby.LobbyNpcs;
import org.slf4j.Logger;

/**
 * Module entry point: Infinite Dungeon's optional EasyNPC content.
 *
 * <p>Characters only (issue 1 and siblings): the receptionist, guild master
 * and adventurers that populate the fixed lobby are authored content imported
 * when EasyNPC is present - every transaction stays a Lightman's Currency
 * trader, so this module never sells or buys. The lobby degrades gracefully
 * without EasyNPC: empty rooms, no NPCs, Infinite Dungeon unaffected.</p>
 */
@Mod(InfiniteDungeonEasyNpc.MOD_ID)
public final class InfiniteDungeonEasyNpc {

    public static final String MOD_ID = "infinite_dungeon_easynpc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InfiniteDungeonEasyNpc(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(LobbyNpcs::onServerStarted);
        LOGGER.info("Infinite Dungeon EasyNPC content module initialized");
    }
}
