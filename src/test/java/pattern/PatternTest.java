/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package pattern;

import mcs.melody.Note;
import mcs.melody.Time;
import mcs.pattern.Pattern;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PatternTest {

	static final int VELOCITY = Note.Dynamic.MEZZO_FORTE.velocity;

	static class TestPattern extends Pattern {

		protected TestPattern(Time.TimeSignature timeSignature, int ticksPerBeat) {
			super(timeSignature, ticksPerBeat);
		}
	}

	@Test
	public void computeBarIndex() {
		int ticksPerBeat = 16;
		int beatsInBar = 4;

		// Tick #0 is the first in bar #0 (first bar)
		// Tick #63 is the last in bar #0
		for(long tick = 0; tick <= 63; tick++) {
			assertEquals("tick #" + tick, 0, Pattern.computeBarIndex(tick, ticksPerBeat, beatsInBar));
		}

		// Tick #64 is the first in bar #1
		// Tick #127 is the last in bar #1
		for(long tick = 64; tick <= 127; tick++) {
			assertEquals("tick #" + tick, 1, Pattern.computeBarIndex(tick, ticksPerBeat, beatsInBar));
		}
	}

	@Test
	public void getBars() {
		Pattern pattern = new TestPattern(new Time.TimeSignature(4, 4), 16);

		pattern.add(0, VELOCITY, 0, 1); // Event on first tick
		pattern.add(1, VELOCITY, 0, 16); // Event during whole bar

		assertEquals(1, pattern.getBars());

		pattern.add(0, VELOCITY, 64, 80);

		assertEquals(2, pattern.getBars());
	}
}