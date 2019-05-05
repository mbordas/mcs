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

import mcs.MSequencer;
import mcs.gui.components.MButton;
import mcs.gui.components.PhraseGrid;
import mcs.melody.Block;
import mcs.melody.Chord;
import mcs.midi.MidiUtils;
import mcs.pattern.MelodicPattern;
import mcs.pattern.Phrase;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class PhraseEditor {

	MButton m_playBtn;
	PhraseGrid m_grid;

	private Phrase m_phrase;
	MSequencer m_sequencer;

	public PhraseEditor(MSequencer sequencer, Phrase phrase) {
		m_sequencer = sequencer;
		m_phrase = phrase;
	}

	ActionListener m_actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == m_playBtn) {
				play();
			}
		}
	};

	public void show() {
		JFrame frame = new JFrame("Phrase Editor");
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout());

		// Controls
		JPanel controls = new JPanel();
		m_playBtn = new MButton("Play");
		m_playBtn.addActionListener(m_actionListener);

		controls.add(m_playBtn);

		content.add(controls, BorderLayout.NORTH);

		m_grid = new PhraseGrid(m_phrase);
		content.add(m_grid, BorderLayout.CENTER);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void play() {
		for(int c = 0; c < m_phrase.getLength(); c++) {

			int[] chord = Chord.byName(m_phrase.getChord(c), 3);

			for(Map.Entry<Phrase.Instrument, MelodicPattern> entry : m_phrase.getMelodicPatterns(c).entrySet()) {
				int channel = entry.getKey().channel;
				MelodicPattern pattern = entry.getValue();
				Block block = pattern.toBlock(channel, chord);
			}
		}
	}

	public static void main(String[] args) throws MidiUnavailableException {
		Phrase phrase = new Phrase();
		phrase.setChord(0, "E7");
		phrase.setChord(1, "A7");
		phrase.setChord(2, "E7");
		phrase.setChord(3, "%");

		phrase.addInstrument("Piano", 0);

		UIUtils.DEBUG = false;

		MidiDevice device = MidiUtils.getMidiOutDevice();

		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		device.open();
		Receiver receiver = device.getReceiver();

		MSequencer sequencer = new MSequencer(receiver, 60);

		new PhraseEditor(sequencer, phrase).show();
	}
}