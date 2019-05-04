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

	public static int[] K(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH);
	}

	public static int[] KM7(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH, MAJOR_SEVENTH);
	}

	public static int[] K7(int key) {
		return withIntervals(key, MAJOR_THIRD, PERFECT_FIFTH, MINOR_SEVENTH);
	}

	public static int[] Km(int key) {
		return withIntervals(key, MINOR_THIRD, PERFECT_FIFTH);
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
		if("".equals(flavorName)) {
			return K(key);
		} else if("7".equals(flavorName)) {
			return K7(key);
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
		return new int[] { key, key + second, key + third, key + fourth, key + fifth, key + sixth, key + seventh };
	}
}