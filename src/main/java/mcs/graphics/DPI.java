/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.graphics;

import javax.swing.*;
import java.awt.*;

public class DPI {

	public static final String CMD_ARG = "-DPI";

	public static double DPI_FACTOR = 1.0;

	public static int toScale(int pixels) {
		return (int) (pixels * DPI_FACTOR);
	}

	public static int unScale(int pixels) {
		return (int) (pixels / DPI_FACTOR);
	}

	public static int toScale(double pixels) {
		return (int) (pixels * DPI_FACTOR);
	}

	/**
	 * Changes component's font size to fit the DPI factor.
	 * @param component
	 */
	public static void adaptFontSize(JComponent component) {
		Font currentFont = component.getFont();
		Font newFont = currentFont.deriveFont((float)DPI.toScale(currentFont.getSize()));
		component.setFont(newFont);
	}

	public static void loadCommandLine(String[] args) {
		for(int a = 0; a < args.length; a++) {
			if(CMD_ARG.equalsIgnoreCase(args[a])) {
				DPI_FACTOR = Double.valueOf(args[a + 1]);
				System.out.println("DPI factor " + DPI_FACTOR);
			}
		}
	}
}