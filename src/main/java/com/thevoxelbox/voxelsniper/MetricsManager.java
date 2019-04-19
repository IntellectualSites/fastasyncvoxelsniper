package com.thevoxelbox.voxelsniper;

import java.io.IOException;
import java.util.Map;
import com.google.common.collect.Maps;
import org.bukkit.util.NumberConversions;
import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

/**
 * @author Monofraps
 */
public final class MetricsManager {

	private static int snipesDone;
	private static long snipeCounterInitTimeStamp;
	private static MetricsManager instance;
	private static Map<String, Integer> brushUsageCounter = Maps.newHashMap();

	private MetricsManager() {
	}

	public static MetricsManager getInstance() {
		if (instance == null) {
			instance = new MetricsManager();
		}
		return instance;
	}

	/**
	 * Increase the Snipes Counter.
	 */
	public static void increaseSnipeCounter() {
		snipesDone++;
	}

	/**
	 * Increase usage for a specific brush.
	 *
	 * @param brushName Name of the Brush
	 */
	public static void increaseBrushUsage(String brushName) {
		brushUsageCounter.putIfAbsent(brushName, 0);
		brushUsageCounter.put(brushName, brushUsageCounter.get(brushName));
	}

	/**
	 * Set Initialization time for reference when calculating average Snipes per Minute.
	 */
	public static void setSnipeCounterInitTimeStamp(long currentTimeMillis) {
		snipeCounterInitTimeStamp = currentTimeMillis;
	}

	/**
	 * Start sending Metrics.
	 */
	public void start() {
		try {
			Metrics metrics = new Metrics(VoxelSniper.getInstance());
			Graph defaultGraph = metrics.createGraph("Default");
			defaultGraph.addPlotter(new Metrics.Plotter("Average Snipes per Minute") {

				@Override
				public int getValue() {
					int currentSnipes = snipesDone;
					long initializationTimeStamp = snipeCounterInitTimeStamp;
					double deltaTime = System.currentTimeMillis() - initializationTimeStamp;
					double average;
					if (deltaTime < 60000) {
						average = currentSnipes;
					} else {
						double timeRunning = deltaTime / 60000;
						average = currentSnipes / timeRunning;
					}
					// quite unlikely ...
					if (average > 10000) {
						average = 0;
					}
					return NumberConversions.floor(average);
				}
			});
			Graph brushUsageGraph = metrics.createGraph("Brush Usage");
			for (Map.Entry<String, Integer> entry : brushUsageCounter.entrySet()) {
				brushUsageGraph.addPlotter(new Metrics.Plotter(entry.getKey()) {
					@Override
					public int getValue() {
						return entry.getValue();
					}

					@Override
					public void reset() {
						brushUsageCounter.remove(entry.getKey());
					}
				});
			}
			metrics.start();
		} catch (IOException exception) {
			VoxelSniper.getInstance()
				.getLogger()
				.finest("Failed to submit Metrics Data.");
		}
	}
}
