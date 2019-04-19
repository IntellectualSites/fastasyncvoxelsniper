package com.thevoxelbox.voxelsniper;

import org.junit.Before;
import org.mockito.Mockito;

/**
 *
 */
public class SniperManagerTest {

	private SniperManager sniperManager;

	@Before
	public void setUp() throws Exception {
		this.sniperManager = new SniperManager(Mockito.mock(VoxelSniper.class));
	}
}
