package com.thevoxelbox.voxelsniper.util;

import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

/**
 * Painting state change handler.
 *
 * @author Piotr
 */
public final class Painter {

	private Painter() {
	}

	/**
	 * The paint method used to scroll or set a painting to a specific type.
	 *
	 * @param player The player executing the method
	 * @param auto Scroll automatically? If false will use 'choice' to try and set the painting
	 * @param back Scroll in reverse?
	 * @param choice Chosen index to set the painting to
	 */
	public static void paint(Player player, boolean auto, boolean back, int choice) {
		Location targetLocation = player.getTargetBlock(null, 4)
			.getLocation();
		Chunk paintingChunk = player.getTargetBlock(null, 4)
			.getLocation()
			.getChunk();
		double bestDistanceMatch = 50.0;
		Painting bestMatch = null;
		for (Entity entity : paintingChunk.getEntities()) {
			if (entity.getType() == EntityType.PAINTING) {
				double distance = targetLocation.distanceSquared(entity.getLocation());
				if (distance <= 4 && distance < bestDistanceMatch) {
					bestDistanceMatch = distance;
					bestMatch = (Painting) entity;
				}
			}
		}
		if (bestMatch != null) {
			if (auto) {
				try {
					int i = bestMatch.getArt()
						.getId() + (back ? -1 : 1);
					Art art = Art.getById(i);
					if (art == null) {
						player.sendMessage(ChatColor.RED + "This is the final painting, try scrolling to the other direction.");
						return;
					}
					bestMatch.setArt(art);
					player.sendMessage(ChatColor.GREEN + "Painting set to ID: " + (i));
				} catch (RuntimeException exception) {
					player.sendMessage(ChatColor.RED + "Oops. Something went wrong.");
				}
			} else {
				Art art = Art.getById(choice);
				if (art == null) {
					player.sendMessage(ChatColor.RED + "Your input was invalid somewhere.");
					return;
				}
				bestMatch.setArt(art);
				player.sendMessage(ChatColor.GREEN + "Painting set to ID: " + choice);
			}
		}
	}
}
