/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Time;
import mcs.pattern.Event;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static junitx.framework.ComparableAssert.assertGreater;
import static org.junit.Assert.assertEquals;

public class MGridTest {

	@Test
	public void writeEvent() {
		Time.TimeSignature timeSignature = new Time.TimeSignature(4, 4);
		int bars = 2;
		int ticksPerBeat = 4;
		Map<String, Integer> levelMapping = new LinkedHashMap<>();
		levelMapping.put("L1", 1); // row 0
		levelMapping.put("L2", 2); // row 1
		levelMapping.put("L3", 3); // row 2

		MGrid grid = new MGrid(timeSignature, bars, ticksPerBeat, levelMapping);

		grid.write(0, new Event(new int[] { 1, 2 }, 42, 3));

		assertGreater(0, grid.m_velocityMatrix[0][0]);
		assertGreater(0, grid.m_velocityMatrix[0][1]);
		assertEquals(0, grid.m_velocityMatrix[0][2]);
	}
}