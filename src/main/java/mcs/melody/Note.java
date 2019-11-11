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

public class Note {

	public static final int DEFAULT_VELOCITY = Dynamic.FORTE.velocity;

	// Notations and MIDI velocity are taken from TuxGuitar 1.5 (Copyright (C) 2005 Julian Gabriel Casadesus)
	// ppp: Piano Pianissimo
	// pp: Pianissimo
	// p: Piano
	// mp: Mezzo Piano
	// mf: Mezzo Forte
	// f: Forte
	// ff: Fortissimo
	// fff: Forte Fortissimo

	public enum Dynamic {
		PIANO_PIANISSIMO("ppp", 15), PIANISSIMO("pp", 31), PIANO("p", 47), MEZZO_PIANO("mp", 63), MEZZO_FORTE("mf", 79), FORTE("f",
				95), FORTISSIMO("ff", 111), FORTE_FORTISSIMO("fff", 127);

		public String label;
		public int velocity;

		Dynamic(String label, int velocity) {
			this.label = label;
			this.velocity = velocity;
		}

		public static Dynamic fromLabel(String label) {
			for(Dynamic dynamic : Dynamic.values()) {
				if(label.equalsIgnoreCase(dynamic.label)) {
					return dynamic;
				}
			}
			return null;
		}
	}

	// Usefull documentation:
	// Quarter note: https://en.wikipedia.org/wiki/Quarter_note

	public static final int NULL = 0;

	public static final int C0 = 12;
	public static final int D0 = 14;
	public static final int E0 = 16;
	public static final int F0 = 17;
	public static final int G0 = 19;
	public static final int A0 = 21;
	public static final int B0 = 23;

	public static final int C1 = 24;
	public static final int D1 = 26;
	public static final int E1 = 28;
	public static final int F1 = 29;
	public static final int G1 = 31;
	public static final int A1 = 33;
	public static final int B1 = 35;

	public static final int C2 = 36;
	public static final int D2 = 38;
	public static final int E2 = 40;
	public static final int F2 = 41;
	public static final int G2 = 43;
	public static final int A2 = 45;
	public static final int B2 = 47;

	public static final int C3 = 48;
	public static final int D3 = 50;
	public static final int E3 = 52;
	public static final int F3 = 53;
	public static final int G3 = 55;
	public static final int A3 = 57;
	public static final int B3 = 59;
	public static final int C4 = 60;
	public static final int D4 = 62;
	public static final int E4 = 64;
	public static final int F4 = 65;
	public static final int G4 = 67;
	public static final int A4 = 69;
	public static final int B4 = 71;
	public static final int C5 = 72;
	public static final int D5 = 74;
	public static final int E5 = 76;
	public static final int F5 = 77;
	public static final int G5 = 79;

	public static final int C6 = 84;
	public static final int D6 = 86;
	public static final int E6 = 88;
	public static final int F6 = 89;
	public static final int G6 = 91;
	public static final int A6 = 93;
	public static final int B6 = 95;
	public static final int C7 = 96;
	public static final int D7 = 98;
	public static final int E7 = 100;
	public static final int F7 = 101;
	public static final int G7 = 103;

	public static final int C8 = 108;

	public static int getInterval(int startKey, int endKey) {
		int result = (endKey - startKey) % 12;
		// Managing negative results
		if(result < 0) {
			result += 12;
		}
		return result;
	}

	public static int getNoteInRange(int note, int minNote, int maxNote) {
		if(note >= minNote && note < maxNote) { // Already in range
			return note;
		} else if(note < minNote) {
			while(note < minNote) {
				note += 12;
			}
			return note;
		} else {
			while(note > maxNote) {
				note -= 12;
			}
			return note;
		}
	}

	public static String getName(int key) {
		int interval = getInterval(C0, key);
		String[] names = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
		return names[interval];
	}
}