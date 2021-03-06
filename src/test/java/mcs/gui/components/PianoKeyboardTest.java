/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Note;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PianoKeyboardTest {

	@Test
	public void noteToX() {
		assertEquals(5, PianoKeyboard.noteToX_px(Note.C1, Note.C1, 10));
		assertEquals(10, PianoKeyboard.noteToX_px(Note.C1 + 1, Note.C1, 10)); // C#
		assertEquals(15, PianoKeyboard.noteToX_px(Note.D1, Note.C1, 10));
		assertEquals(20, PianoKeyboard.noteToX_px(Note.D1 + 1, Note.C1, 10)); //D#
		assertEquals(25, PianoKeyboard.noteToX_px(Note.E1, Note.C1, 10));
		assertEquals(35, PianoKeyboard.noteToX_px(Note.F1, Note.C1, 10));
		assertEquals(40, PianoKeyboard.noteToX_px(Note.F1 + 1, Note.C1, 10)); // F#
		assertEquals(45, PianoKeyboard.noteToX_px(Note.G1, Note.C1, 10));
		assertEquals(50, PianoKeyboard.noteToX_px(Note.G1 + 1, Note.C1, 10)); // G#
		assertEquals(55, PianoKeyboard.noteToX_px(Note.A1, Note.C1, 10));
		assertEquals(60, PianoKeyboard.noteToX_px(Note.A1 + 1, Note.C1, 10)); // A#
		assertEquals(65, PianoKeyboard.noteToX_px(Note.B1, Note.C1, 10));
		assertEquals(75, PianoKeyboard.noteToX_px(Note.C2, Note.C1, 10));
		assertEquals(80, PianoKeyboard.noteToX_px(Note.C2 + 1, Note.C1, 10)); // C#
	}

	@Test
	public void isWhite() {
		for(int octave = 1; octave <= 2; octave++) {
			assertTrue(PianoKeyboard.isWhite(Note.C0 + 12 * octave));
			assertFalse(PianoKeyboard.isWhite(Note.C0 + 12 * octave + 1));
			assertTrue(PianoKeyboard.isWhite(Note.D0 + 12 * octave));
			assertFalse(PianoKeyboard.isWhite(Note.D0 + 12 * octave + 1));
			assertTrue(PianoKeyboard.isWhite(Note.E0 + 12 * octave));
			assertTrue(PianoKeyboard.isWhite(Note.F0 + 12 * octave));
			assertFalse(PianoKeyboard.isWhite(Note.F0 + 12 * octave + 1));
			assertTrue(PianoKeyboard.isWhite(Note.G0 + 12 * octave));
			assertFalse(PianoKeyboard.isWhite(Note.G0 + 12 * octave + 1));
			assertTrue(PianoKeyboard.isWhite(Note.A0 + 12 * octave));
			assertFalse(PianoKeyboard.isWhite(Note.A0 + 12 * octave + 1));
			assertTrue(PianoKeyboard.isWhite(Note.B0 + 12 * octave));
		}
	}

	@Test
	public void xyToNote() {
		int lowerC = Note.C1;
		int wkw_px = 20; // White key width
		int bkw_px = 10; // Black key width
		int bkh_px = 50; // Black key height

		assertEquals(Note.C1, PianoKeyboard.xyToNote(0, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.C1, PianoKeyboard.xyToNote(0, 60, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.C1, PianoKeyboard.xyToNote(10, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.C1, PianoKeyboard.xyToNote(10, 60, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.C1 + 1, PianoKeyboard.xyToNote(16, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.C1, PianoKeyboard.xyToNote(16, 60, lowerC, wkw_px, bkw_px, bkh_px));

		assertEquals(Note.D1 - 1, PianoKeyboard.xyToNote(21, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.D1, PianoKeyboard.xyToNote(21, 60, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.D1, PianoKeyboard.xyToNote(30, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.D1, PianoKeyboard.xyToNote(30, 60, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.D1 + 1, PianoKeyboard.xyToNote(36, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.D1, PianoKeyboard.xyToNote(36, 60, lowerC, wkw_px, bkw_px, bkh_px));

		assertEquals(Note.C2, PianoKeyboard.xyToNote(140, 0, lowerC, wkw_px, bkw_px, bkh_px));
		assertEquals(Note.C2, PianoKeyboard.xyToNote(140, 60, lowerC, wkw_px, bkw_px, bkh_px));
	}

}