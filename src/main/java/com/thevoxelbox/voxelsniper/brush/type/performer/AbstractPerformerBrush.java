/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.performer.Performer;
import com.thevoxelbox.voxelsniper.brush.performer.Performers;
import com.thevoxelbox.voxelsniper.brush.performer.type.material.MaterialPerformer;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

/**
 * @author Voxel
 */
public abstract class AbstractPerformerBrush extends AbstractBrush implements PerformerBrush {

	protected Performer performer = new MaterialPerformer();

	public AbstractPerformerBrush(String name) {
		super(name);
	}

	@Override
	public void parse(String[] args, ToolkitProperties toolkitProperties) {
		String handle = args[0];
		if (Performers.has(handle)) {
			Performer performer = Performers.getPerformer(handle);
			if (performer != null) {
				this.performer = performer;
				Messages messages = toolkitProperties.getMessages();
				info(messages);
				this.performer.info(messages);
				if (args.length > 1) {
					String[] additionalArguments = Arrays.copyOfRange(args, 1, args.length);
					parameters(hackTheArray(additionalArguments), toolkitProperties);
				}
			} else {
				parameters(hackTheArray(args), toolkitProperties);
			}
		} else {
			parameters(hackTheArray(args), toolkitProperties);
		}
	}

	@Override
	public void showInfo(Messages messages) {
		this.performer.info(messages);
	}

	@Override
	public void initPerformer(ToolkitProperties toolkitProperties) {
		this.performer.init(toolkitProperties);
		this.performer.setUndo();
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

	public Performer getPerformer() {
		return this.performer;
	}
}
