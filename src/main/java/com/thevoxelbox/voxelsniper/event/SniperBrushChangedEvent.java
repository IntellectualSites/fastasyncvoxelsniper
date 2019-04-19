package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.Brush;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SniperBrushChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Sniper sniper;
	private Brush originalBrush;
	private Brush newBrush;
	private String toolId;

	public SniperBrushChangedEvent(Sniper sniper, Brush originalBrush, Brush newBrush, String toolId) {
		this.sniper = sniper;
		this.originalBrush = originalBrush;
		this.newBrush = newBrush;
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

	public Brush getOriginalBrush() {
		return this.originalBrush;
	}

	public Brush getNewBrush() {
		return this.newBrush;
	}

	public String getToolId() {
		return this.toolId;
	}
}
