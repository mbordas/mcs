/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.gui.UIUtils;
import mcs.pattern.Phrase;

import javax.swing.*;
import java.awt.*;

public class PhraseGrid extends JComponent {

	public static final int CELL_WIDTH_px = 80;
	public static final int CHORD_CELL_HEIGHT_px = 70;
	public static final int PATTERN_CELL_HEIGHT_px = 50;
	public static final int GRID_PADDING_px = 10;
	public static final int CELL_PADDING_px = 2;
	public static final int ROW_LABEL_WIDTH_px = 200;

	public static final int LABEL_RIGHT_PADDING_px = 10;

	// Graphic
	private Image m_image;
	private Graphics2D m_graphics2D;

	// Music
	private Phrase m_phrase;

	public PhraseGrid(Phrase phrase) {
		m_phrase = phrase;

		setSize(getSize());
		setPreferredSize(getSize());
	}

	@Override
	public Dimension getSize() {
		int width_px = ROW_LABEL_WIDTH_px + 2 * GRID_PADDING_px + m_phrase.getLength() * CELL_WIDTH_px;
		int height_px = 2 * GRID_PADDING_px + CHORD_CELL_HEIGHT_px + m_phrase.getInstruments().size() * PATTERN_CELL_HEIGHT_px;
		return new Dimension(width_px, height_px);
	}

	private void clear() {
		// Clearing display
		m_graphics2D.setPaint(MGrid.BACKGROUND_COLOR);
		m_graphics2D.fillRect(0, 0, getSize().width, getSize().height);

		m_graphics2D.setColor(MGrid.LABEL_COLOR);

		// Displaying rows labels
		// * chords line
		UIUtils.drawStringRight(m_graphics2D, "Chords", GRID_PADDING_px, GRID_PADDING_px, ROW_LABEL_WIDTH_px, CHORD_CELL_HEIGHT_px,
				LABEL_RIGHT_PADDING_px);
		// * then instruments lines
		{
			int i = 0;
			for(Phrase.Instrument instrument : m_phrase.getInstruments()) {
				int heightInstruments_px = GRID_PADDING_px + CHORD_CELL_HEIGHT_px + i * PATTERN_CELL_HEIGHT_px;
				UIUtils.drawStringRight(m_graphics2D, instrument.label, GRID_PADDING_px, heightInstruments_px, ROW_LABEL_WIDTH_px,
						PATTERN_CELL_HEIGHT_px, LABEL_RIGHT_PADDING_px);
				i++;
			}
		}

		// Displaying chords
		for(int i = 0; i < m_phrase.getLength(); i++) {
			String chord = m_phrase.getChord(i);
			UIUtils.drawStringCenter(m_graphics2D, chord, GRID_PADDING_px + ROW_LABEL_WIDTH_px + i * CELL_WIDTH_px, GRID_PADDING_px,
					CELL_WIDTH_px, CHORD_CELL_HEIGHT_px);
		}

		repaint();
	}

	protected void paintComponent(Graphics g) {
		if(m_image == null) {
			m_image = createImage(getSize().width, getSize().height);
			m_graphics2D = (Graphics2D) m_image.getGraphics();
			m_graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			clear();
		}

		g.drawImage(m_image, 0, 0, null);
	}
}