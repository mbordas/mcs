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
import mcs.pattern.GuitarPatternStore;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuitarChordEditor {

	JFrame m_frame;
	JButton m_clearBtn, m_playBtn, m_saveBtn, m_toggleScaleBtn, m_exportJPGBtn;

	JComboBox<String> m_patternSelect;
	AtomicBoolean m_patternSelectEnabled = new AtomicBoolean(true);

	GuitarNeck m_neck;
	GuitarPatternStore m_patternStore;
	MSequencer m_sequencer;

	public GuitarChordEditor(MSequencer sequencer) {
		m_sequencer = sequencer;
		m_neck = new GuitarNeck();
		m_patternStore = new GuitarPatternStore();
		m_neck.enableEdition(GuitarNeck.EditionMode.SCALE);
	}

	ActionListener m_actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == m_clearBtn) {
				m_neck.eraseAll();
			} else if(e.getSource() == m_playBtn) {
				play();
			} else if(e.getSource() == m_saveBtn) {
				saveChord();
			} else if(e.getSource() == m_toggleScaleBtn) {
				toggleScale();
			} else if(e.getSource() == m_exportJPGBtn) {
				exportJPG();
			}
		}
	};

	private void toggleScale() {
		m_neck.showAsScale(!m_neck.isShowedAsScale());
	}

	private void saveChord() {
		String name = JOptionPane.showInputDialog(m_frame, "Please give a name for this pattern.");
		if(name == null) {
			return;
		}

		try {
			GuitarPattern pattern = m_neck.computeGuitarPattern();
			if(pattern == null) {
				return;
			}

			// TODO: compute fingers

			m_patternStore.save(name, pattern);
			updateChords(name);
		} catch(NullPointerException e) {
			JOptionPane.showMessageDialog(m_frame, e.getMessage());
		}
	}

	private void exportJPG() {
		String name = JOptionPane.showInputDialog(m_frame, "Please give a name for this diagram.");
		if(name == null) {
			return;
		}
		GuitarPattern pattern = m_neck.computeGuitarPattern();
		ChordDiagram diagram = new ChordDiagram(name, m_neck.getRootNote(), pattern);
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
		m_toggleScaleBtn = new MButton("Scale On/Off");
		m_toggleScaleBtn.addActionListener(m_actionListener);
		m_exportJPGBtn = new MButton("Export JPG");
		m_exportJPGBtn.addActionListener(m_actionListener);

		updateChords(null);

		// add to panel
		controls.add(m_clearBtn);
		controls.add(m_playBtn);
		controls.add(m_patternSelect);
		controls.add(m_saveBtn);
		controls.add(m_toggleScaleBtn);
		controls.add(m_exportJPGBtn);

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
				if(m_patternSelectEnabled.get()) {
					String patternName = (String) m_patternSelect.getSelectedItem();
					if(patternName != null) {
						GuitarPattern pattern = m_patternStore.get(patternName);
						// Computing where to draw pattern
						Integer fret = m_neck.computeFret(pattern);
						if(fret != null) {
							m_neck.set(pattern, fret);
						}
					}
				}
			}
		};
	}

	void updateChords(String patternToSelect) {
		m_patternSelectEnabled.set(false);

		m_patternSelect.removeAllItems();
		for(String patternName : m_patternStore.getAll().keySet()) {
			m_patternSelect.addItem(patternName);
		}
		if(patternToSelect != null) {
			m_patternSelect.setSelectedItem(patternToSelect);
		}

		m_patternSelectEnabled.set(true);
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