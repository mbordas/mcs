/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.pattern;

public class GuitarPattern {

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

	public void set(int string, int interval, int abscissa, int finger) {
		StringFingering fingering = new StringFingering(interval, abscissa, finger);
		m_fingerings[string] = fingering;
	}

	public void clear(int string) {
		m_fingerings[string] = NOT_PLAYED;
	}

	public StringFingering getFingerings(int string) {
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
}