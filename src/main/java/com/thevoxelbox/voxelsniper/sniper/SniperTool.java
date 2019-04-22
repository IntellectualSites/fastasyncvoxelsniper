package com.thevoxelbox.voxelsniper.sniper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.type.performer.SnipeBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeAction;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class SniperTool {

	private Map<Material, SnipeAction> actionTools = new HashMap<>();
	private Map<Class<? extends Brush>, Brush> brushes = new HashMap<>();
	private Class<? extends Brush> currentBrush;
	private Class<? extends Brush> previousBrush;
	private SnipeData snipeData;
	private Messages messagesHelper;

	SniperTool(Sniper owner) {
		this(SnipeBrush.class, new SnipeData(owner));
	}

	private SniperTool(Class<? extends Brush> currentBrush, SnipeData snipeData) {
		this.snipeData = snipeData;
		this.messagesHelper = new Messages(snipeData);
		snipeData.setMessages(this.messagesHelper);
		Brush newBrushInstance = createBrushInstance(currentBrush);
		Sniper owner = snipeData.getOwner();
		Player player = owner.getPlayer();
		if (player != null && player.hasPermission(newBrushInstance.getPermissionNode())) {
			this.brushes.put(currentBrush, newBrushInstance);
			this.currentBrush = currentBrush;
		}
	}

	public boolean hasToolAssigned(Material material) {
		return this.actionTools.containsKey(material);
	}

	public SnipeAction getActionAssigned(Material itemInHand) {
		return this.actionTools.get(itemInHand);
	}

	public void assignAction(Material itemInHand, SnipeAction action) {
		this.actionTools.put(itemInHand, action);
	}

	public void unassignAction(Material itemInHand) {
		this.actionTools.remove(itemInHand);
	}

	public Map<Material, SnipeAction> getActionTools() {
		return Map.copyOf(this.actionTools);
	}

	public SnipeData getSnipeData() {
		return this.snipeData;
	}

	public Messages getMessagesHelper() {
		return this.messagesHelper;
	}

	@Override
	public String toString() {
		return "SniperTool{" + "actionTools=" + this.actionTools + ", brushes=" + this.brushes + ", currentBrush=" + this.currentBrush + ", previousBrush=" + this.previousBrush + ", snipeData=" + this.snipeData + ", messagesHelper=" + this.messagesHelper + "}";
	}

	@Nullable
	public Brush getCurrentBrush() {
		if (this.currentBrush == null) {
			return null;
		}
		return this.brushes.get(this.currentBrush);
	}

	@Nullable
	public Brush setCurrentBrush(Class<? extends Brush> brush) {
		Brush brushInstance = this.brushes.get(brush);
		Sniper owner = this.snipeData.getOwner();
		Player player = owner.getPlayer();
		if (brushInstance == null) {
			brushInstance = createBrushInstance(brush);
			if (player != null && player.hasPermission(brushInstance.getPermissionNode())) {
				this.brushes.put(brush, brushInstance);
				this.previousBrush = this.currentBrush;
				this.currentBrush = brush;
				return brushInstance;
			}
		}
		if (player != null && player.hasPermission(brushInstance.getPermissionNode())) {
			this.previousBrush = this.currentBrush;
			this.currentBrush = brush;
			return brushInstance;
		}
		return null;
	}

	@Nullable
	public Brush previousBrush() {
		if (this.previousBrush == null) {
			return null;
		}
		return setCurrentBrush(this.previousBrush);
	}

	private Brush createBrushInstance(Class<? extends Brush> brush) {
		try {
			Constructor<? extends Brush> constructor = brush.getConstructor();
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
			throw new RuntimeException(exception);
		}
	}
}
