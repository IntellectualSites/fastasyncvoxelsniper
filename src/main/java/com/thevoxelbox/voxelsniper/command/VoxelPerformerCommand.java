package com.thevoxelbox.voxelsniper.command;

import java.util.logging.Level;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.api.command.VoxelCommand;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.BrushPerformer;
import org.bukkit.entity.Player;

public class VoxelPerformerCommand extends VoxelCommand {

	public VoxelPerformerCommand(VoxelSniper plugin) {
		super("VoxelPerformer", plugin);
		setIdentifier("p");
		setPermission("voxelsniper.sniper");
	}

	@Override
	public boolean onCommand(Player player, String[] args) {
		Sniper sniper = this.plugin.getSniperManager()
			.getSniperForPlayer(player);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		try {
			if (args == null || args.length == 0) {
				Brush brush = sniper.getBrush(sniper.getCurrentToolId());
				if (brush instanceof BrushPerformer) {
					((BrushPerformer) brush).parse(new String[] {"m"}, snipeData);
				} else {
					player.sendMessage("This brush is not a performer brush.");
				}
			} else {
				Brush brush = sniper.getBrush(sniper.getCurrentToolId());
				if (brush instanceof BrushPerformer) {
					((BrushPerformer) brush).parse(args, snipeData);
				} else {
					player.sendMessage("This brush is not a performer brush.");
				}
			}
			return true;
		} catch (NumberFormatException exception) {
			this.plugin.getLogger()
				.log(Level.WARNING, "Command error from " + player.getName(), exception);
			return true;
		}
	}
}
