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

	protected Performer current = new MaterialPerformer();

	public AbstractPerformerBrush(String name) {
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
				Messages messages = snipeData.getMessages();
				info(messages);
				this.current.info(messages);
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

	@Override
	public void initPerformer(SnipeData snipeData) {
		this.current.init(snipeData);
		this.current.setUndo();
	}

	@Override
	public void showInfo(Messages messages) {
		this.current.info(messages);
	}
}
