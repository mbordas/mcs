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

import mcs.events.KeyListener;
import mcs.gui.components.PianoKeyboard;
import mcs.gui.components.ScoreFragment;
import mcs.melody.Chord;
import mcs.melody.Note;

import javax.swing.*;
import java.awt.*;

public class PianoTrainer implements KeyListener {

	static int NOTE_MAX = Note.A2;
	static int NOTE_MIN = Note.C1;

	JFrame m_frame;

	ScoreFragment m_score;
	PianoKeyboard m_keyboard;

	int m_note;
	int m_points = 0;
	int m_timeLeft_s = 60;

	public PianoTrainer() {
		m_note = getRandomNote();

		m_score = new ScoreFragment(NOTE_MIN, NOTE_MAX);
		m_score.setNote(m_note);

		m_keyboard = new PianoKeyboard();
		m_keyboard.setKeyListener(this);
	}

	void start(int timer_s) {
		m_points = 0;
		m_timeLeft_s = timer_s;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(m_timeLeft_s > 0) {
						Thread.sleep(1000);
						m_timeLeft_s--;
						updateTitle();
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	void updateTitle() {
		m_frame.setTitle(String.format("%d pts / %d s", m_points, m_timeLeft_s));
	}

	int getRandomNote() {
		int octaves = (ScoreFragment.getDistance(NOTE_MIN, NOTE_MAX) / 7) + 1;
		int note = 0;
		boolean isNOk = true;
		while(isNOk) {
			int interval = (int) (7 * Math.random());
			int octave = (int) (octaves * Math.random());
			note = NOTE_MIN + Chord.majorScale()[interval] + 12 * octave;
			isNOk = note == m_note || note < NOTE_MIN || note > NOTE_MAX;
		}
		return note;
	}

	public void show() {
		// create main frame
		m_frame = new JFrame();
		updateTitle();
		Container content = m_frame.getContentPane();
		// set layout on content pane
		content.setLayout(new BorderLayout());

		// add to content pane
		content.add(m_score, BorderLayout.NORTH);
		content.add(m_keyboard, BorderLayout.CENTER);

		m_frame.pack();
		// can close frame
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// show the swing paint result
		m_frame.setVisible(true);
	}

	@Override
	public void onNoteClicked(int key) {
		if(m_timeLeft_s <= 0) {
			return;
		}

		if(Note.getInterval(m_note, key) == Chord.ROOT) {
			System.out.println("you win");
			m_points++;
		} else {
			System.out.println("you loose");
			m_points--;
		}

		updateTitle();

		m_note = getRandomNote();
		m_score.setNote(m_note);
	}

	@Override
	public void onNotePressed(int key) {
	}

	@Override
	public void onNoteReleased(int key) {
	}

	public static void main(String[] args) {
		PianoTrainer trainer = new PianoTrainer();
		trainer.show();
		trainer.start(30);
	}
}