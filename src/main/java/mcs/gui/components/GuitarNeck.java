/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Chord;
import mcs.pattern.GuitarPattern;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GuitarNeck extends MComponent {

	public static Color FRETS_COLOR = Color.lightGray;
	public static Color STRINGS_COLOR = Color.gray;
	public static Color MARKERS_COLOR = Color.darkGray;
	public static Color FINGER_COLOR = Color.orange;
	public static Color FRETBOARD_COLOR = Color.black;

	public static final int CELL_WIDTH_px = 64;
	public static final int CELL_HEIGHT_px = 24;
	public static final int GRID_PADDING_px = 10;

	public static final int FINGER_RADIUS_px = 18;
	public static final int FINGER_STROKE_px = 4;
	public static final int FRET_THICKNESS_px = 2;
	public static final int STRING_THICKNESS_px = 2;
	public static final int MARKER_RADIUS_px = 14;
	public static final int DEFAULT_FRETS_NUMBER = 21;

	int m_frets = DEFAULT_FRETS_NUMBER;

	GuitarPattern m_pattern;
	int m_patternAnchorCell = 1;

	public GuitarNeck(int frets) {

		m_frets = frets;

		int width_px = m_frets * CELL_WIDTH_px + 2 * GRID_PADDING_px;
		int height_px = 6 * CELL_HEIGHT_px + 2 * GRID_PADDING_px;

		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));
	}

	public void set(GuitarPattern pattern, int cell) {
		m_pattern = pattern;
		m_patternAnchorCell = cell;
		updateDisplay();
	}

	protected void updateDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		// Clearing display
		graphics.setPaint(MGrid.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getSize().width, getSize().height);

		int fretboardWidth_px = m_frets * CELL_WIDTH_px;
		int fretboardHeight_px = 6 * CELL_HEIGHT_px;

		graphics2d.setPaint(FRETBOARD_COLOR);
		graphics2d.fillRect(GRID_PADDING_px, GRID_PADDING_px, fretboardWidth_px, fretboardHeight_px);

		// Frets
		graphics2d.setPaint(FRETS_COLOR);
		for(int f = 1; f <= m_frets; f++) {
			int x_px = GRID_PADDING_px + f * CELL_WIDTH_px;
			int y_px = GRID_PADDING_px;
			graphics.fillRect(x_px, y_px, FRET_THICKNESS_px, fretboardHeight_px);
		}

		// Strings
		graphics2d.setPaint(STRINGS_COLOR);
		for(int s = 0; s < 6; s++) {
			int x_px = GRID_PADDING_px;
			int y_px = GRID_PADDING_px + (6 - s) * CELL_HEIGHT_px - (CELL_HEIGHT_px + STRING_THICKNESS_px) / 2;
			graphics.fillRect(x_px, y_px, m_frets * CELL_WIDTH_px, STRING_THICKNESS_px);
		}

		// Markers
		graphics2d.setPaint(MARKERS_COLOR);
		for(int m : new int[] { 3, 5, 7, 9, 12, 15, 17, 19, 21, 24 }) {
			if(m > m_frets) {
				break;
			}

			int x_px = GRID_PADDING_px + m * CELL_WIDTH_px - CELL_WIDTH_px / 2 - MARKER_RADIUS_px / 2;

			if(m % 12 == 0) {
				for(int i : new int[] { 2, 4 }) {
					int y_px = GRID_PADDING_px + i * CELL_HEIGHT_px - MARKER_RADIUS_px / 2;
					graphics.fillOval(x_px, y_px, MARKER_RADIUS_px, MARKER_RADIUS_px);
				}
			} else {
				int y_px = GRID_PADDING_px + 3 * CELL_HEIGHT_px - MARKER_RADIUS_px / 2;
				graphics.fillOval(x_px, y_px, MARKER_RADIUS_px, MARKER_RADIUS_px);
			}
		}

		if(m_pattern != null) {
			graphics2d.setPaint(FINGER_COLOR);
			Stroke stroke = graphics2d.getStroke();
			graphics2d.setStroke(new BasicStroke(FINGER_STROKE_px));

			int rootRadius = FINGER_RADIUS_px; // Radius used to fill circle
			// radius used to draw circle, it takes finger stroke into account
			int nonRootRadius_px = FINGER_RADIUS_px - FINGER_STROKE_px / 2;

			// Looping over the strings. Low E=0, A=1... high E=5
			for(int string = 0; string < 6; string++) {
				GuitarPattern.StringFingering fingering = m_pattern.getFingering(string);

				int cell = m_patternAnchorCell + fingering.getAbscissa();

				int x_px = GRID_PADDING_px + cell * CELL_WIDTH_px - CELL_WIDTH_px / 2;
				int y_px = GRID_PADDING_px + (6 - string) * CELL_HEIGHT_px - CELL_HEIGHT_px / 2;

				if(fingering.getInterval() != Chord.ROOT) {
					graphics.drawOval(x_px - nonRootRadius_px / 2, y_px - nonRootRadius_px / 2, nonRootRadius_px, nonRootRadius_px);
				} else {
					graphics.fillOval(x_px - rootRadius / 2, y_px - rootRadius / 2, rootRadius, rootRadius);
				}
			}

			graphics2d.setStroke(stroke);
		}

	}

	public static void main(String[] args) throws IOException {
		GuitarNeck m_neck = new GuitarNeck(DEFAULT_FRETS_NUMBER);

		// create main frame
		JFrame frame = new JFrame("Guitar Neck");
		Container content = frame.getContentPane();
		// set layout on content pane
		content.setLayout(new BorderLayout());

		// add to content pane
		content.add(m_neck, BorderLayout.CENTER);

		frame.pack();
		// can close frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// show the swing paint result
		frame.setVisible(true);

		GuitarPattern gpt = new GuitarPattern(new File("pattern/guitar/minor_s1.gpt"));

		m_neck.set(gpt, 10);
	}

}