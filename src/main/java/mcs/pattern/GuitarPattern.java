/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.pattern;

import mcs.gui.components.GuitarNeck;
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

	private final int[] m_tunning;
	private final StringFingering[] m_fingerings;

	public GuitarPattern() {
		m_tunning = GuitarNeck.TUNING_STANDARD;
		m_fingerings = new StringFingering[6];
	}

	public GuitarPattern(GuitarPattern other) {
		m_tunning = other.m_tunning;
		m_fingerings = new StringFingering[6];
		for(int string = 1; string <= 6; string++) {
			m_fingerings[string - 1] = new StringFingering(other.getFingering(string));
		}
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

				String paramStr = line.split("=")[1];

				if("X".equalsIgnoreCase(paramStr)) { // Not played
					clear(string);
				} else { // played
					String[] params = line.split("=")[1].split(",");
					int abscissa = Integer.valueOf(params[0]);
					int finger = Integer.valueOf(params[1]);
					int interval = Integer.valueOf(params[2]);

					add(string, interval, abscissa, finger);
				}
			}
			l++;
		}
	}

	public void add(int string, int interval, int abscissa, int finger) {
		StringFingering fingering = new StringFingering(interval, abscissa, finger);
		m_fingerings[string - 1] = fingering;
	}

	public void clear(int string) {
		m_fingerings[string - 1] = NOT_PLAYED;
	}

	public StringFingering getFingering(int string) {
		return m_fingerings[string - 1];
	}

	public int[] getTunning() {
		return m_tunning;
	}

	/**
	 * Returns the number of frets covered by the pattern.
	 *
	 * @return
	 */
	public int getWidth() {
		int leftFret = Integer.MAX_VALUE;
		int rightFret = Integer.MIN_VALUE;
		for(StringFingering fingering : m_fingerings) {
			if(fingering != NOT_PLAYED) {
				leftFret = Math.min(leftFret, fingering.getAbscissa());
				rightFret = Math.max(rightFret, fingering.getAbscissa());
			}
		}

		return leftFret > rightFret ? 0 : rightFret - leftFret + 1;
	}

	/**
	 * Updates abscissas so that the lowest one equals to 'fret'.
	 *
	 * @param fret
	 */
	public void setLeftFret(int fret) {
		int lowestAbscissa = Integer.MAX_VALUE;
		for(int string = 1; string <= 6; string++) {
			if(m_fingerings[string - 1] != NOT_PLAYED) {
				lowestAbscissa = Math.min(lowestAbscissa, m_fingerings[string - 1].getAbscissa());
			}
		}
		int abscissaOffset = fret - lowestAbscissa;
		for(int string = 1; string <= 6; string++) {
			StringFingering fingering = m_fingerings[string - 1];
			if(fingering != NOT_PLAYED) {
				fingering.setAbscissa(fingering.getAbscissa() + abscissaOffset);
			}
		}
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

		public StringFingering(StringFingering other) {
			m_interval = other.m_interval;
			m_abscissa = other.m_abscissa;
			m_finger = other.m_finger;
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

		public void setAbscissa(int abscissa) {
			m_abscissa = abscissa;
		}
	}

	public void save(File output) throws IOException {
		StringBuilder content = new StringBuilder();
		content.append(HEADER + "\n");
		for(int string = 6; string >= 1; string--) {
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