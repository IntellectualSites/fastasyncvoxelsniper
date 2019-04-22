/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Arrays;
import com.thevoxelbox.voxelsniper.Messages;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.performer.type.MaterialPerformer;
import com.thevoxelbox.voxelsniper.brush.performer.Performer;
import com.thevoxelbox.voxelsniper.brush.performer.Performers;
import com.thevoxelbox.voxelsniper.sniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.sniper.Sniper;

/**
 * @author Voxel
 */
public abstract class AbstractPerformerBrush extends AbstractBrush implements PerformerBrush {

	protected Performer performer = new MaterialPerformer();

	public AbstractPerformerBrush(String name) {
		super(name);
	}

	@Override
	public void parse(String[] args, SnipeData snipeData) {
		String handle = args[0];
		if (Performers.has(handle)) {
			Performer performer = Performers.getPerformer(handle);
			if (performer != null) {
				this.performer = performer;
				Sniper owner = snipeData.getOwner();
				String currentToolId = owner.getCurrentToolId();
				if (currentToolId == null) {
					return;
				}
				Messages messages = snipeData.getMessages();
				info(messages);
				this.performer.info(messages);
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

	@Override
	public void showInfo(Messages messages) {
		this.performer.info(messages);
	}

	@Override
	public void initPerformer(SnipeData snipeData) {
		this.performer.init(snipeData);
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
