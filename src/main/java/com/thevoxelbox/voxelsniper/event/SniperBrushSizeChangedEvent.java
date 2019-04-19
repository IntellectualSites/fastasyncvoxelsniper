package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SniperBrushSizeChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Sniper sniper;
	private int originalSize;
	private int newSize;
	private String toolId;

	public SniperBrushSizeChangedEvent(Sniper sniper, int originalSize, int newSize, String toolId) {
		this.sniper = sniper;
		this.originalSize = originalSize;
		this.newSize = newSize;
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

	public int getOriginalSize() {
		return this.originalSize;
	}

	public int getNewSize() {
		return this.newSize;
	}

	public String getToolId() {
		return this.toolId;
	}
}
