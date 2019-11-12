/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui.pianotrainer;

import mcs.graphics.MGraphics;
import mcs.gui.components.ScoreFragment;

import java.awt.*;

public class TrainerScore extends ScoreFragment {

	int m_points = 0;

	public TrainerScore(int noteMin, int noteMax) {
		super(noteMin, noteMax);
	}

	void setPoints(int points) {
		m_points = points;
	}

	@Override
	protected void paintComponent(MGraphics graphics) {
		super.paintComponent(graphics);

		String text = String.format("%d", m_points);

		graphics.setFontSize(96f);
		graphics.setPaint(m_points >= 0 ? Color.GREEN : Color.RED);
		graphics.drawStringRight(text, 0, 0, getWidth(), getHeight(), 2 * PADDING_px);
	}

}