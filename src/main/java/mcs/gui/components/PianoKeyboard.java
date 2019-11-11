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

import mcs.events.KeyListener;
import mcs.graphics.DPI;
import mcs.graphics.MGraphics;
import mcs.gui.Theme;
import mcs.melody.Chord;
import mcs.melody.Note;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PianoKeyboard extends MComponent implements MouseListener {

	public static final int PADDING_px = 20;
	public static final int KEY_WHITE_HEIGHT_px = 200;
	public static final int KEY_BLACK_HEIGHT_px = 120;
	public static final int KEY_WHITE_WIDTH_px = 60;
	public static final int KEY_BLACK_WIDTH_px = KEY_WHITE_WIDTH_px / 2;

	private int m_octaves = 1;
	private int m_lowerNote = Note.C3;

	KeyListener m_listener;

	public PianoKeyboard() {
		int width_px = (m_octaves * 7) * KEY_WHITE_WIDTH_px + 2 * PADDING_px;
		int height_px = KEY_WHITE_HEIGHT_px + 2 * PADDING_px;

		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));

		addMouseListener(this);
	}

	public void setKeyListener(KeyListener listener) {
		m_listener = listener;
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		// Clearing display
		graphics.setPaint(MGrid.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		// Drawing white keys
		for(int octave = 0; octave < m_octaves; octave++) {
			// Drawing white keys
			for(int note = m_lowerNote + 12 * octave; note < m_lowerNote + 12 * (octave + 1); note++) {
				if(isWhite(note)) {
					int x_px = noteToX_px(note, m_lowerNote, KEY_WHITE_WIDTH_px);
					graphics.setPaint(Theme.PIANO_KEY_WHITE);
					graphics.fillRect(PADDING_px + x_px - KEY_WHITE_WIDTH_px / 2, PADDING_px, KEY_WHITE_WIDTH_px, KEY_WHITE_HEIGHT_px);
					graphics.setPaint(Theme.PIANO_KEY_BORDER);
					graphics.drawRect(PADDING_px + x_px - KEY_WHITE_WIDTH_px / 2, PADDING_px, KEY_WHITE_WIDTH_px, KEY_WHITE_HEIGHT_px);
				}
			}

			// Drawing black keys
			for(int note = m_lowerNote + 12 * octave; note < m_lowerNote + 12 * (octave + 1); note++) {
				if(!isWhite(note)) {
					int x_px = noteToX_px(note, m_lowerNote, KEY_WHITE_WIDTH_px);
					graphics.setPaint(Theme.PIANO_KEY_BLACK);
					graphics.fillRect(PADDING_px + x_px - KEY_BLACK_WIDTH_px / 2, PADDING_px, KEY_BLACK_WIDTH_px, KEY_BLACK_HEIGHT_px);
					graphics.setPaint(Theme.PIANO_KEY_BORDER);
					graphics.drawRect(PADDING_px + x_px - KEY_BLACK_WIDTH_px / 2, PADDING_px, KEY_BLACK_WIDTH_px, KEY_BLACK_HEIGHT_px);
				}
			}
		}
	}

	static boolean isWhite(int note) {
		int interval = Note.getInterval(Note.C0, note);
		for(int _interval : Chord.majorScale()) {
			if(interval == _interval) {
				return true;
			}
		}
		return false;
	}

	static int noteToX_px(int note, int lowerC, int whiteKeyWidth_px) {
		int interval = Note.getInterval(lowerC, note);
		int x_px;
		if(interval == Chord.ROOT) {
			x_px = whiteKeyWidth_px / 2;
		} else if(interval == Chord.MINOR_SECOND) {
			x_px = 2 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.MAJOR_SECOND) {
			x_px = 3 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.MINOR_THIRD) {
			x_px = 4 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.MAJOR_THIRD) {
			x_px = 5 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.PERFECT_FOURTH) {
			x_px = 7 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.DIMINISHED_FIFTH) {
			x_px = 8 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.PERFECT_FIFTH) {
			x_px = 9 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.MINOR_SIXTH) {
			x_px = 10 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.MAJOR_SIXTH) {
			x_px = 11 * whiteKeyWidth_px / 2;
		} else if(interval == Chord.MINOR_SEVENTH) {
			x_px = 12 * whiteKeyWidth_px / 2;
		} else {
			x_px = 13 * whiteKeyWidth_px / 2;
		}

		int octaves = (note - lowerC) / 12;
		x_px += 70 * octaves;
		return x_px;
	}

	/**
	 * Computes the note pressed on the keyboard when mouse is at 'x,y'. x and y are the relative coordinates in the keyboard's
	 * drawing zone (without padding).
	 *
	 * @param x
	 * @param y
	 * @param lowerC
	 * @param whiteKeyWidth_px
	 * @param blackKeyWidth_px
	 * @param blackKeyHeight_px
	 * @return
	 */
	static int xyToNote(int x, int y, int lowerC, int whiteKeyWidth_px, int blackKeyWidth_px, int blackKeyHeight_px) {
		// Index of the zone defined by white key's rectangle, ignoring overlapping black keys
		int octaveIndex = x / (7 * whiteKeyWidth_px);
		int whiteZoneIndex = (x % (7 * whiteKeyWidth_px)) / whiteKeyWidth_px;
		int xInWhiteZone_px = x % whiteKeyWidth_px;

		int whiteNote = lowerC + 12 * octaveIndex + Chord.majorScale()[whiteZoneIndex]; // Interval of the white note with same x

		if(y > blackKeyHeight_px) {
			// The note is white for sure
			return whiteNote;
		} else {
			if(xInWhiteZone_px < blackKeyWidth_px / 2) {
				if(isWhite(whiteNote - 1)) {
					return whiteNote; // No overlapping black key to the left
				} else {
					return whiteNote - 1; // The mouse is over the overlapping black key to the left
				}
			} else if(xInWhiteZone_px > whiteKeyWidth_px - (blackKeyWidth_px / 2)) {
				if(isWhite(whiteNote + 1)) {
					return whiteNote; // No overlapping black key to the right
				} else {
					return whiteNote + 1; // The mouse is over the overlapping black key to the right
				}
			} else {
				return whiteNote;
			}
		}
	}

	int toNote(MouseEvent event) {
		int x = DPI.unScale(event.getX() - PADDING_px);
		int y = DPI.unScale(event.getY() - PADDING_px);
		int note = xyToNote(x, y, m_lowerNote, KEY_WHITE_WIDTH_px, KEY_BLACK_WIDTH_px, KEY_BLACK_HEIGHT_px);
		return note;
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		if(m_listener != null) {
			m_listener.onNoteClicked(toNote(mouseEvent));
		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		if(m_listener != null) {
			m_listener.onNotePressed(toNote(mouseEvent));
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		if(m_listener != null) {
			m_listener.onNoteReleased(toNote(mouseEvent));
		}
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {
	}

	@Override
	public void mouseExited(MouseEvent mouseEvent) {
	}
}