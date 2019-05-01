package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.Performers;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialPerformer;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

public abstract class AbstractPerformerBrush extends AbstractBrush implements PerformerBrush {

	protected Performer performer = new MaterialPerformer();

	@Override
	public void handlePerformerCommand(String[] parameters, Snipe snipe) {
		String handle = parameters[0];
		if (Performers.has(handle)) {
			Performer performer = Performers.getPerformer(handle);
			if (performer != null) {
				this.performer = performer;
				sendInfo(snipe);
				this.performer.info(new Messages(snipe));
				if (parameters.length > 1) {
					String[] additionalArguments = Arrays.copyOfRange(parameters, 1, parameters.length);
					super.handleCommand(hackTheArray(additionalArguments), snipe);
				}
			} else {
				super.handleCommand(hackTheArray(parameters), snipe);
			}
		} else {
			super.handleCommand(hackTheArray(parameters), snipe);
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

	@Override
	public void sendPerformerInfo(Snipe snipe) {
		this.performer.info(new Messages(snipe));
	}

	@Override
	public void initialize(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.performer.init(toolkitProperties);
		this.performer.setUndo();
	}

	public Performer getPerformer() {
		return this.performer;
	}
}
