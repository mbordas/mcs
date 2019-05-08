/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Time;
import mcs.pattern.Event;
import mcs.pattern.MelodicPattern;

import java.util.LinkedHashMap;
import java.util.Map;

public class MelodicGrid extends MGrid {

	public MelodicGrid(Time.TimeSignature timeSignature, int bars, int ticksPerBeat) {
		super(timeSignature, bars, ticksPerBeat, getKeyMapping());
	}

	/**
	 * Writes 'pattern' in this grid. It does not clear the grid before, if some cells are already written, then they will be
	 * kept written (or overridden).
	 *
	 * @param pattern
	 */
	public void write(MelodicPattern pattern) {
		// Writing events
		for(long tick = 0; tick < pattern.size(); tick++) {
			for(Event event : pattern.getEvents(tick)) {
				write(tick, event);
			}
		}

		// Updating display
		repaint();
	}

	public MelodicPattern toPattern() {
		MelodicPattern result = new MelodicPattern(m_timeSignature, m_ticksPerBeat);

		int row = 0;
		for(int interval : m_levelMapping.values()) {
			for(int tick = 0; tick < getMatrixWidth(); tick++) {
				int velocity = m_velocityMatrix[tick][row];

				if(velocity > 0) {
					result.add(interval, velocity, tick, tick + 1);
				}
			}

			row++;
		}

		return result;
	}

	public static final Map<String, Integer> getKeyMapping() {
		Map<String, Integer> result = new LinkedHashMap<>();
		for(int k = 7; k >= 1; k--) {
			result.put("Interval #" + k, k);
		}
		return result;
	}
}