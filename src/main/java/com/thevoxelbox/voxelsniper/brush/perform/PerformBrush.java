/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;
import com.thevoxelbox.voxelsniper.Sniper;
import com.thevoxelbox.voxelsniper.brush.AbstractBrush;
import com.thevoxelbox.voxelsniper.event.SniperBrushChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * @author Voxel
 */
public abstract class PerformBrush extends AbstractBrush implements BrushPerformer {

	protected Performer current = new MaterialPerformer();

	public PerformBrush(String name) {
		super(name);
	}

	public Performer getCurrentPerformer() {
		return this.current;
	}

	@Override
	public void parse(String[] args, SnipeData snipeData) {
		String handle = args[0];
		if (Performers.has(handle)) {
			Performer performer = Performers.getPerformer(handle);
			if (performer != null) {
				this.current = performer;
				Sniper owner = snipeData.getOwner();
				String currentToolId = owner.getCurrentToolId();
				if (currentToolId == null) {
					return;
				}
				SniperBrushChangedEvent event = new SniperBrushChangedEvent(owner, this, this, currentToolId);
				PluginManager pluginManager = Bukkit.getPluginManager();
				pluginManager.callEvent(event);
				info(snipeData.getMessage());
				this.current.info(snipeData.getMessage());
				if (args.length > 1) {
					String[] additionalArguments = Arrays.copyOfRange(args, 1, args.length);
					parameters(hackTheArray(additionalArguments), snipeData);
				}
			} else {
				parameters(hackTheArray(args), snipeData);
			}
		} else {
			parameters(hackTheArray(args), snipeData);
		}
	}

	/**
	 * Padds an empty String to the front of the array.
	 *
	 * @param args Array to pad empty string in front of
	 * @return padded array
	 */
	private String[] hackTheArray(String[] args) {
		String[] returnValue = new String[args.length + 1];
		for (int i = 0, argsLength = args.length; i < argsLength; i++) {
			String arg = args[i];
			returnValue[i + 1] = arg;
		}
		return returnValue;
	}

	public void initPerformer(SnipeData snipeData) {
		this.current.init(snipeData);
		this.current.setUndo();
	}

	@Override
	public void showInfo(Message message) {
		this.current.info(message);
	}
}
