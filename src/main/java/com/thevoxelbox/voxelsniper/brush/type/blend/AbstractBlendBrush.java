package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class AbstractBlendBrush extends AbstractBrush {

	protected static final MaterialSet BLOCKS = MaterialSet.builder()
		.filtered(Material::isBlock)
		.build();

	protected boolean excludeAir = true;
	protected boolean excludeWater = true;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int i = 1; i < parameters.length; ++i) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("water")) {
				this.excludeWater = !this.excludeWater;
				messenger.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		this.excludeAir = false;
		blend(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		this.excludeAir = true;
		blend(snipe);
	}

	public abstract void blend(Snipe snipe);

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.blockTypeMessage()
			.message(ChatColor.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"))
			.send();
	}

	public boolean isExcludeAir() {
		return this.excludeAir;
	}

	public void setExcludeAir(boolean excludeAir) {
		this.excludeAir = excludeAir;
	}

	public boolean isExcludeWater() {
		return this.excludeWater;
	}

	public void setExcludeWater(boolean excludeWater) {
		this.excludeWater = excludeWater;
	}
}
