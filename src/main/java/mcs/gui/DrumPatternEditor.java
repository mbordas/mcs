/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui;

import mcs.MSequencer;
import mcs.melody.Time;
import mcs.midi.Drum;
import mcs.midi.MidiUtils;
import mcs.pattern.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrumPatternEditor {

	JButton m_clearBtn, m_playBtn;

	DrumGrid m_drumGrid;

	ActionListener m_actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == m_clearBtn) {
				m_drumGrid.clear();
			} else if(e.getSource() == m_playBtn) {
				play();
			}
		}
	};

	public void show() {
		// create main frame
		JFrame frame = new JFrame("Drum Pattern Editor");
		Container content = frame.getContentPane();
		// set layout on content pane
		content.setLayout(new BorderLayout());
		// create draw area
		m_drumGrid = new DrumGrid(new Time.TimeSignature(4, 4), 1, Drum.getBasicKeyMapping());

		// add to content pane
		content.add(m_drumGrid, BorderLayout.CENTER);

		// create controls to apply colors and call clear feature
		JPanel controls = new JPanel();

		m_clearBtn = new JButton("Clear");
		m_clearBtn.addActionListener(m_actionListener);
		m_playBtn = new JButton("Play");
		m_playBtn.addActionListener(m_actionListener);

		// add to panel
		controls.add(m_clearBtn);
		controls.add(m_playBtn);

		// add to content pane
		content.add(controls, BorderLayout.NORTH);

		frame.pack();
		// can close frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// show the swing paint result
		frame.setVisible(true);
	}

	void play() {
		Pattern pattern = m_drumGrid.toPattern(Drum.CHANNEL);

		System.out.println("Pattern size: " + pattern.size());

		MidiDevice device = null;
		try {
			device = MidiUtils.getMidiOutDevice();
			if(device == null) {
				device = MidiSystem.getSynthesizer();
			}

			device.open();
			Receiver receiver = device.getReceiver();

			MSequencer sequencer = new MSequencer(receiver, 60);
			sequencer.append(pattern);

			sequencer.start();

			long patternDuration_ms = pattern.getDuration_ms(sequencer.getTempo_bpm());
			System.out.println("Pattern duration: " + patternDuration_ms + " ms");

			Thread.sleep(patternDuration_ms);

			sequencer.stop();

		} catch(InterruptedException e) {
			e.printStackTrace();
		} catch(MidiUnavailableException e) {
			e.printStackTrace();
		} finally {
			if(device != null) {
				device.close();
			}
		}
	}

	public static void main(String[] args) {
		new DrumPatternEditor().show();
	}
}