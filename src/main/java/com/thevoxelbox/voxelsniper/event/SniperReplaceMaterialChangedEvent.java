package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SniperReplaceMaterialChangedEvent extends SniperMaterialChangedEvent {

	private static final HandlerList handlers = new HandlerList();

	public SniperReplaceMaterialChangedEvent(Sniper sniper, BlockData originalMaterial, BlockData newMaterial, String toolId) {
		super(sniper, originalMaterial, newMaterial, toolId);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
