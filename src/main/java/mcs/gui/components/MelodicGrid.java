/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Block;
import mcs.melody.Time;

import java.util.LinkedHashMap;
import java.util.Map;

public class MelodicGrid extends MGrid {

	public MelodicGrid(Time.TimeSignature timeSignature, int bars) {
		super(timeSignature, bars, getKeyMapping());
	}

	public Block toBlock(int channel, int[] chord) {
		Block result = new Block(m_timeSignature, m_ticksPerBeat);

		int row = 0;
		for(int interval : m_keyMapping.values()) {
			for(int tick = 0; tick < getMatrixWidth(); tick++) {
				int velocity = m_velocityMatrix[tick][row];

				if(velocity > 0) {
					int note = chord[interval - 1];
					if(note > 0) {
						result.add(channel, note, velocity, tick, tick + 1);
					}
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