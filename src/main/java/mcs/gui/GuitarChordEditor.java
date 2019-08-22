/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui;

import mcs.MSequencer;
import mcs.graphics.DPI;
import mcs.gui.components.ChordDiagram;
import mcs.gui.components.GuitarNeck;
import mcs.gui.components.MButton;
import mcs.melody.Block;
import mcs.melody.Note;
import mcs.melody.Time;
import mcs.midi.MidiInterface;
import mcs.pattern.GuitarPattern;
import mcs.utils.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GuitarChordEditor {

	public static final File PATTERN_DIR = new File("./pattern/guitar/");
	public static final String GUITAR_PATTERN_FILE_REGEX = "([^\\.]+)\\.gpt";

	JFrame m_frame;
	JButton m_clearBtn, m_playBtn, m_saveBtn;
	JComboBox<String> m_patternSelect;

	GuitarNeck m_neck;

	MSequencer m_sequencer;

	public GuitarChordEditor(MSequencer sequencer) {
		m_sequencer = sequencer;
		m_neck = new GuitarNeck();
		m_neck.enableEdition(GuitarNeck.EditionMode.CHORD);
	}

	ActionListener m_actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == m_clearBtn) {
				m_neck.eraseAll();
			} else if(e.getSource() == m_playBtn) {
				play();
			} else if(e.getSource() == m_saveBtn) {
				saveChord();
			}
		}
	};

	private void saveChord() {
		Integer rootNote = m_neck.getRootNote();

		if(rootNote == Note.NULL) {
			JOptionPane.showMessageDialog(m_frame, "Chord pattern could not be saved, you must first define the root note.");
			return;
		}

		String name = JOptionPane.showInputDialog(m_frame, "Please give a name for this pattern.");
		if(name == null) {
			return;
		}

		// Building the pattern from neck
		GuitarPattern pattern = new GuitarPattern();

		// First we computes the fret numbers and intervals of each note
		int minFret = m_neck.getFrets();
		int dots = 0;
		for(int string = 1; string <= 6; string++) {
			Integer note = m_neck.getLowestNoteOfString(string);
			if(note == Note.NULL) {
				pattern.clear(string);
			} else {
				dots++;
				int interval = Note.getInterval(rootNote, note);
				int fret = m_neck.computeFret(string, note);
				minFret = Math.min(minFret, fret);
				int finger = -1;

				pattern.add(string, interval, fret, finger);
			}
		}

		pattern.setLeftFret(0);

		// TODO: compute fingers

		File output = new File(PATTERN_DIR, name.replaceAll(" ", "") + ".gpt");
		try {
			pattern.save(output);
		} catch(IOException e) {
			e.printStackTrace();
		}

		// Save diagram
		ChordDiagram diagram = new ChordDiagram("K?", m_neck.getRootNote(), pattern);
		diagram.exportJPG(new File(name.replaceAll(" ", "") + ".jpg"));
	}

	private void play() {
		try {
			m_sequencer.stop();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		Block block = buildChordBlock();
		System.out.println("Block size: " + block.size());
		m_sequencer.set(block);
		m_sequencer.enableLooping(false);
		try {
			m_sequencer.start();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Block buildChordBlock() {
		Block result = new Block(new Time.TimeSignature(2, 1), 1, 1);
		for(int string = 1; string <= 6; string++) {
			Integer note = m_neck.getLowestNoteOfString(string);
			if(note != null) {
				result.add(note, Note.Dynamic.MEZZO_FORTE.velocity, 0, 1);
			}
		}
		return result;
	}

	public void show() {
		// create main frame
		m_frame = new JFrame("Guitar Chord Editor");
		Container content = m_frame.getContentPane();
		// set layout on content pane
		content.setLayout(new BorderLayout());

		// add to content pane
		content.add(m_neck, BorderLayout.CENTER);

		// create controls
		JPanel controls = new JPanel();

		m_clearBtn = new MButton("Clear");
		m_clearBtn.addActionListener(m_actionListener);
		m_playBtn = new MButton("Play");
		m_playBtn.addActionListener(m_actionListener);
		m_patternSelect = new JComboBox<>();
		DPI.adaptFontSize(m_patternSelect);
		m_patternSelect.addActionListener(buildPatternSelectListener());
		m_saveBtn = new MButton("Save");
		m_saveBtn.addActionListener(m_actionListener);
		loadChords();

		// add to panel
		controls.add(m_clearBtn);
		controls.add(m_playBtn);
		controls.add(m_patternSelect);
		controls.add(m_saveBtn);

		// add to content pane
		content.add(controls, BorderLayout.NORTH);

		m_frame.pack();
		// can close frame
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// show the swing paint result
		m_frame.setVisible(true);
	}

	private ActionListener buildPatternSelectListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String patternName = (String) m_patternSelect.getSelectedItem();
				File patternFile = new File(PATTERN_DIR, patternName + ".gpt");
				try {
					GuitarPattern pattern = new GuitarPattern(patternFile);
					// Computing where to draw pattern
					Integer fret = m_neck.computeFret(pattern);
					if(fret != null) {
						m_neck.set(pattern, fret);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	void loadChords() {
		m_patternSelect.removeAllItems();
		for(File file : PATTERN_DIR.listFiles()) {
			String fileName = file.getName();
			if(!fileName.matches(GUITAR_PATTERN_FILE_REGEX)) {
				continue;
			}
			String name = StringUtils.getGroup(fileName, GUITAR_PATTERN_FILE_REGEX, 1);
			m_patternSelect.addItem(name);
		}
	}

	public static void main(String[] args) throws MidiUnavailableException {
		DPI.loadCommandLine(args);
		MidiDevice device = MidiInterface.getMidiOutDevice();

		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		device.open();
		Receiver receiver = device.getReceiver();

		MSequencer sequencer = new MSequencer(receiver, 100);

		new GuitarChordEditor(sequencer).show();
	}
}