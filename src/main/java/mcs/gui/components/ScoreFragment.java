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

import mcs.graphics.MGraphics;
import mcs.gui.Theme;
import mcs.melody.Note;

import java.awt.*;

public class ScoreFragment extends MComponent {

	public static final int WIDTH_px = 120;
	public static final int LINE_HEIGHT_px = 16;
	public static final int PADDING_px = 20;

	public static final int NOTE_MAX = Note.G7;
	public static final int NOTE_MIN = Note.G5;
	public static final int NOTE_CENTER_G = Note.G6;

	private Integer m_note;

	public ScoreFragment() {
		int width_px = WIDTH_px + 2 * PADDING_px;
		int height_px = (getNoteRange() / 2) * LINE_HEIGHT_px + 2 * PADDING_px;

		setSize(width_px, height_px);
		setPreferredSize(new Dimension(width_px, height_px));
	}

	public void setNote(int note) {
		m_note = note;
	}

	int getNoteRange() {
		return NOTE_MAX - NOTE_MIN;
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		// Clearing display
		graphics.setPaint(MGrid.BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.setPaint(Theme.CHORD_DIAGRAM_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		// Painting lines
		graphics.setPaint(Theme.CHORD_DIAGRAM_COLOR);
		for(int _note = NOTE_CENTER_G - 4; _note <= NOTE_CENTER_G + 12; _note += 4) {
			int y_px = PADDING_px + noteToY_px(_note);
			graphics.drawLine(PADDING_px, y_px, PADDING_px + WIDTH_px, y_px);
		}

		// Painting note
		if(m_note != null) {
			int y_px = PADDING_px + noteToY_px(m_note);
			int xCenter_px = PADDING_px + WIDTH_px / 2;
			graphics.setPaint(Theme.CHORD_DIAGRAM_COLOR);
			graphics.fillOval(xCenter_px - LINE_HEIGHT_px / 2, y_px - LINE_HEIGHT_px / 2, LINE_HEIGHT_px, LINE_HEIGHT_px);
			if(m_note >= NOTE_CENTER_G + 4) {
				graphics.drawLine(xCenter_px - LINE_HEIGHT_px / 2, y_px, xCenter_px - LINE_HEIGHT_px / 2, y_px + 2 * LINE_HEIGHT_px);
			} else {
				graphics.drawLine(xCenter_px + LINE_HEIGHT_px / 2, y_px, xCenter_px + LINE_HEIGHT_px / 2, y_px - 2 * LINE_HEIGHT_px);
			}
		}
	}

	private int noteToY_px(int note) {
		int notesUnderMax = NOTE_MAX - note;
		return notesUnderMax * LINE_HEIGHT_px / 4;
	}
}