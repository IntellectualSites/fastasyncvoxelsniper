/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.sniper.toolkit.Messages;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

/**
 * @author Voxel
 */
public interface PerformerBrush {

	void parse(String[] args, ToolkitProperties toolkitProperties);

	void showInfo(Messages messages);

	void initPerformer(ToolkitProperties toolkitProperties);
}
