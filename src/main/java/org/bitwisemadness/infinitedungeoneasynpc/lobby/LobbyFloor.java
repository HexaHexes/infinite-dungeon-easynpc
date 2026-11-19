package org.bitwisemadness.infinitedungeoneasynpc.lobby;

/**
 * The fixed lobby floor a slot stands on. Mirrors the infinite-dungeon lobby
 * contract (issue #131, landed as {@code LobbyFloors} in infinite-dungeon
 * #288): the first layer is of fixed construction with exactly five floors,
 * each with one authoritative role. Mirrored here as data - not referenced as
 * a class - so this module stays decoupled from the optional infinite-dungeon
 * artifact; the mirror is pinned to the upstream contract by the GameTest
 * suite, and the mirror's maintenance note lives in agents/REPO.md.
 */
public enum LobbyFloor {

    /** Floor 0: adventurer registration and task handling (infinite-dungeon #138). */
    GUILD_RECEPTION(0),

    /** Floor 1: guild-backed essentials shops and buy-back (infinite-dungeon #139). */
    GUILD_SHOPS(1),

    /** Floor 2: specialised shops (infinite-dungeon #140). */
    SPECIALISED_SHOPS(2),

    /** Floor 3: advanced specialised shops (infinite-dungeon #140). */
    ADVANCED_SHOPS(3),

    /** Floor 4: the waystone and respawn-arrival room (infinite-dungeon #141). */
    WAYSTONE_ROOM(4);

    private final int index;

    LobbyFloor(int index) {
        this.index = index;
    }

    /** The floor's index in the fixed lobby, top-anchored like every band. */
    public int index() {
        return index;
    }
}
