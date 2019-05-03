package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class AbstractBlendBrush extends AbstractBrush {

	@Deprecated
	protected static final MaterialSet BLOCKS = MaterialSet.builder()
		.filtered(Material::isBlock)
		.build();

	private boolean airExcluded = true;
	private boolean waterExcluded = true;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int index = 1; index < parameters.length; ++index) {
			String parameter = parameters[index];
			if (parameter.equalsIgnoreCase("water")) {
				this.waterExcluded = !this.waterExcluded;
				messenger.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.waterExcluded ? "exclude" : "include"));
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		this.airExcluded = false;
		blend(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		this.airExcluded = true;
		blend(snipe);
	}

	public abstract void blend(Snipe snipe);

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.brushSizeMessage()
			.blockTypeMessage()
			.message(ChatColor.BLUE + "Water Mode: " + (this.waterExcluded ? "exclude" : "include"))
			.send();
	}

	public boolean isAirExcluded() {
		return this.airExcluded;
	}

	public boolean isWaterExcluded() {
		return this.waterExcluded;
	}
}
