/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Note;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScoreFragmentTest {

	@Test
	public void getDistance() {
		assertEquals(0, ScoreFragment.getDistance(Note.C1, Note.C1));
		assertEquals(1, ScoreFragment.getDistance(Note.C1, Note.D1));
		assertEquals(2, ScoreFragment.getDistance(Note.C1, Note.E1));
		assertEquals(3, ScoreFragment.getDistance(Note.C1, Note.F1));
		assertEquals(4, ScoreFragment.getDistance(Note.C1, Note.G1));
		assertEquals(5, ScoreFragment.getDistance(Note.C1, Note.A1));
		assertEquals(6, ScoreFragment.getDistance(Note.C1, Note.B1));
		assertEquals(7, ScoreFragment.getDistance(Note.C1, Note.C2));

		assertEquals(1, ScoreFragment.getDistance(Note.E1, Note.F1));
		assertEquals(2, ScoreFragment.getDistance(Note.E1, Note.G1));
		assertEquals(3, ScoreFragment.getDistance(Note.E1, Note.A1));
		assertEquals(4, ScoreFragment.getDistance(Note.E1, Note.B1));
		assertEquals(5, ScoreFragment.getDistance(Note.E1, Note.C2));
		assertEquals(6, ScoreFragment.getDistance(Note.E1, Note.D2));
		assertEquals(7, ScoreFragment.getDistance(Note.E1, Note.E2));
		assertEquals(8, ScoreFragment.getDistance(Note.E1, Note.F2));

		assertEquals(-1, ScoreFragment.getDistance(Note.F1, Note.E1));
	}
}