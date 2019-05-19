/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import mcs.MSequencer;
import mcs.gui.MelodicPatternEditor;
import mcs.gui.UIUtils;
import mcs.melody.Chord;
import mcs.melody.Note;
import mcs.pattern.MelodicPattern;
import mcs.pattern.Phrase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PhraseGrid extends JComponent implements ClosedComponentListener {

	public static final int CELL_WIDTH_px = 80;
	public static final int CHORD_CELL_HEIGHT_px = 70;
	public static final int PATTERN_CELL_HEIGHT_px = 50;
	public static final int GRID_PADDING_px = 10;
	public static final int ROW_LABEL_WIDTH_px = 200;

	public static final int LABEL_RIGHT_PADDING_px = 10;

	// Music
	private final Phrase m_phrase;
	private final MSequencer m_sequencer;

	// Editors
	private MelodicPatternEditor m_melodicPatternEditor;
	private int m_editingColumn = -1;
	private int m_editingRow = -1;

	public PhraseGrid(Phrase phrase, MSequencer sequencer) {
		m_phrase = phrase;
		m_sequencer = sequencer;

		addMouseListener(buildMouseListener());

		setSize(getSize());
		setPreferredSize(getSize());
	}

	@Override
	public Dimension getSize() {
		int width_px = ROW_LABEL_WIDTH_px + 2 * GRID_PADDING_px + m_phrase.getLength() * CELL_WIDTH_px;
		int height_px = 2 * GRID_PADDING_px + CHORD_CELL_HEIGHT_px + m_phrase.getInstruments().size() * PATTERN_CELL_HEIGHT_px;
		return new Dimension(width_px, height_px);
	}

	/**
	 * Shows up the {@link MelodicPatternEditor}'s window on the selected {@link MelodicPattern}.
	 *
	 * @param column The column index in the phrase's grid.
	 * @param row    The row index of the melodic instrument in the phrase's grid.
	 */
	private void openMelodicEditor(int column, int row) {
		m_editingColumn = column;
		m_editingRow = row;

		int[] chord = Chord.byName(m_phrase.getChord(column), Phrase.DEFAULT_OCTAVE);
		MelodicPattern pattern = m_phrase.getMelodicPattern(row, column);
		if(pattern == null) {
			pattern = new MelodicPattern(MelodicPattern.DEFAULT_TIME_SIGNATURE, 4);
			m_phrase.set(row, column, pattern);
		}

		if(m_melodicPatternEditor == null) {
			m_melodicPatternEditor = new MelodicPatternEditor(m_sequencer, chord, pattern);
			m_melodicPatternEditor.setClosedComponentListener(this);
		}
		m_melodicPatternEditor.set(chord, pattern);
		m_melodicPatternEditor.show(false);
	}

	@Override
	/**
	 * This method is called when the {@link MelodicPatternEditor} is closed (window is only hidden).
	 */
	public void onClosed(Object component) {
		MelodicPattern pattern = m_melodicPatternEditor.toPattern();
		System.out.println("Saving pattern:" + pattern.toBlock(0, Chord.K(Note.C2)));
		m_phrase.set(m_editingRow, m_editingColumn, pattern);
	}

	//
	// Mouse management
	//

	private MouseListener buildMouseListener() {
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent event) {
				int column = MGrid.pixel2column(event.getX(), ROW_LABEL_WIDTH_px, CELL_WIDTH_px, GRID_PADDING_px);

				// The height of chords cell must be removed
				int row = MGrid.pixel2row(event.getY() - CHORD_CELL_HEIGHT_px, PATTERN_CELL_HEIGHT_px, GRID_PADDING_px);

				if(MGrid.isInGrid(column, row, m_phrase.getLength(), m_phrase.getInstruments().size())) {
					openMelodicEditor(column, row);
				}
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent) {

			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent) {

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
	// Custom graphics
	//

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D graphics2D = ((Graphics2D) graphics);

		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		clear(graphics2D);
	}

	private void clear(Graphics2D graphics2D) {
		// Clearing display
		graphics2D.setPaint(MGrid.BACKGROUND_COLOR);
		graphics2D.fillRect(0, 0, getSize().width, getSize().height);

		graphics2D.setColor(MGrid.LABEL_COLOR);

		// Displaying rows labels
		// * chords line
		UIUtils.drawStringRight(graphics2D, "Chords", GRID_PADDING_px, GRID_PADDING_px, ROW_LABEL_WIDTH_px, CHORD_CELL_HEIGHT_px,
				LABEL_RIGHT_PADDING_px);
		// * then instruments lines
		{
			int i = 0;
			for(Phrase.Instrument instrument : m_phrase.getInstruments()) {
				int heightInstruments_px = GRID_PADDING_px + CHORD_CELL_HEIGHT_px + i * PATTERN_CELL_HEIGHT_px;
				UIUtils.drawStringRight(graphics2D, instrument.label, GRID_PADDING_px, heightInstruments_px, ROW_LABEL_WIDTH_px,
						PATTERN_CELL_HEIGHT_px, LABEL_RIGHT_PADDING_px);
				i++;
			}
		}

		// Displaying chords
		for(int i = 0; i < m_phrase.getLength(); i++) {
			String chord = m_phrase.getChord(i);
			UIUtils.drawStringCenter(graphics2D, chord, GRID_PADDING_px + ROW_LABEL_WIDTH_px + i * CELL_WIDTH_px, GRID_PADDING_px,
					CELL_WIDTH_px, CHORD_CELL_HEIGHT_px);
		}
	}

}