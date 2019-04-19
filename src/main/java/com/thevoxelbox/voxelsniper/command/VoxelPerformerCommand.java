package com.thevoxelbox.voxelsniper.command;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.SniperManager;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.perform.BrushPerformer;
import org.bukkit.entity.Player;

public class VoxelPerformerCommand extends VoxelCommand {

	private VoxelSniperPlugin plugin;

	public VoxelPerformerCommand(VoxelSniperPlugin plugin) {
		super("VoxelPerformer", "p", "voxelsniper.sniper");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(Player sender, String[] args) {
		SniperManager sniperManager = this.plugin.getSniperManager();
		Sniper sniper = sniperManager.getSniperForPlayer(sender);
		SnipeData snipeData = sniper.getSnipeData(sniper.getCurrentToolId());
		try {
			if (args == null || args.length == 0) {
				Brush brush = sniper.getBrush(sniper.getCurrentToolId());
				if (brush instanceof BrushPerformer) {
					((BrushPerformer) brush).parse(new String[] {"m"}, snipeData);
				} else {
					sender.sendMessage("This brush is not a performer brush.");
				}
			} else {
				Brush brush = sniper.getBrush(sniper.getCurrentToolId());
				if (brush instanceof BrushPerformer) {
					((BrushPerformer) brush).parse(args, snipeData);
				} else {
					sender.sendMessage("This brush is not a performer brush.");
				}
			}
			return true;
		} catch (NumberFormatException exception) {
			Logger logger = this.plugin.getLogger();
			logger.log(Level.WARNING, "Command error from " + sender.getName(), exception);
			return true;
		}
	}
}
