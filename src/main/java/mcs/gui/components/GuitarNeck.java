/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import javax.swing.*;
import java.awt.*;

public class GuitarNeck extends JComponent {

	public static Color FRETS_COLOR = Color.lightGray;
	public static Color STRINGS_COLOR = Color.gray;
	public static Color MARKERS_COLOR = Color.darkGray;
	public static Color FRETBOARD_COLOR = Color.black;

	public static final int CELL_WIDTH_px = 46;
	public static final int CELL_HEIGHT_px = 24;
	public static final int GRID_PADDING_px = 10;

	public static final int FRET_THICKNESS_px = 2;
	public static final int STRING_THICKNESS_px = 2;
	public static final int MARKER_RADIUS_px = 14;
	public static final int DEFAULT_FRETS_NUMBER = 21;

	int m_frets = DEFAULT_FRETS_NUMBER;

	public GuitarNeck(int frets) {

		m_frets = frets;

		int width_px = m_frets * CELL_WIDTH_px + 2 * GRID_PADDING_px;
		int height_px = 6 * CELL_HEIGHT_px + 2 * GRID_PADDING_px;

		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));
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
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D graphics2d = (Graphics2D) graphics;

		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Clearing display
		graphics2d.setPaint(MGrid.BACKGROUND_COLOR);
		graphics2d.fillRect(0, 0, getSize().width, getSize().height);

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

	}

	public static void main(String[] args) {
		GuitarNeck m_neck = new GuitarNeck(24);

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
	}
}