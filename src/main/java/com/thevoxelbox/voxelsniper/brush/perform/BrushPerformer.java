/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush.perform;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.SnipeData;

/**
 * @author Voxel
 */
public interface BrushPerformer {

	void parse(String[] args, SnipeData snipeData);

	void showInfo(Message message);
}
