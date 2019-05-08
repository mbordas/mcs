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

	// Graphic
	private Image m_image;
	private Graphics2D m_graphics2D;

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

		setDoubleBuffered(false);

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
	}

	private boolean isInGrid(int columns, int row) {
		if(columns < 0 || columns >= getMatrixWidth()) {
			return false;
		}
		if(row < 0 || row >= getMatrixHeight()) {
			return false;
		}
		return true;
	}

	private void write(int column, int row) {
		System.out.println("write " + column + "," + row);
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
	private static int pixel2column(int mouseX_px) {
		int xInGrid_px = mouseX_px - (ROW_LABEL_WIDTH_px + GRID_PADDING_px);
		if(xInGrid_px < 0) {
			// Avoids dividing a negative but small number by CELL_WIDTH_px
			// which could given result = 0, still in Grid.
			return -1;
		} else {
			return xInGrid_px / CELL_WIDTH_px;
		}
	}

	/**
	 * Computes row index (going from top to bottom) in grid from the relative y-axis coordinate of mouse in that {@link MGrid}
	 * component.
	 *
	 * @param mouseY_px
	 * @return
	 */
	private static int pixel2row(int mouseY_px) {
		return (mouseY_px - GRID_PADDING_px) / CELL_HEIGHT_px;
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

					int column = pixel2column(event.getX());
					int row = pixel2row(event.getY());

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

				int column = pixel2column(event.getX());
				int row = pixel2row(event.getY());

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
		int column = pixel2column(event.getX());
		int row = pixel2row(event.getY());

		if(isInGrid(column, row)) {
			if(m_graphics2D != null) {
				if(m_mode == EditionMode.MOVE) {
					return true;
				}

				if(m_mode == EditionMode.WRITE) {
					write(column, row);
					m_graphics2D.setPaint(m_filledCellColor);
				} else if(m_mode == EditionMode.ERASE) {
					erase(column, row);
					m_graphics2D.setPaint(m_emptyCellColor);
				}

				m_graphics2D.fillRect(ROW_LABEL_WIDTH_px + GRID_PADDING_px + column * CELL_WIDTH_px + CELL_PADDING_px,
						GRID_PADDING_px + row * CELL_HEIGHT_px + CELL_PADDING_px, CELL_WIDTH_px - 2 * CELL_PADDING_px,
						CELL_HEIGHT_px - 2 * CELL_PADDING_px);

				repaint();
			}

			return true;
		} else {
			return false;
		}
	}

	//
	// Swing UI
	//

	protected void paintComponent(Graphics g) {
		if(m_image == null) {
			m_image = createImage(getSize().width, getSize().height);
			m_graphics2D = (Graphics2D) m_image.getGraphics();
			// Enable antialiasing
			m_graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			updateDisplay();
		}

		g.drawImage(m_image, 0, 0, null);
	}

	private void updateDisplay() {
		if(m_graphics2D != null) {

			// Clearing display
			m_graphics2D.setPaint(BACKGROUND_COLOR);
			m_graphics2D.fillRect(0, 0, getSize().width, getSize().height);

			m_graphics2D.setColor(m_emptyCellColor);
			for(int x = 0; x < getMatrixWidth(); x++) {
				for(int y = 0; y < getMatrixHeight(); y++) {
					int velocity = m_velocityMatrix[x][y];
					if(velocity > 0) {
						m_graphics2D.setPaint(m_filledCellColor);
					} else {
						m_graphics2D.setPaint(m_emptyCellColor);
					}
					m_graphics2D.fillRect(ROW_LABEL_WIDTH_px + GRID_PADDING_px + x * CELL_WIDTH_px + CELL_PADDING_px,
							GRID_PADDING_px + y * CELL_HEIGHT_px + CELL_PADDING_px, CELL_WIDTH_px - 2 * CELL_PADDING_px,
							CELL_HEIGHT_px - 2 * CELL_PADDING_px);
				}
			}

			m_graphics2D.setColor(LABEL_COLOR);
			int row = 0;
			for(String label : m_levelMapping.keySet()) {
				Rectangle2D labelMetrics = m_graphics2D.getFontMetrics().getStringBounds(label, m_graphics2D);
				int marginBottom_px = (CELL_HEIGHT_px - (int) labelMetrics.getHeight()) / 2;
				int y_px = GRID_PADDING_px + (row + 1) * CELL_HEIGHT_px - marginBottom_px;

				int x_px = ROW_LABEL_WIDTH_px - (int) labelMetrics.getWidth();
				m_graphics2D.drawString(label, x_px, y_px);
				row++;
			}

			repaint();
		}
	}

}