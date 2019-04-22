package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import org.bukkit.Chunk;

/**
 * Regenerates the target chunk.
 *
 * @author Mick
 */
public class RegenerateChunkBrush extends AbstractBrush {


	public RegenerateChunkBrush() {
		super("Chunk Generator 40k");
	}

	private void generateChunk(SnipeData v) {
		Chunk chunk = this.getTargetBlock()
			.getChunk();
		Undo undo = new Undo();
		for (int z = CHUNK_SIZE; z >= 0; z--) {
			for (int x = CHUNK_SIZE; x >= 0; x--) {
				for (int y = this.getWorld()
					.getMaxHeight(); y >= 0; y--) {
					undo.put(chunk.getBlock(x, y, z));
				}
			}
		}
		v.getOwner()
			.storeUndo(undo);
		v.sendMessage("Generate that chunk! " + chunk.getX() + " " + chunk.getZ());
		this.getWorld()
			.regenerateChunk(chunk.getX(), chunk.getZ());
		this.getWorld()
			.refreshChunk(chunk.getX(), chunk.getZ());
	}

	@Override
	public final void arrow(SnipeData snipeData) {
		this.generateChunk(snipeData);
	}

	@Override
	public final void powder(SnipeData snipeData) {
		this.generateChunk(snipeData);
	}

	@Override
	public final void info(Messages messages) {
		messages.brushName(this.getName());
		messages.brushMessage("Tread lightly.");
		messages.brushMessage("This brush will melt your spleen and sell your kidneys.");
	}

	@Override
	public String getPermissionNode() {
		return "voxelsniper.brush.regeneratechunk";
	}
}
