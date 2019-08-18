/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Note;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GuitarNeckTest {

	@Test
	public void getNote() {
		GuitarNeck neck = new GuitarNeck();

		assertEquals(Note.E2, neck.computeNote(1, 0));
		assertEquals(Note.F2, neck.computeNote(1, 1));
		assertEquals(Note.G2, neck.computeNote(1, 3));
		assertEquals(Note.A2, neck.computeNote(1, 5));
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