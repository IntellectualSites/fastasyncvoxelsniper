/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.perms;

import com.voxelplugineering.voxelsniper.api.entity.living.Player;
import com.voxelplugineering.voxelsniper.api.permissions.PermissionProxy;
import com.voxelplugineering.voxelsniper.entity.living.BukkitPlayer;

/**
 * PermissionProxy for super perms of bukkit.
 */
public class SuperPermsPermissionProxy implements PermissionProxy
{

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOp(Player sniper)
    {
        return sniper instanceof BukkitPlayer && ((BukkitPlayer) sniper).getThis().isOp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPermission(Player sniper, String permission)
    {
        return sniper instanceof BukkitPlayer && ((BukkitPlayer) sniper).getThis().hasPermission(permission);
    }

}