/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.pattern;

import mcs.utils.FileUtils;
import mcs.utils.StringUtils;

import java.io.File;
import java.io.IOException;

public class GuitarPattern {

	public static final String STANDARD_GUITAR_TUNING = "E2 A2 D3 G3 B3 E4";
	public static final String HEADER = String.format("[tuning=%s]", STANDARD_GUITAR_TUNING);

	public static final int FINGER_INDEX = 1;
	public static final int FINGER_MIDDLE = 2;
	public static final int FINGER_RING = 3;
	public static final int FINGER_LITTLE = 4;
	public static final int FINGER_THUMB = 5;

	public static final StringFingering NOT_PLAYED = new StringFingering(0, 0, 0);

	private final StringFingering[] m_fingerings;

	public GuitarPattern() {
		m_fingerings = new StringFingering[6];
	}

	public GuitarPattern(File input) throws IOException {
		this();

		int l = 0;
		for(String line : FileUtils.readLines(input)) {
			if(l == 0) {
				// Checking header
				if(!HEADER.equals(line)) {
					throw new IOException("Unexpected header in file: " + input.getAbsolutePath());
				}
			} else {
				int string = Integer.valueOf(line.split("=")[0]);
				String[] params = line.split("=")[1].split(",");
				int abscissa = Integer.valueOf(params[0]);
				int finger = Integer.valueOf(params[1]);
				int interval = Integer.valueOf(params[2]);

				set(string, interval, abscissa, finger);
			}
			l++;
		}
	}

	public void set(int string, int interval, int abscissa, int finger) {
		StringFingering fingering = new StringFingering(interval, abscissa, finger);
		m_fingerings[string] = fingering;
	}

	public void clear(int string) {
		m_fingerings[string] = NOT_PLAYED;
	}

	public StringFingering getFingering(int string) {
		return m_fingerings[string];
	}

	// Represents the finger position on one string.
	public static class StringFingering {
		int m_interval; // Harmonic interval from the root note
		int m_abscissa; // Number of cells from the arbitrary center chosen
		int m_finger;

		public StringFingering(int interval, int abscissa, int finger) {
			m_interval = interval;
			m_abscissa = abscissa;
			m_finger = finger;
		}

		public int getInterval() {
			return m_interval;
		}

		public int getAbscissa() {
			return m_abscissa;
		}

		public int getFinger() {
			return m_finger;
		}
	}

	public void save(File output) throws IOException {
		StringBuilder content = new StringBuilder();
		content.append(HEADER + "\n");
		for(int string = 5; string >= 0; string--) {
			StringFingering fingering = getFingering(string);
			if(fingering == NOT_PLAYED) {
				content.append(String.format("%d=X\n", string));
			} else {
				content.append(String.format("%d=%d,%d,%d\n", string,
						fingering.getAbscissa(),
						fingering.getFinger(),
						fingering.getInterval()));
			}
		}
		FileUtils.writeToFile(content.toString(), output, StringUtils.DEFAULT_ENCODING, false);
	}
}