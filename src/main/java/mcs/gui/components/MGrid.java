/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.melody.Note;
import mcs.melody.Time;
import mcs.pattern.Pattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * The {@link MGrid} is a Swing component that helps edition of {@link Pattern}'s {@link Event}s.
 */
public class MGrid extends JComponent {

	public static final int CELL_WIDTH_px = 40;
	public static final int CELL_HEIGHT_px = 40;
	public static final int GRID_PADDING_px = 10;
	public static final int CELL_PADDING_px = 2;
	public static final int ROW_LABEL_WIDTH_px = 200;

	public static Color BACKGROUND_COLOR = Color.darkGray;
	public static Color LABEL_COLOR = Color.WHITE;
	static Color m_emptyCellColor = Color.lightGray;
	static Color m_filledCellColor = Color.orange;

	// Edition
	enum EditionMode {WRITE, ERASE, MOVE}

	int m_ticksPerBeat;
	EditionMode m_mode = EditionMode.MOVE;

	// Pattern
	Time.TimeSignature m_timeSignature;
	Map<String, Integer> m_levelMapping;
	int[][] m_velocityMatrix;
	int m_currentVelocity = Note.DEFAULT_VELOCITY;

	RowClickListener m_rowClickListener;

	/**
	 * This interface is used for container to process clicks on grid's rows. For example it could play the corresponding sound.
	 */
	public interface RowClickListener {
		void onPress(int level);

		void onRelease(int level);
	}

	/**
	 * @param timeSignature
	 * @param bars
	 * @param levelMapping  Entries are put on rows from top to bottom.
	 */
	public MGrid(Time.TimeSignature timeSignature, int bars, int ticksPerBeat, Map<String, Integer> levelMapping) {
		m_timeSignature = timeSignature;
		m_ticksPerBeat = ticksPerBeat;
		m_levelMapping = levelMapping;

		initMatrix(timeSignature, bars);

		addMouseListener(buildMouseListener());
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent event) {
				onMouseAction(event);
			}
		});

		int width_px = ROW_LABEL_WIDTH_px + 2 * GRID_PADDING_px + getMatrixWidth() * CELL_WIDTH_px;
		int height_px = 2 * GRID_PADDING_px + getMatrixHeight() * CELL_HEIGHT_px;
		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));
	}

	private void initMatrix(Time.TimeSignature timeSignature, int bars) {
		// Total number of columns in grid (x-axis)
		int columns = bars * timeSignature.getBeatsInBar() * m_ticksPerBeat;
		int rows = m_levelMapping.size();
		// Each available key is a line (y-axis)
		m_velocityMatrix = new int[columns][rows];
		for(int x = 0; x < columns; x++) {
			m_velocityMatrix[x] = new int[rows];
			for(int y = 0; y < rows; y++) {
				m_velocityMatrix[x][y] = 0;
			}
		}
	}

	public void setRowClickListener(RowClickListener listener) {
		m_rowClickListener = listener;
	}

	//
	// Grid management
	//

	int getMatrixWidth() {
		return m_velocityMatrix.length;
	}

	int getMatrixHeight() {
		return m_velocityMatrix[0].length;
	}

	void write(long tick, mcs.pattern.Event event) {
		int column = tickToColumn(tick);
		for(int level : event.getLevels()) {
			int row = levelToRow(level);
			for(int c = 0; c < event.getDuration_ticks(); c++) { // Last tick of event is the one before tickStop
				write(column + c, row);
			}
		}
	}

	/**
	 * Erases values of all cells. After this call, grid is empty.
	 */
	public void eraseAll() {
		// Clearing velocity matrix
		for(int x = 0; x < getMatrixWidth(); x++) {
			for(int y = 0; y < getMatrixHeight(); y++) {
				m_velocityMatrix[x][y] = 0;
			}
		}

		updateDisplay();
	}

	static boolean isInGrid(int columns, int row, int width, int height) {
		if(columns < 0 || columns >= width) {
			return false;
		}
		if(row < 0 || row >= height) {
			return false;
		}
		return true;
	}

	private void write(int column, int row) {
		m_velocityMatrix[column][row] = m_currentVelocity;
	}

	private void erase(int column, int row) {
		m_velocityMatrix[column][row] = 0;
	}

	//
	// Coordinates
	//

	/**
	 * Computes column index in grid from {@link Pattern}'s tick.
	 *
	 * @param tick
	 * @return
	 */
	private int tickToColumn(long tick) {
		// A tick corresponds to the resolution of the pattern. Even if this method is obvious, it makes the code more
		// readable ;-)
		return (int) tick;
	}

	/**
	 * Computes row index in grid from {@link Pattern}'s level. Pattern's levels are free (stored in a local mapping),
	 * 'row' starts with 0 and rows are going from top to bottom.
	 *
	 * @param level Starts with 1. Level #1 is display at top row.
	 * @return
	 */
	private int levelToRow(int level) {
		int row = 0;
		for(int _level : m_levelMapping.values()) {
			if(level == _level) {
				return row;
			}
			row++;
		}
		throw new IndexOutOfBoundsException("Level out of mapping: " + level);
	}

	/**
	 * Computes column index in grid from the relative x-axis coordinate of mouse in that {@link MGrid} component.
	 *
	 * @param mouseX_px
	 * @return
	 */
	static int pixel2column(int mouseX_px, int rowLabelWidth_px, int cellWidth_px, int gridPadding_px) {
		int xInGrid_px = mouseX_px - (rowLabelWidth_px + gridPadding_px);
		if(xInGrid_px < 0) {
			// Avoids dividing a negative but small number by CELL_WIDTH_px
			// which could given result = 0, still in Grid.
			return -1;
		} else {
			return xInGrid_px / cellWidth_px;
		}
	}

	/**
	 * Computes row index (going from top to bottom) in grid from the relative y-axis coordinate of mouse in that {@link MGrid}
	 * component.
	 *
	 * @param mouseY_px
	 * @return
	 */
	static int pixel2row(int mouseY_px, int cellHeight_px, int gridPadding_px) {
		return (mouseY_px - gridPadding_px) / cellHeight_px;
	}

	//
	// Mouse management
	//

	private MouseListener buildMouseListener() {
		return new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
			}

			@Override
			public void mousePressed(MouseEvent event) {
				if(event.getButton() == MouseEvent.BUTTON1) {
					m_mode = EditionMode.WRITE;
				} else if(event.getButton() == MouseEvent.BUTTON3) {
					m_mode = EditionMode.ERASE;
				}

				if(!onMouseAction(event)) {

					int column = pixel2column(event.getX(), ROW_LABEL_WIDTH_px, CELL_WIDTH_px, GRID_PADDING_px);
					int row = pixel2row(event.getY(), CELL_HEIGHT_px, GRID_PADDING_px);

					if(column < 0 && m_rowClickListener != null) {
						int i = 0;
						for(Integer level : m_levelMapping.values()) {
							if(i == row) {
								m_rowClickListener.onPress(level);
							}
							i++;
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent event) {
				m_mode = EditionMode.MOVE;

				int column = pixel2column(event.getX(), ROW_LABEL_WIDTH_px, CELL_WIDTH_px, GRID_PADDING_px);
				int row = pixel2row(event.getY(), CELL_HEIGHT_px, GRID_PADDING_px);

				if(column < 0 && m_rowClickListener != null) {
					int i = 0;
					for(Integer level : m_levelMapping.values()) {
						if(i == row) {
							m_rowClickListener.onRelease(level);
						}
						i++;
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
			}
		};
	}

	/**
	 * @param event
	 * @return If event has been processed. Returns false if event is ignored here.
	 */
	private boolean onMouseAction(MouseEvent event) {
		int column = pixel2column(event.getX(), ROW_LABEL_WIDTH_px, CELL_WIDTH_px, GRID_PADDING_px);
		int row = pixel2row(event.getY(), CELL_HEIGHT_px, GRID_PADDING_px);

		if(isInGrid(column, row, getMatrixWidth(), getMatrixHeight())) {
			if(m_mode == EditionMode.MOVE) {
				return true;
			}

			if(m_mode == EditionMode.WRITE) {
				write(column, row);
			} else if(m_mode == EditionMode.ERASE) {
				erase(column, row);
			}

			// We only update the display of the cell
			updateDisplay(column, row);

			return true;
		} else {
			return false;
		}
	}

	//
	// Custom graphics
	//

	protected void updateDisplay() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}

	/**
	 * This method repaints only the area of cell given by 'column' and 'row'.
	 *
	 * @param column
	 * @param row
	 */
	private void updateDisplay(final int column, final int row) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint(ROW_LABEL_WIDTH_px + GRID_PADDING_px + column * CELL_WIDTH_px + CELL_PADDING_px,
						GRID_PADDING_px + row * CELL_HEIGHT_px + CELL_PADDING_px, CELL_WIDTH_px - 2 * CELL_PADDING_px,
						CELL_HEIGHT_px - 2 * CELL_PADDING_px);
			}
		});
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D graphics2d = (Graphics2D) graphics;

		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Clearing display
		graphics2d.setPaint(BACKGROUND_COLOR);
		graphics2d.fillRect(0, 0, getSize().width, getSize().height);

		graphics2d.setColor(m_emptyCellColor);
		for(int x = 0; x < getMatrixWidth(); x++) {
			for(int y = 0; y < getMatrixHeight(); y++) {
				int velocity = m_velocityMatrix[x][y];
				if(velocity > 0) {
					graphics2d.setPaint(m_filledCellColor);
				} else {
					graphics2d.setPaint(m_emptyCellColor);
				}
				graphics2d.fillRect(ROW_LABEL_WIDTH_px + GRID_PADDING_px + x * CELL_WIDTH_px + CELL_PADDING_px,
						GRID_PADDING_px + y * CELL_HEIGHT_px + CELL_PADDING_px, CELL_WIDTH_px - 2 * CELL_PADDING_px,
						CELL_HEIGHT_px - 2 * CELL_PADDING_px);
			}
		}

		graphics2d.setColor(LABEL_COLOR);
		int row = 0;
		for(String label : m_levelMapping.keySet()) {
			Rectangle2D labelMetrics = graphics2d.getFontMetrics().getStringBounds(label, graphics2d);
			int marginBottom_px = (CELL_HEIGHT_px - (int) labelMetrics.getHeight()) / 2;
			int y_px = GRID_PADDING_px + (row + 1) * CELL_HEIGHT_px - marginBottom_px;
			int x_px = ROW_LABEL_WIDTH_px - (int) labelMetrics.getWidth();
			graphics2d.drawString(label, x_px, y_px);
			row++;
		}

		for(int x = 0; x < m_velocityMatrix.length; x++) {
			for(int y = 0; y < m_velocityMatrix[x].length; y++) {
				int velocity = m_velocityMatrix[x][y];
				if(velocity > 0) {
					graphics2d.setPaint(m_filledCellColor);
				} else {
					graphics2d.setPaint(m_emptyCellColor);
				}

				graphics2d.fillRect(ROW_LABEL_WIDTH_px + GRID_PADDING_px + x * CELL_WIDTH_px + CELL_PADDING_px,
						GRID_PADDING_px + y * CELL_HEIGHT_px + CELL_PADDING_px, CELL_WIDTH_px - 2 * CELL_PADDING_px,
						CELL_HEIGHT_px - 2 * CELL_PADDING_px);
			}
		}
	}

}