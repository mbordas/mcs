/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.graphics.MGraphics;

import java.awt.*;

public class ChordGrid extends MComponent {

	public static Color LINES_COLOR = Color.gray;

	public static final int PADDING_px = 20;

	public static final int CELL_WIDTH_px = 100;
	public static final int CELL_HEIGTH_px = 76;

	int m_rows;
	int m_cols;

	public ChordGrid(int rows, int cols) {
		m_rows = rows;
		m_cols = cols;

		int width_px = 2 * PADDING_px + cols * CELL_WIDTH_px;
		int heigth_px = 2 * PADDING_px + rows * CELL_HEIGTH_px;
		setSize(width_px, heigth_px);
		setPreferredSize(width_px, heigth_px);
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		// Clearing display
		graphics.setPaint(MGrid.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getSize().width, getSize().height);

		graphics.setPaint(LINES_COLOR);
		for(int r = 0; r < m_rows; r++) {
			for(int c = 0; c < m_cols; c++) {
				int x_px = PADDING_px + c * CELL_WIDTH_px;
				int y_px = PADDING_px + r * CELL_HEIGTH_px;
				graphics.drawRect(x_px, y_px, CELL_WIDTH_px, CELL_HEIGTH_px);
			}
		}
	}
}