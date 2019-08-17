/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.graphics.DPI;
import mcs.graphics.MGraphics;
import mcs.melody.Chord;
import mcs.melody.Note;
import mcs.pattern.GuitarPattern;
import mcs.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

public class GuitarNeck extends MComponent {

	public static Color FRETS_COLOR = Color.lightGray;
	public static Color STRINGS_COLOR = Color.gray;
	public static Color MARKERS_COLOR = Color.darkGray;
	public static Color FINGER_COLOR = Color.orange;
	public static Color FRETBOARD_COLOR = Color.black;

	public static final int CELL_WIDTH_px = 64;
	public static final int HEAD_CELL_WIDTH_px = CELL_WIDTH_px / 2;
	public static final int CELL_HEIGHT_px = 24;
	public static final int GRID_PADDING_px = 10;

	public static final int FINGER_RADIUS_px = 18;
	public static final int FINGER_STROKE_px = 4;
	public static final int FRET_THICKNESS_px = 2;
	public static final int STRING_THICKNESS_px = 2;
	public static final int MARKER_RADIUS_px = 14;
	public static final int DEFAULT_FRETS_NUMBER = 21;

	public static final int[] TUNING_STANDARD = new int[] { Note.E2, Note.A2, Note.D3, Note.G3, Note.B3, Note.E4 };

	final int m_frets;
	final int[] m_tuning;
	DotType[][] m_dots;
	int m_rootNote = Note.NULL;

	EditionMode m_editionMode = null;

	public enum EditionMode {
		CHORD, // Maximum one note per string
		SCALE // Free
	}

	public enum DotType {NOTE}

	public GuitarNeck() {
		m_frets = DEFAULT_FRETS_NUMBER;
		m_tuning = TUNING_STANDARD;

		clear();

		int width_px = HEAD_CELL_WIDTH_px + m_frets * CELL_WIDTH_px + 2 * GRID_PADDING_px;
		int height_px = 6 * CELL_HEIGHT_px + 2 * GRID_PADDING_px;

		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));

		addMouseListener(buildMouseListener());
	}

	//
	// Edition
	//

	public void clearString(int string) {
		m_dots[string - 1] = new DotType[m_frets];
	}

	public void clear() {
		m_dots = new DotType[6][m_frets + 1];
	}

	/**
	 * @param pattern
	 * @param fret    0 means the neck head, 1 the first fret, etc.
	 */
	public void set(GuitarPattern pattern, int fret) {
		clear();

		for(int string = 1; string <= 6; string++) {
			GuitarPattern.StringFingering fingering = pattern.getFingering(string);
			int _fret = fret + fingering.getAbscissa();
			if(_fret < 0 || _fret > m_frets - 1) {
				// not displayed
			} else {
				m_dots[string - 1][_fret] = DotType.NOTE;
			}

			int note = getNote(string, _fret);
			m_rootNote = note - fingering.getInterval();
		}

		updateDisplay();
	}

	public void enableEdition(EditionMode mode) {
		m_editionMode = mode;
	}

	int getNote(int string, int fret) {
		return m_tuning[string - 1] + fret;
	}

	// Mouse management

	private MouseListener buildMouseListener() {
		return new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if(m_editionMode == null) {
					return;
				}

				int string = pixel2string(DPI.unScale(event.getY()));
				int fret = pixel2fret(DPI.unScale(event.getX()));

				FileUtils.log("GuitarNeck clicked string=%d, fret=%d", string, fret);

				if(event.getButton() == MouseEvent.BUTTON1) { // Add
					if(m_editionMode == EditionMode.CHORD) {
						// Clearing string
						clearString(string);
					}
					m_dots[string - 1][fret] = DotType.NOTE;
				} else if(event.getButton() == MouseEvent.BUTTON3) { // Remove
					m_dots[string - 1][fret] = null;
				}

				updateDisplay();
			}

			@Override
			public void mousePressed(MouseEvent event) {
			}

			@Override
			public void mouseReleased(MouseEvent event) {
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
			}
		};
	}

	//
	// Graphics
	//

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

		int fretboardWidth_px = HEAD_CELL_WIDTH_px + m_frets * CELL_WIDTH_px;
		int fretboardHeight_px = 6 * CELL_HEIGHT_px;

		graphics.setPaint(FRETBOARD_COLOR);
		graphics.fillRect(GRID_PADDING_px, GRID_PADDING_px, fretboardWidth_px, fretboardHeight_px);

		// Frets
		graphics.setPaint(FRETS_COLOR);
		for(int f = 0; f <= m_frets; f++) {
			int x_px = GRID_PADDING_px + HEAD_CELL_WIDTH_px + f * CELL_WIDTH_px;
			int y_px = GRID_PADDING_px;
			graphics.fillRect(x_px, y_px, f == 0 ? FRET_THICKNESS_px * 2 : FRET_THICKNESS_px, fretboardHeight_px);
		}

		// Strings
		graphics.setPaint(STRINGS_COLOR);
		for(int s = 0; s < 6; s++) {
			int x_px = GRID_PADDING_px;
			int y_px = GRID_PADDING_px + (6 - s) * CELL_HEIGHT_px - (CELL_HEIGHT_px + STRING_THICKNESS_px) / 2;
			graphics.fillRect(x_px, y_px, HEAD_CELL_WIDTH_px + m_frets * CELL_WIDTH_px, STRING_THICKNESS_px);
		}

		// Markers
		graphics.setPaint(MARKERS_COLOR);
		for(int m : new int[] { 3, 5, 7, 9, 12, 15, 17, 19, 21, 24 }) {
			if(m > m_frets) {
				break;
			}

			int x_px = GRID_PADDING_px + HEAD_CELL_WIDTH_px + m * CELL_WIDTH_px - CELL_WIDTH_px / 2 - MARKER_RADIUS_px / 2;

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

		graphics.setPaint(FINGER_COLOR);
		Stroke stroke = graphics.getStroke();
		graphics.setStroke(new BasicStroke(FINGER_STROKE_px));

		// Looping over the strings. Low E=0, A=1... high E=5
		for(int string = 1; string <= 6; string++) {
			for(int fret = 0; fret < m_frets; fret++) {
				DotType dot = m_dots[string - 1][fret];
				if(dot == null) {
					continue;
				}

				Integer interval = null;
				if(m_rootNote != Note.NULL) {
					int note = getNote(string, fret);
					interval = Note.getInterval(m_rootNote, note);
				}
				drawNote(graphics, string, fret, interval);
			}
		}

		graphics.setStroke(stroke);
	}

	private void drawNote(MGraphics graphics, int string, int cell, Integer interval) {
		int rootRadius = FINGER_RADIUS_px; // Radius used to fill circle
		int nonRootRadius_px = FINGER_RADIUS_px - FINGER_STROKE_px / 2; // radius used to draw circle

		int cellWidth_x = cell == 0 ? HEAD_CELL_WIDTH_px : CELL_WIDTH_px;
		int x_px = GRID_PADDING_px + HEAD_CELL_WIDTH_px + cell * CELL_WIDTH_px - cellWidth_x / 2;
		int y_px = GRID_PADDING_px + (7 - string) * CELL_HEIGHT_px - CELL_HEIGHT_px / 2;

		if(interval == null || Chord.ROOT != interval) {
			graphics.drawOval(x_px - nonRootRadius_px / 2, y_px - nonRootRadius_px / 2, nonRootRadius_px, nonRootRadius_px);
		} else {
			graphics.fillOval(x_px - rootRadius / 2, y_px - rootRadius / 2, rootRadius, rootRadius);
		}
	}

	int pixel2string(int y_px) {
		return 6 - (y_px - GRID_PADDING_px) / CELL_HEIGHT_px;
	}

	int pixel2fret(int x_px) {
		int xInNeck = x_px - GRID_PADDING_px;
		if(xInNeck <= HEAD_CELL_WIDTH_px) {
			return 0;
		} else {
			// 'xInNeck' is augmented, as if the head fret was as wide as the others.
			int correctedX = xInNeck + (CELL_WIDTH_px - HEAD_CELL_WIDTH_px);
			return correctedX / CELL_WIDTH_px;
		}
	}

	public static void main(String[] args) throws IOException {
		DPI.loadCommandLine(args);

		GuitarNeck m_neck = new GuitarNeck();

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

		m_neck.set(gpt, 0);
		m_neck.enableEdition(EditionMode.CHORD);
	}

}