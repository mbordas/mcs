/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui;

import mcs.graphics.DPI;
import mcs.gui.components.ChordGrid;
import mcs.gui.components.GuitarNeck;

import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.*;

public class GuitarGridEditor {

	JFrame m_frame;

	ChordGrid m_chordGrid;
	GuitarNeck m_neck;

	public void show() {
		// create main frame
		m_frame = new JFrame("Guitar Chord Editor");
		Container content = m_frame.getContentPane();
		// set layout on content pane
		content.setLayout(new BorderLayout());

		m_chordGrid = new ChordGrid(2, 4);
		content.add(m_chordGrid, BorderLayout.NORTH);

		m_neck = new GuitarNeck(18);
		content.add(m_neck, BorderLayout.CENTER);

		m_frame.pack();
		// can close frame
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// show the swing paint result
		m_frame.setVisible(true);
	}

	static GridBagConstraints gridBagHorizontal() {
		GridBagConstraints result = new GridBagConstraints();
		result.fill = GridBagConstraints.HORIZONTAL;
		return result;
	}

	public static void main(String[] args) throws MidiUnavailableException {
		DPI.loadCommandLine(args);

		new GuitarGridEditor().show();
	}
}