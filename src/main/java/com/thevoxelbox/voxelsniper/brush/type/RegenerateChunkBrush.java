package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

/**
 * Regenerates the target chunk.
 */
public class RegenerateChunkBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		generateChunk(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		generateChunk(snipe);
	}

	@SuppressWarnings("deprecation")
	private void generateChunk(Snipe snipe) {
		BlockVector3 targetBlock = getTargetBlock();
		Undo undo = new Undo();
		EditSession editSession = getEditSession();
		int chunkX = targetBlock.getX() >> 4;
		int chunkZ = targetBlock.getZ() >> 4;
		int blockX = chunkX << 4;
		int blockZ = chunkZ << 4;
		for (int z = CHUNK_SIZE; z >= 0; z--) {
			for (int x = CHUNK_SIZE; x >= 0; x--) {
				for (int y = editSession.getMaxY() + 1; y >= 0; y--) {
					undo.put(getBlock(blockX + x, y, blockZ + z));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage("Generate that chunk! " + chunkX + " " + chunkZ);
		regenerateChunk(chunkX, chunkZ);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Tread lightly.");
		messenger.sendMessage(ChatColor.LIGHT_PURPLE + "This brush will melt your spleen and sell your kidneys.");
	}
}
