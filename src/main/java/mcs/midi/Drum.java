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

package mcs.midi;

import java.util.LinkedHashMap;
import java.util.Map;

public class Drum {

	public static final int CHANNEL = 9; // General Midi specs channels are 1 to 16 (in code 0-15), drums are on 10th (in code 9).

	// General Midi Standard Drum Map
	// Source: https://www.midi.org/specifications/item/gm-level-1-sound-set

	// Bass drum
	public static final int ACOUSTIC_BASS_DRUM = 35;
	public static final int BASS_DRUM_1 = 36;

	// Toms
	public static final int LOW_FLOOR_TOM = 41;
	public static final int HIGH_FLOOR_TOM = 43;
	public static final int LOW_TOM = 45;
	public static final int LOW_MID_TOM = 47;
	public static final int HIGH_TOM = 50;
	public static final int HIGH_MID_TOM = 48;

	// Hit-Hat
	public static final int OPEN_HIT_HAT = 46;
	public static final int CLOSED_HIT_HAT = 42;
	public static final int PEDAL_HIT_HAT = 44;

	// Snare
	public static final int ACOUSTIC_SNARE = 38;
	public static final int ELECTRIC_SNARE = 40;

	// Cymbals
	public static final int CRASH_CYMBAL_1 = 49;
	public static final int CRASH_CYMBAL_2 = 57;
	public static final int RIDE_CYMBAL_1 = 51;
	public static final int RIDE_CYMBAL_2 = 59;
	public static final int CHINESE_CYMBAL = 52;
	public static final int SPLASH_CYMBAL = 55;

	// Bells
	public static final int RIDE_BELL = 53;
	public static final int COWBELL = 56;

	// Bongo
	public static final int LOW_BONGO = 61;
	public static final int HIGH_BONGO = 60;

	// Congas
	public static final int MUTE_HIGH_CONGA = 62;
	public static final int OPEN_HIGH_CONGA = 53;
	public static final int LOW_CONGA = 64;

	// Timbale
	public static final int HIGH_TIMBALE = 65;
	public static final int LOW_TIMBALE = 66;

	// Others
	public static final int SIDE_STICK = 37;
	public static final int HAND_CLAP = 39;
	public static final int TAMBOURINE = 54;
	public static final int VIBRASLAP = 58;
	public static final int HIGH_AGOGO = 67;
	public static final int LOW_AGOGO = 68;
	public static final int CABASA = 69;
	public static final int MARACAS = 70;
	public static final int SHORT_WHISTLE = 71;
	public static final int LONG_WHISTLE = 72;
	public static final int SHORT_GUIRO = 73;
	public static final int LONG_GUIRO = 74;
	public static final int CLAVES = 75;
	public static final int HIGH_WOOD_BLOCK = 76;
	public static final int LOW_WOOD_BLOCK = 77;
	public static final int MUTE_CUICA = 78;
	public static final int OPEN_CUICA = 79;
	public static final int MUTE_TRIANGLE = 80;
	public static final int OPEN_TRIANGLE = 81;

	public static Map<String, Integer> getBasicKeyMapping() {
		Map<String, Integer> result = new LinkedHashMap<>();

		result.put("Bass", BASS_DRUM_1);
		result.put("Open HH", OPEN_HIT_HAT);
		result.put("Pedal HH", PEDAL_HIT_HAT);
		result.put("Snare", ACOUSTIC_SNARE);

		return result;
	}

}