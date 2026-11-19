package org.bitwisemadness.infinitedungeoneasynpc.lobby;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

/**
 * One authored NPC slot: the character's identity (the idempotency key
 * EasyNPC persists), the fixed lobby floor it stands on, and the authored
 * preset that realises it. A slot without a preset is a declared placement
 * awaiting its authored content (the sibling issues 2 and 3); the import only
 * acts on slots that have one.
 */
public record LobbySlot(String id, LobbyFloor floor, Optional<ResourceLocation> preset) {

    public LobbySlot {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(floor, "floor");
        preset = preset == null ? Optional.empty() : preset;
    }

    /** Whether the slot has authored content to import. */
    public boolean hasPreset() {
        return preset.isPresent();
    }

    /** The slot's custom identifier: what EasyNPC persists and keys on. */
    public ResourceLocation customIdentifier() {
        return ResourceLocation.fromNamespaceAndPath("infinite_dungeon_easynpc", id);
    }
}
