/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.Message;
import com.thevoxelbox.voxelsniper.sniper.SnipeData;

/**
 * @author Voxel
 */
public interface PerformerBrush {

	void parse(String[] args, SnipeData snipeData);

	void showInfo(Message message);
}
