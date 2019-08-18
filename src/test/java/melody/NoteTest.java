/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package melody;

import mcs.melody.Chord;
import mcs.melody.Note;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NoteTest {

	@Test
	public void getInterval() {
		// Simple cases, where start note < end note
		assertEquals(Chord.ROOT, Note.getInterval(Note.E3, Note.E4));
		assertEquals(Chord.MAJOR_SECOND, Note.getInterval(Note.C3, Note.D4));
		assertEquals(Chord.MINOR_THIRD, Note.getInterval(Note.C3, Note.E3 - 1));

		// Cases where start note > end note
		assertEquals(Chord.ROOT, Note.getInterval(Note.E4, Note.E3));
		assertEquals(Chord.MAJOR_SECOND, Note.getInterval(Note.C5, Note.D4));
		assertEquals(Chord.MINOR_THIRD, Note.getInterval(Note.C8, Note.E3 - 1));
	}
}