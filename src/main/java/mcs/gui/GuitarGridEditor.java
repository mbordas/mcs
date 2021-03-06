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