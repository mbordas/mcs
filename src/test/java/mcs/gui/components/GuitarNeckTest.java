/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Note;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GuitarNeckTest {

	@Test
	public void getNotesOfString() {
		GuitarNeck neck = new GuitarNeck();

		neck.add(1, 5);

		assertEquals(1, neck.getPickedFretsOfString(1).size());
		assertEquals(5, (int) neck.getPickedFretsOfString(1).get(0));

		neck.add(1, 7);

		assertEquals(2, neck.getPickedFretsOfString(1).size());
		assertEquals(5, (int) neck.getPickedFretsOfString(1).get(0));
		assertEquals(7, (int) neck.getPickedFretsOfString(1).get(1));
	}

	@Test
	public void getNote() {
		GuitarNeck neck = new GuitarNeck();

		assertEquals(Note.E2, neck.computeNote(1, 0));
		assertEquals(Note.F2, neck.computeNote(1, 1));
		assertEquals(Note.G2, neck.computeNote(1, 3));
		assertEquals(Note.A2, neck.computeNote(1, 5));
	}

	@Test
	public void getFrets() {
		GuitarNeck neck = new GuitarNeck();

		assertEquals(2, neck.getFrets(1, Note.E1).size());
		assertTrue(neck.getFrets(1, Note.E1).contains(0));
		assertTrue(neck.getFrets(1, Note.E1).contains(12));
	}

	@Test
	public void pixel2string() {
		GuitarNeck neck = new GuitarNeck();

		assertEquals(6, neck.pixel2string(GuitarNeck.GRID_PADDING_px));
		assertEquals(6, neck.pixel2string(GuitarNeck.GRID_PADDING_px + GuitarNeck.CELL_HEIGHT_px - 1));
		assertEquals(5, neck.pixel2string(GuitarNeck.GRID_PADDING_px + GuitarNeck.CELL_HEIGHT_px + 1));
	}

	@Test
	public void pixel2fret() {
		GuitarNeck neck = new GuitarNeck();

		int padding = GuitarNeck.GRID_PADDING_px;
		int head = GuitarNeck.HEAD_CELL_WIDTH_px;
		int fret = GuitarNeck.CELL_WIDTH_px;

		assertEquals(0, neck.pixel2fret(padding));
		assertEquals(0, neck.pixel2fret(padding + head - 1));
		assertEquals(1, neck.pixel2fret(padding + head + 1));
		assertEquals(1, neck.pixel2fret(padding + head + fret - 1));
		assertEquals(2, neck.pixel2fret(padding + head + fret + 1));
	}
}