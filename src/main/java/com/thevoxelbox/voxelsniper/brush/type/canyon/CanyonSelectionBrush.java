package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;

public class CanyonSelectionBrush extends CanyonBrush {

	private boolean first = true;
	private int fx;
	private int fz;

	@Override
	public void handleArrowAction(Snipe snipe) {
		execute(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		execute(snipe);
	}

	private void execute(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		Chunk chunk = getTargetBlock().getChunk();
		if (this.first) {
			this.fx = chunk.getX();
			this.fz = chunk.getZ();
			messenger.sendMessage(ChatColor.YELLOW + "First point selected!");
		} else {
			messenger.sendMessage(ChatColor.YELLOW + "Second point selected!");
			selection(Math.min(this.fx, chunk.getX()), Math.min(this.fz, chunk.getZ()), Math.max(this.fx, chunk.getX()), Math.max(this.fz, chunk.getZ()), snipe);
		}
		this.first = !this.first;
	}

	private void selection(int lowX, int lowZ, int highX, int highZ, Snipe snipe) {
		Undo undo = new Undo();
		for (int x = lowX; x <= highX; x++) {
			for (int z = lowZ; z <= highZ; z++) {
				canyon(getWorld().getChunkAt(x, z), undo);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(ChatColor.GREEN + "Shift Level set to " + this.getYLevel())
			.send();
	}
}
