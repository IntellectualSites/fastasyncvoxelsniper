package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SniperMaterialChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Sniper sniper;
	private BlockData originalMaterial;
	private BlockData newMaterial;
	private String toolId;

	public SniperMaterialChangedEvent(Sniper sniper, BlockData originalMaterial, BlockData newMaterial, String toolId) {
		this.sniper = sniper;
		this.originalMaterial = originalMaterial;
		this.newMaterial = newMaterial;
		this.toolId = toolId;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Sniper getSniper() {
		return this.sniper;
	}

	public BlockData getOriginalMaterial() {
		return this.originalMaterial;
	}

	public BlockData getNewMaterial() {
		return this.newMaterial;
	}

	public String getToolId() {
		return this.toolId;
	}
}
