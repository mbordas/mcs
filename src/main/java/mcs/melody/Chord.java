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

package mcs.melody;

import mcs.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Chord {

	public static final String NAME_REGEX = "([ABCDEFG][b#]?)(.*)";

	// Intervals 'size' in semi-tones

	public static final int NULL_INTERVAL = Integer.MIN_VALUE;
	public static final int ROOT = 0;
	public static final int MINOR_SECOND = 1;
	public static final int MAJOR_SECOND = 2;
	public static final int MINOR_THIRD = 3;
	public static final int MAJOR_THIRD = 4;
	public static final int PERFECT_FOURTH = 5;
	public static final int DIMINISHED_FIFTH = 6;
	public static final int PERFECT_FIFTH = 7;
	public static final int MINOR_SIXTH = 8;
	public static final int MAJOR_SIXTH = 9;
	public static final int MINOR_SEVENTH = 10;
	public static final int MAJOR_SEVENTH = 11;

	//
	// Common chords
	//

	// Major
	public static int[] K(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH);
	}

	// Minor
	public static int[] Km(int key) {
		return withIntervals(key, MINOR_THIRD, PERFECT_FIFTH);
	}

	// Major 7
	public static int[] KM7(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH, MAJOR_SEVENTH);
	}

	// 7
	public static int[] K7(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH, MINOR_SEVENTH);
	}

	// Minor 7
	public static int[] Km7(int key) {
		return withIntervals(key, MINOR_THIRD, PERFECT_FIFTH, MINOR_SEVENTH);
	}

	// Minor 7 b5
	public static int[] Km7b5(int key) {
		return withIntervals(key, MINOR_THIRD, DIMINISHED_FIFTH, MINOR_SEVENTH);
	}

	// Diminished 7
	public static int[] Kdim7(int key) {
		return withIntervals(key, MINOR_THIRD, DIMINISHED_FIFTH, MINOR_SEVENTH - 1);
	}

	// 6
	public static int[] K6(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH, MAJOR_SIXTH);
	}

	// m6
	public static int[] Km6(int key) {
		return withIntervals(key, NULL_INTERVAL, MINOR_THIRD, NULL_INTERVAL, PERFECT_FIFTH, MAJOR_SIXTH, NULL_INTERVAL);
	}

	// 7sus4
	public static int[] K7sus4(int key) {
		return withIntervals(key, PERFECT_FOURTH, PERFECT_FIFTH, MINOR_SEVENTH);
	}

	// 9
	public static int[] K9(int key) {
		return withIntervals(key, MAJOR_SECOND, MAJOR_THIRD, NULL_INTERVAL, PERFECT_FIFTH, NULL_INTERVAL, MINOR_SEVENTH);
	}

	// m9
	public static int[] Km9(int key) {
		return withIntervals(key, MAJOR_SECOND, MINOR_THIRD, NULL_INTERVAL, PERFECT_FIFTH, NULL_INTERVAL, MINOR_SEVENTH);
	}

	// 7#11
	public static int[] K7s11(int key) {
		return withIntervals(key, MAJOR_SECOND, MAJOR_THIRD, NULL_INTERVAL, DIMINISHED_FIFTH, NULL_INTERVAL, MINOR_SEVENTH);
	}

	// 7#5
	public static int[] K7s5(int key) {
		return withIntervals(key, NULL_INTERVAL, MAJOR_THIRD, NULL_INTERVAL, PERFECT_FIFTH + 1, NULL_INTERVAL, MINOR_SEVENTH);
	}

	// 6 9
	public static int[] K69(int key) {
		return withIntervals(key, NULL_INTERVAL, MINOR_THIRD - 1, NULL_INTERVAL, PERFECT_FIFTH, MAJOR_SIXTH, NULL_INTERVAL);
	}

	// 13
	public static int[] K13(int key) {
		return withIntervals(key, NULL_INTERVAL, MAJOR_THIRD, NULL_INTERVAL, NULL_INTERVAL, MAJOR_SIXTH, MINOR_SEVENTH);
	}

	// 7b9
	public static int[] K7b9(int key) {
		return withIntervals(key, MINOR_SECOND, MAJOR_THIRD, NULL_INTERVAL, PERFECT_FIFTH, NULL_INTERVAL, MINOR_SEVENTH);
	}

	// 7#9
	public static int[] K7s9(int key) {
		return withIntervals(key, MAJOR_SECOND + 1, MAJOR_THIRD, NULL_INTERVAL, PERFECT_FIFTH, NULL_INTERVAL, MINOR_SEVENTH);
	}

	public static int[] byName(String name, int octave) {
		String keyName = StringUtils.getGroup(name, NAME_REGEX, 1);
		String flavorName = StringUtils.getGroup(name, NAME_REGEX, 2);

		Map<String, Integer> noteOffsets = new HashMap<>();
		noteOffsets.put("C", 0);
		noteOffsets.put("C#", 1);
		noteOffsets.put("Db", 1);
		noteOffsets.put("D", 2);
		noteOffsets.put("D#", 3);
		noteOffsets.put("Eb", 3);
		noteOffsets.put("E", 4);
		noteOffsets.put("F", 5);
		noteOffsets.put("F#", 6);
		noteOffsets.put("Gb", 6);
		noteOffsets.put("G", 7);
		noteOffsets.put("G#", 8);
		noteOffsets.put("Ab", 8);
		noteOffsets.put("A", 9);
		noteOffsets.put("A#", 10);
		noteOffsets.put("Bb", 10);
		noteOffsets.put("B", 11);

		int key = Note.C0 + noteOffsets.get(keyName) + octave * 12;
		if(StringUtils.equalsOne(flavorName, "")) {
			return K(key);
		} else if(StringUtils.equalsOne(flavorName, "m")) {
			return Km(key);
		} else if(StringUtils.equalsOne(flavorName, "M7", "Maj7")) {
			return KM7(key);
		} else if(StringUtils.equalsOne(flavorName, "7")) {
			return K7(key);
		} else if(StringUtils.equalsOne(flavorName, "m7")) {
			return Km7(key);
		} else if(StringUtils.equalsOne(flavorName, "m7b5")) {
			return Km7b5(key);
		} else if(StringUtils.equalsOne(flavorName, "°7", "dim7")) {
			return Kdim7(key);
		} else if(StringUtils.equalsOne(flavorName, "6")) {
			return K6(key);
		} else if(StringUtils.equalsOne(flavorName, "m6")) {
			return Km6(key);
		} else if(StringUtils.equalsOne(flavorName, "7sus4")) {
			return K7sus4(key);
		} else if(StringUtils.equalsOne(flavorName, "9")) {
			return K9(key);
		} else if(StringUtils.equalsOne(flavorName, "m9")) {
			return Km9(key);
		} else if(StringUtils.equalsOne(flavorName, "7#11")) {
			return K7s11(key);
		} else if(StringUtils.equalsOne(flavorName, "7#5")) {
			return K7s5(key);
		} else if(StringUtils.equalsOne(flavorName, "6 9", "69")) {
			return K69(key);
		} else if(StringUtils.equalsOne(flavorName, "13")) {
			return K13(key);
		} else if(StringUtils.equalsOne(flavorName, "7b9")) {
			return K7b9(key);
		} else if(StringUtils.equalsOne(flavorName, "7#9")) {
			return K7s9(key);
		} else {
			return null;
		}
	}

	//
	// Utils
	//

	/**
	 * Creates 3-notes chord made of 2 intervals (third and fifth).
	 *
	 * @param key
	 * @param third
	 * @param fifth
	 * @return
	 */
	static int[] withIntervals(int key, int third, int fifth) {
		return new int[] { key, Note.NULL, key + third, Note.NULL, key + fifth, Note.NULL, Note.NULL };
	}

	/**
	 * Creates a 4-notes chord made of 3 intervals (third, fifth and seventh).
	 *
	 * @param key
	 * @param third
	 * @param fifth
	 * @param seventh
	 * @return
	 */
	static int[] withIntervals(int key, int third, int fifth, int seventh) {
		return new int[] { key, Note.NULL, key + third, Note.NULL, key + fifth, Note.NULL, key + seventh };
	}

	/**
	 * Creates a seven-notes chord made of 6 intervals (second, third, fourth, fifth, sixth and seventh).
	 *
	 * @param key
	 * @param second
	 * @param third
	 * @param fourth
	 * @param fifth
	 * @param sixth
	 * @param seventh
	 * @return
	 */
	static int[] withIntervals(int key, int second, int third, int fourth, int fifth, int sixth, int seventh) {
		return new int[] { key,
				toNote(key, second),
				toNote(key, third),
				toNote(key, fourth),
				toNote(key, fifth),
				toNote(key, sixth),
				toNote(key, seventh) };
	}

	static int toNote(int key, int interval) {
		return interval == NULL_INTERVAL ? Note.NULL : key + interval;
	}

}