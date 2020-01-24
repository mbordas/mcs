/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.graphics.MGraphics;
import mcs.gui.Theme;
import mcs.melody.Chord;
import mcs.pattern.GuitarPattern;

import java.awt.*;

public class ChordDiagram extends MComponent {

	public static final int SCALE_FACTOR = 2;

	public static final int FONT_SIZE = 20 * SCALE_FACTOR;
	public static final int LABEL_HEIGHT_px = 20 * SCALE_FACTOR;
	public static final int CELL_WIDTH_px = 20 * SCALE_FACTOR;
	public static final int CELL_HEIGHT_px = 36 * SCALE_FACTOR;
	public static final int HEADER_CELL_HEIGHT_px = CELL_HEIGHT_px;
	public static final int FRET_NUMBER_WIDTH_px = 20 * SCALE_FACTOR;
	public static final int PADDING_px = 10 * SCALE_FACTOR;

	public static final int FRET_THICKNESS_px = 2 * SCALE_FACTOR;
	public static final int MARKER_DIAMETER_px = (int) (CELL_WIDTH_px * 0.6);

	String m_label;
	int m_rootNote;
	GuitarPattern m_pattern;

	int m_frets;
	int m_fretboardWidth_px;
	int m_fretboardHeight_px;

	public ChordDiagram(String label, int rootNote, GuitarPattern pattern) {
		m_label = label;
		m_rootNote = rootNote;
		m_pattern = new GuitarPattern(pattern);
		m_pattern.setLeftFret(0);

		computeSize();
	}

	private void computeSize() {
		// Computing the number of frets to draw
		m_frets = Math.max(4, m_pattern.getWidth());

		m_fretboardWidth_px = 5 * CELL_WIDTH_px;
		m_fretboardHeight_px = HEADER_CELL_HEIGHT_px + m_frets * CELL_HEIGHT_px;

		setSize(2 * PADDING_px + FRET_NUMBER_WIDTH_px + m_fretboardWidth_px,
				2 * PADDING_px + LABEL_HEIGHT_px + m_fretboardHeight_px);
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		// Clearing display
		graphics.setPaint(MGrid.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.setPaint(Theme.CHORD_DIAGRAM_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.setPaint(Theme.CHORD_DIAGRAM_COLOR);

		// Label
		graphics.setFontSize((float) FONT_SIZE);
		graphics.drawStringCenter(m_label, PADDING_px + FRET_NUMBER_WIDTH_px, PADDING_px, m_fretboardWidth_px, LABEL_HEIGHT_px);

		int leftFret = Integer.MAX_VALUE;

		// Strings
		for(int string = 1; string <= 6; string++) {
			int x_px = getStringX_px(string);
			graphics.fillRect(x_px - FRET_THICKNESS_px / 2, PADDING_px + LABEL_HEIGHT_px + HEADER_CELL_HEIGHT_px,
					FRET_THICKNESS_px, m_fretboardHeight_px - HEADER_CELL_HEIGHT_px);

			GuitarPattern.StringFingering fingering = m_pattern.getFingering(string);
			if(fingering.isPlayed()) {
				int key = m_rootNote + fingering.getInterval();
				int fret = key - m_pattern.getTunning()[string - 1];
				while(fret < 0) {
					fret += 12;
				}
				leftFret = Math.min(leftFret, fret);
			}
		}

		// Frets
		for(int fret = 0; fret <= m_frets; fret++) {
			int thickness = (fret == 0 && leftFret <= 1) ?
					FRET_THICKNESS_px * 3 : // diagram is on head of neck
					FRET_THICKNESS_px;

			int x_px = getStringX_px(1);
			graphics.fillRect(x_px - FRET_THICKNESS_px / 2,
					PADDING_px + LABEL_HEIGHT_px + HEADER_CELL_HEIGHT_px - thickness / 2 + fret * CELL_HEIGHT_px,
					m_fretboardWidth_px + FRET_THICKNESS_px, thickness);
		}

		// Fret number
		if(leftFret > 1) {
			graphics.setFontSize(0.4f * FONT_SIZE);
			graphics.drawStringLeft("" + leftFret, PADDING_px, PADDING_px + LABEL_HEIGHT_px + HEADER_CELL_HEIGHT_px,
					FRET_NUMBER_WIDTH_px, CELL_HEIGHT_px, 0);
		}

		// Dots
		for(int string = 1; string <= 6; string++) {
			GuitarPattern.StringFingering fingering = m_pattern.getFingering(string);
			int x_px = getStringX_px(string);
			if(!fingering.isPlayed()) {
				graphics.setFontSize(0.6f * FONT_SIZE);
				graphics.drawStringCenter("x", x_px - CELL_WIDTH_px / 2, PADDING_px + LABEL_HEIGHT_px,
						CELL_WIDTH_px, HEADER_CELL_HEIGHT_px);
			} else {
				int fret = fingering.getAbscissa();

				// When diagram is not on head of neck, the header cell is not used
				if(leftFret > 0) {
					fret += 1;
				}

				if(fret == 0) {
					graphics.setPaint(Theme.CHORD_DIAGRAM_COLOR);
					graphics.drawStringCenter("o", x_px - CELL_WIDTH_px / 2, PADDING_px + LABEL_HEIGHT_px, CELL_WIDTH_px,
							HEADER_CELL_HEIGHT_px);
				} else {
					int y_px =
							PADDING_px + LABEL_HEIGHT_px + HEADER_CELL_HEIGHT_px + (fret - leftFret - 1) * CELL_HEIGHT_px
									+ CELL_HEIGHT_px / 2;

					fillCircle(graphics, x_px, y_px, MARKER_DIAMETER_px / 2, Theme.CHORD_DIAGRAM_COLOR);
					if(fingering.getInterval() == Chord.ROOT) { // Root dots have emptied center
						fillCircle(graphics, x_px, y_px, MARKER_DIAMETER_px / 3, Theme.CHORD_DIAGRAM_BACKGROUND_COLOR);
					}
				}
			}
		}
	}

	private void fillCircle(MGraphics graphics, int centerX_px, int centerY_px, int radius, Color color) {
		graphics.setPaint(color);
		graphics.fillOval(centerX_px - radius, centerY_px - radius, 2 * radius, 2 * radius);
	}

	int getStringX_px(int string) {
		return PADDING_px + FRET_NUMBER_WIDTH_px + CELL_WIDTH_px * (string - 1);
	}
}