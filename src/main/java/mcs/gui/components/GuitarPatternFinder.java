/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.components;

import javax.swing.*;
import java.awt.*;

public class GuitarPatternFinder extends JComponent {

	JTextField m_inputText;

	public GuitarPatternFinder() {
		setLayout(new BorderLayout());

		m_inputText = new JTextField("");
		m_inputText.setMinimumSize(new Dimension(500, 50));
		add(m_inputText, BorderLayout.NORTH);
	}

}