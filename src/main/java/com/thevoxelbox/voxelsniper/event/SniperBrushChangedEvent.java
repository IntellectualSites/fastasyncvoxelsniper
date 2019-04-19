package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class SniperBrushChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Sniper sniper;
	private final Brush originalBrush;
	private final Brush newBrush;
	private final String toolId;

	public SniperBrushChangedEvent(Sniper sniper, String toolId, Brush originalBrush, Brush newBrush) {
		this.sniper = sniper;
		this.originalBrush = originalBrush;
		this.newBrush = newBrush;
		this.toolId = toolId;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Brush getOriginalBrush() {
		return this.originalBrush;
	}

	public Brush getNewBrush() {
		return this.newBrush;
	}

	public Sniper getSniper() {
		return this.sniper;
	}

	public String getToolId() {
		return this.toolId;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
