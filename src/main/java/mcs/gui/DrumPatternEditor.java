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
import mcs.gui.components.DrumGrid;
import mcs.gui.components.MButton;
import mcs.gui.components.MGrid;
import mcs.melody.Block;
import mcs.melody.Note;
import mcs.melody.Time;
import mcs.midi.Drum;
import mcs.midi.MidiInterface;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrumPatternEditor {

	JButton m_clearBtn, m_playBtn, m_stopBtn;

	DrumGrid m_grid;

	MSequencer m_sequencer;

	public DrumPatternEditor(MSequencer sequencer) {
		m_sequencer = sequencer;

		m_grid = new DrumGrid(new Time.TimeSignature(4, 4), 1, 4, Drum.getBasicKeyMapping());

		m_grid.setRowClickListener(new MGrid.RowClickListener() {
			@Override
			public void onPress(int key) {
				try {
					m_sequencer.pressNote(Drum.CHANNEL, key, Note.Dynamic.MEZZO_FORTE.velocity);
				} catch(InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onRelease(int key) {
				try {
					m_sequencer.releaseNote(Drum.CHANNEL, key);
				} catch(InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
		});
	}

	ActionListener m_actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == m_clearBtn) {
				m_grid.eraseAll();
			} else if(e.getSource() == m_playBtn) {
				play();
			} else if(e.getSource() == m_stopBtn) {
				stop();
			}
		}
	};

	public void show() {
		// create main frame
		JFrame frame = new JFrame("Drum Pattern Editor");
		Container content = frame.getContentPane();
		// set layout on content pane
		content.setLayout(new BorderLayout());

		// add to content pane
		content.add(m_grid, BorderLayout.CENTER);

		// create controls to apply colors and call clear feature
		JPanel controls = new JPanel();

		m_clearBtn = new MButton("Clear");
		m_clearBtn.addActionListener(m_actionListener);
		m_playBtn = new MButton("Play");
		m_playBtn.addActionListener(m_actionListener);
		m_stopBtn = new MButton("Stop");
		m_stopBtn.addActionListener(m_actionListener);

		// add to panel
		controls.add(m_clearBtn);
		controls.add(m_playBtn);
		controls.add(m_stopBtn);

		// add to content pane
		content.add(controls, BorderLayout.NORTH);

		frame.pack();
		// can close frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// show the swing paint result
		frame.setVisible(true);
	}

	void play() {
		Block block = m_grid.toBlock(Drum.CHANNEL);
		System.out.println("Block size: " + block.size());
		m_sequencer.set(block);
		m_sequencer.enableLooping(true);
		try {
			m_sequencer.start();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	void stop() {
		try {
			m_sequencer.stop();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws MidiUnavailableException {
		MidiDevice device = MidiInterface.getMidiOutDevice();

		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		device.open();
		Receiver receiver = device.getReceiver();

		MSequencer sequencer = new MSequencer(receiver, 100);

		new DrumPatternEditor(sequencer).show();
	}
}