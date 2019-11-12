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

package mcs.gui.components;

import mcs.graphics.DPI;
import mcs.graphics.MGraphics;
import mcs.gui.Theme;
import mcs.melody.Chord;
import mcs.melody.Note;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ScoreFragment extends MComponent implements MouseMotionListener {

	static Font FONT_BRAVURA = new Font("Bravura", Font.PLAIN, 32);

	public static final String UNICODE_TREBLE_CLEF = "\uD834\uDD1E";
	public static final String UNICODE_BASS_CLEF = "\uD834\uDD1A";
	public static final String UNICODE_BLACK_DOT = "\uD834\uDD58";
	public static final String UNICODE_DOT_RIGHT_BAR = "\uD834\uDD65";
	public static final String UNICODE_5_LINES = "\uD834\uDD1A";
	public static final String UNICODE_1_LINE = "\uD834\uDD16";

	public static final int WIDTH_px = 120;
	public static final int PADDING_px = 20;

	final int m_noteMin;
	final int m_noteMax;
	final int m_lowC; // C just under the lines

	private Integer m_note;
	private boolean m_isDebugEnabled = false;
	private String m_debugText = "";

	public ScoreFragment(int noteMin, int noteMax) {
		m_noteMin = noteMin;
		m_noteMax = noteMax;
		m_lowC = Note.getNoteInRange(Note.C0, noteMin, noteMax);

		int width_px = WIDTH_px + 2 * PADDING_px;
		int height_px = 2 * WIDTH_px + 2 * PADDING_px;

		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));

		addMouseMotionListener(this);
	}

	public void setNote(int note) {
		m_note = note;
		updateDisplay();
	}

	int getNoteRange() {
		return m_noteMax - m_noteMin;
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		// Clearing display
		graphics.setPaint(MGrid.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		int zoneWidth_px = getWidth() - 2 * PADDING_px;
		int zoneHeight_px = getHeight() - 2 * PADDING_px;

		graphics.setPaint(Theme.CHORD_DIAGRAM_BACKGROUND_COLOR);
		graphics.fillRect(PADDING_px, PADDING_px, zoneWidth_px, zoneHeight_px);

		// Printing clef and note
		graphics.setPaint(Theme.CHORD_DIAGRAM_COLOR);
		graphics.setFont(FONT_BRAVURA);
		graphics.setFontSize(128);

		Rectangle zone = new Rectangle(PADDING_px, PADDING_px, zoneWidth_px, zoneHeight_px);

		int x_px = PADDING_px;
		drawLines(graphics, zone, x_px, 2);
		x_px += drawTrebleClef(graphics, zone, x_px);
		x_px += drawNote(graphics, zone, x_px, m_note);

		// Debug text (mouse position
		if(m_isDebugEnabled) {
			graphics.setPaint(Color.white);
			graphics.setFont(null);
			graphics.drawStringLeft(m_debugText, 0, 0, getWidth(), 20, 5);
		}
	}

	/**
	 * @param graphics
	 * @param zone
	 * @param x
	 * @return The width of drawn symbol in pixel.
	 */
	int drawTrebleClef(MGraphics graphics, Rectangle zone, int x) {
		return drawSymbol(graphics, zone, x, UNICODE_TREBLE_CLEF, 100);
	}

	/**
	 * @param graphics
	 * @param zone
	 * @param x
	 * @param n
	 * @return The width of drawn symbol in pixel.
	 */
	int drawLines(MGraphics graphics, Rectangle zone, int x, int n) {
		int width_px = 0;
		for(int _n = 0; _n < n; _n++) {
			width_px += drawSymbol(graphics, zone, x + width_px, UNICODE_5_LINES, 100);
		}
		return width_px;
	}

	/**
	 * @param graphics
	 * @param zone
	 * @param x
	 * @param note
	 * @return The width of drawn symbol in pixel.
	 */
	int drawNote(MGraphics graphics, Rectangle zone, int x, int note) {

		// Computing the y distance from F (Bravura font's centered key) in pixel
		int distanceNfromF = getDistanceFromCenterF(note); // Distance in white notes
		int distanceFromF_px = distanceToPx(distanceNfromF);

		// We draw the note in a new zone that is shifted with y distance in pixel
		Rectangle _zone = new Rectangle(zone.x, zone.y + 2 - distanceFromF_px, zone.width, zone.height);
		drawSymbol(graphics, _zone, x + 33, UNICODE_DOT_RIGHT_BAR, 40);
		int width_px = drawSymbol(graphics, _zone, x, UNICODE_BLACK_DOT, 100);

		// If note is on top on on bottom of 5 lines group, we must draw additional lines
		int distanceCfromG = getDistanceFromCenterF(m_lowC) - 1; // Distance from G = distance from F - 1
		int distanceAfromG = getDistanceFromCenterF(m_lowC + Chord.MAJOR_SIXTH + Chord.OCTAVE) - 1;
		int distanceNfromG = distanceNfromF - 1;

		Stroke previousStroke = graphics.getStroke();
		graphics.setStroke_px(2);
		if(distanceNfromG <= distanceCfromG) { // Note is C or under
			for(int i = distanceCfromG; i >= distanceNfromG; i = i - 2) {
				int y_px = zone.y + zone.height / 2 - (i / 2) * 25;
				graphics.drawLine(zone.x + x - 5, y_px, zone.x + x + 45, y_px);
			}
		} else if(distanceNfromG >= distanceAfromG) { // Note is C or under
			for(int i = distanceAfromG; i <= distanceNfromG; i = i + 2) {
				int y_px = zone.y + zone.height / 2 - (i / 2) * 25;
				graphics.drawLine(zone.x + x - 5, y_px, zone.x + x + 45, y_px);
			}
		}
		graphics.setStroke(previousStroke);

		return width_px;
	}

	int getDistanceFromCenterF(int note) {
		return getDistance(m_lowC + Chord.PERFECT_FOURTH, note);
	}

	int distanceToPx(int distance) {
		return (25 * distance) / 2;
	}

	int drawSymbol(MGraphics graphics, Rectangle zone, int x, String code, int symbolWidth_px) {
		int fontShiftY_px = -52; // Bravura is printed a bit too low by default. Here we apply a constant shift.
		graphics.drawStringLeft(code, zone.x, zone.y + fontShiftY_px, zone.width, zone.height, x);
		return symbolWidth_px;
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
		if(!m_isDebugEnabled) {
			return;
		}

		int x_px = DPI.unScale(mouseEvent.getX());
		int y_px = DPI.unScale(mouseEvent.getY());
		m_debugText = String.format("x=%d , y=%d", x_px, y_px);
		updateDisplay();
	}

	/**
	 * Computes the number of degrees between given keys. Keys are flattened (# removed).
	 *
	 * @param startKey
	 * @param endKey
	 * @return
	 */
	public static int getDistance(int startKey, int endKey) {
		// Rounding keys to the nearest (lower) white key
		startKey = PianoKeyboard.isWhite(startKey) ? startKey : startKey - 1;
		endKey = PianoKeyboard.isWhite(endKey) ? endKey : endKey - 1;

		// Computing distance of each from C0
		// Moving notes in same octave
		int normStartKey = Note.getNoteInRange(startKey, Note.C0, Note.B0);
		int normEndKey = Note.getNoteInRange(endKey, Note.C0, Note.B0);

		int normDistA = 0;
		int normDistB = 0;
		int[] majorScale = Chord.majorScale();
		for(int i = 0; i < majorScale.length; i++) {
			if(normStartKey == Note.C0 + majorScale[i]) {
				normDistA = i;
			}
			if(normEndKey == Note.C0 + majorScale[i]) {
				normDistB = i;
			}
		}

		// Computing absolute distances from C0
		int distA = normDistA + 7 * ((startKey - normStartKey) / 12);
		int distB = normDistB + 7 * ((endKey - normEndKey) / 12);

		return distB - distA;
	}
}