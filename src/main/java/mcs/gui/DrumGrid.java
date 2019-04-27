/*Copyright (c) 2018-2019, Mathieu Bordas
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
2- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
3- Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package mcs.gui;

import mcs.melody.Note;
import mcs.melody.Time;
import mcs.pattern.Pattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;

public class DrumGrid extends JComponent {

	public static final int CELL_WIDTH_px = 200;
	public static final int CELL_HEIGHT_px = 200;
	public static final int MARGIN_px = 100;
	public static final int CELL_PADDING_px = 10;

	static Color m_backgroundColor = Color.darkGray;
	static Color m_emptyCellColor = Color.lightGray;
	static Color m_filledCellColor = Color.orange;

	// Graphic
	private Image m_image;
	private Graphics2D m_graphics2D;

	// Pattern
	Time.TimeSignature m_timeSignature;
	Map<String, Integer> m_keyMapping;
	int[][] m_velocityMatrix;
	int m_currentVelocity = Note.DEFAULT_VELOCITY;

	// Edition
	enum EditionMode {WRITE, ERASE, MOVE}

	int m_ticksPerBeat = 2;
	EditionMode m_mode = EditionMode.MOVE;

	public DrumGrid(Time.TimeSignature timeSignature, int bars, Map<String, Integer> keyMapping) {
		m_timeSignature = timeSignature;
		m_keyMapping = keyMapping;

		// Total number of raws in grid (x-axis)
		int columns = bars * timeSignature.getBeatsInBar() * m_ticksPerBeat;
		int rows = m_keyMapping.size();
		// Each available key is a line (y-axis)
		m_velocityMatrix = new int[columns][rows];
		for(int x = 0; x < columns; x++) {
			m_velocityMatrix[x] = new int[rows];
			for(int y = 0; y < rows; y++) {
				m_velocityMatrix[x][y] = 0;
			}
		}

		setDoubleBuffered(false);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
			}

			@Override
			public void mousePressed(MouseEvent event) {
				if(event.getButton() == 1) {
					m_mode = EditionMode.WRITE;
				} else if(event.getButton() == 3) {
					m_mode = EditionMode.ERASE;
				}

				System.out.println("edition mode: " + m_mode);

				onMouseAction(event);
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {
				m_mode = EditionMode.MOVE;
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent event) {
				onMouseAction(event);
			}
		});

		int width_px = 2 * MARGIN_px + getMatrixWidth() * CELL_WIDTH_px;
		int height_px = 2 * MARGIN_px + getMatrixHeight() * CELL_HEIGHT_px;
		System.out.println("Drum grid dimension: " + width_px + " x " + height_px);
		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));
	}

	int getMatrixWidth() {
		return m_velocityMatrix.length;
	}

	int getMatrixHeight() {
		return m_velocityMatrix[0].length;
	}

	boolean isInGrid(int columns, int row) {
		if(columns < 0 || columns >= getMatrixWidth()) {
			return false;
		}
		if(row < 0 || row >= getMatrixHeight()) {
			return false;
		}
		return true;
	}

	void write(int column, int row) {
		m_velocityMatrix[column][row] = m_currentVelocity;
	}

	void erase(int column, int row) {
		m_velocityMatrix[column][row] = 0;
	}

	void onMouseAction(MouseEvent event) {
		int column = pixel2column(event.getX());
		int row = pixel2row(event.getY());

		if(isInGrid(column, row)) {
			if(m_graphics2D != null) {
				if(m_mode == EditionMode.MOVE) {
					return;
				}

				if(m_mode == EditionMode.WRITE) {
					write(column, row);
					m_graphics2D.setPaint(m_filledCellColor);
				} else if(m_mode == EditionMode.ERASE) {
					erase(column, row);
					m_graphics2D.setPaint(m_emptyCellColor);
				}

				m_graphics2D.fillRect(MARGIN_px + column * CELL_WIDTH_px + CELL_PADDING_px,
						MARGIN_px + row * CELL_HEIGHT_px + CELL_PADDING_px, CELL_WIDTH_px - 2 * CELL_PADDING_px,
						CELL_HEIGHT_px - 2 * CELL_PADDING_px);
				repaint();
			}
		}
	}

	protected void paintComponent(Graphics g) {
		if(m_image == null) {
			m_image = createImage(getSize().width, getSize().height);
			m_graphics2D = (Graphics2D) m_image.getGraphics();
			// Enable antialiasing
			m_graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			clear();
		}

		g.drawImage(m_image, 0, 0, null);
	}

	public void clear() {
		// Clearing velocity matrix
		for(int x = 0; x < getMatrixWidth(); x++) {
			for(int y = 0; y < getMatrixHeight(); y++) {
				m_velocityMatrix[x][y] = 0;
			}
		}

		// Clearing display
		m_graphics2D.setPaint(m_backgroundColor);
		m_graphics2D.fillRect(0, 0, getSize().width, getSize().height);
		m_graphics2D.setColor(m_emptyCellColor);
		m_graphics2D.fillRect(MARGIN_px, MARGIN_px, getMatrixWidth() * CELL_WIDTH_px, getMatrixHeight() * CELL_HEIGHT_px);
		repaint();
	}

	public Pattern toPattern(int channel) {
		Pattern result = new Pattern(m_timeSignature, m_ticksPerBeat);

		int row = 0;
		for(int key : m_keyMapping.values()) {
			int _velocity = 0; // previous velocity
			int tickStart = -1;

			for(int tick = 0; tick < getMatrixWidth(); tick++) {
				int velocity = m_velocityMatrix[tick][row];
				if(velocity != _velocity) {
					// Velocity changes from previous cell
					if(velocity > 0) {
						tickStart = tick;
					} else {
						result.add(channel, key, _velocity, tickStart, tick);
					}
				}

				_velocity = velocity;
			}

			if(_velocity > 0) {
				result.add(channel, key, _velocity, tickStart, getMatrixWidth());
			}

			row++;
		}

		return result;
	}

	static int pixel2column(int mouseX_px) {
		return (mouseX_px - MARGIN_px) / CELL_WIDTH_px;
	}

	static int pixel2row(int mouseY_px) {
		return (mouseY_px - MARGIN_px) / CELL_HEIGHT_px;
	}
}