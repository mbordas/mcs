/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui;

import mcs.MSequencer;
import mcs.devices.Roland_FP30;
import mcs.gui.components.ClosedComponentListener;
import mcs.gui.components.MButton;
import mcs.gui.components.MGrid;
import mcs.gui.components.MelodicGrid;
import mcs.melody.Block;
import mcs.melody.Chord;
import mcs.melody.Note;
import mcs.melody.Time;
import mcs.midi.Message;
import mcs.midi.MidiInterface;
import mcs.midi.Tone;
import mcs.pattern.MelodicPattern;
import mcs.utils.StringUtils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

public class MelodicPatternEditor implements WindowListener {

	public static final int CHANNEL = 0;
	public static final int OCTAVE = 3;

	JButton m_clearBtn, m_playBtn, m_stopBtn;
	ClosedComponentListener m_closedComponentListener;

	MelodicGrid m_grid;

	MSequencer m_sequencer;

	int[] m_chord;

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

	public MelodicPatternEditor(MSequencer sequencer, int[] chord, MelodicPattern pattern) {
		m_sequencer = sequencer;
		m_chord = chord;

		Time.TimeSignature timeSignature = MelodicPattern.DEFAULT_TIME_SIGNATURE;
		int bars = 1;
		int ticksPerBeat = 4;
		if(pattern != null) {
			timeSignature = pattern.getTimeSignature();
			bars = pattern.getBars();
			ticksPerBeat = pattern.getTicksPerBeat();
		}

		m_grid = new MelodicGrid(timeSignature, bars, ticksPerBeat);
		if(pattern != null) {
			m_grid.write(pattern);
		}

		m_grid.setRowClickListener(new MGrid.RowClickListener() {
			@Override
			public void onPress(int interval) {
				int note = m_chord[interval - 1];
				if(note == Note.NULL) {
					return;
				}

				try {
					m_sequencer.pressNote(CHANNEL, note, Note.Dynamic.MEZZO_FORTE.velocity);
				} catch(InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onRelease(int interval) {
				int note = m_chord[interval - 1];
				if(note == Note.NULL) {
					return;
				}

				try {
					m_sequencer.releaseNote(CHANNEL, note);
				} catch(InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
		});

		System.out.println(m_grid.toPattern().getContent());
	}

	public void setClosedComponentListener(ClosedComponentListener listener) {
		m_closedComponentListener = listener;
	}

	/**
	 * Changes the chord used for 'play'. Pattern is written but not linked to this editor, it will not be modified.
	 *
	 * @param chord
	 * @param pattern
	 */
	public void set(int[] chord, MelodicPattern pattern) {
		m_chord = chord;
		m_grid.eraseAll();
		m_grid.write(pattern);

		System.out.println("Set pattern:\n" + pattern.getContent());
	}

	/**
	 * Builds a {@link MelodicPattern} from written grid.
	 *
	 * @return
	 */
	public MelodicPattern toPattern() {
		return m_grid.toPattern();
	}

	/**
	 * Shows up this editor's window.
	 *
	 * @param exitOnClose If 'true', then the program will exit. If 'false', then the window will only be hidden.
	 */
	public void show(boolean exitOnClose) {
		// create main frame
		JFrame frame = new JFrame("Melodic Pattern Editor");
		frame.addWindowListener(this);
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
		if(exitOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		}
		// show the swing paint result
		frame.setVisible(true);
	}

	/**
	 * Uses its {@link MSequencer} and m'chord' to play the current written pattern.
	 */
	void play() {
		try {
			MelodicPattern melodicPattern = m_grid.toPattern();
			System.out.println("Pattern:\n" + melodicPattern.getContent());

			System.out.println("chord=" + StringUtils.toString(m_chord, ","));

			Block block = melodicPattern.toBlock(CHANNEL, m_chord);
			System.out.println("Block:\n" + block.getContent());

			for(long tick = 0; tick < block.size(); tick++) {
				System.out.println("t=" + tick);
				for(ShortMessage message : block.toMessages(tick)) {
					System.out.println(Message.toString(message));
				}
			}

			m_sequencer.set(block);
			m_sequencer.enableLooping(true);
			m_sequencer.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the {@link MSequencer} playback.
	 */
	void stop() {
		try {
			m_sequencer.stop();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs a standalone editor.
	 *
	 * @param args
	 * @throws MidiUnavailableException
	 * @throws InvalidMidiDataException
	 * @throws IOException
	 */
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException {

		// Loading pattern
		MelodicPattern pattern = null;
		if(args.length >= 1) {
			File patternFile = new File(args[0]);
			pattern = MelodicPattern.load(patternFile);
		}

		MidiDevice device = MidiInterface.getMidiOutDevice();

		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		device.open();
		Receiver receiver = device.getReceiver();

		Tone.selectInstrument(receiver, CHANNEL, Roland_FP30.GRAND_PIANO_1);

		MSequencer sequencer = new MSequencer(receiver, 100);

		new MelodicPatternEditor(sequencer, Chord.Km(Note.A3), pattern).show(true);
	}

	@Override
	public void windowOpened(WindowEvent windowEvent) {
	}

	@Override
	public void windowClosing(WindowEvent windowEvent) {
	}

	@Override
	public void windowClosed(WindowEvent windowEvent) {
	}

	@Override
	public void windowIconified(WindowEvent windowEvent) {
	}

	@Override
	public void windowDeiconified(WindowEvent windowEvent) {
	}

	@Override
	public void windowActivated(WindowEvent windowEvent) {
	}

	@Override
	/**
	 * This method is called when the window is closed using the cross.
	 */
	public void windowDeactivated(WindowEvent windowEvent) {
		if(m_closedComponentListener != null) {
			m_closedComponentListener.onClosed(this);
		}
	}
}