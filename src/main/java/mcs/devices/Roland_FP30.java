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

package mcs.devices;

import mcs.midi.Tone;

public class Roland_FP30 {

	// Piano

	public static final Tone GRAND_PIANO_1 = new Tone("Grand Piano 1", 0, 68, 1);
	public static final Tone GRAND_PIANO_2 = new Tone("Grand Piano 2", 16, 67, 1);
	public static final Tone GRAND_PIANO_3 = new Tone("Grand Piano 3", 8, 66, 2);
	public static final Tone RAGTIME_PIANO = new Tone("Ragtime Piano", 0, 64, 4);
	public static final Tone HARPSICHORD_1 = new Tone("Harpsichord 1", 0, 66, 7);
	public static final Tone HARPSICHORD_2 = new Tone("Harpsichord 2", 8, 66, 7);

	// Electric Piano

	public static final Tone ELECTRIC_PIANO_1 = new Tone("E. Piano 1", 16, 67, 5);
	public static final Tone ELECTRIC_PIANO_2 = new Tone("E. Piano 2", 0, 70, 6);
	public static final Tone ELECTRIC_PIANO_3 = new Tone("E. Piano 3", 24, 65, 5);
	public static final Tone CLAV = new Tone("Clav.", 0, 67, 8);
	public static final Tone VIBRAPHONE = new Tone("Vibraphone", 0, 0, 12);
	public static final Tone CELESTA = new Tone("Celesta", 0, 0, 9);
	public static final Tone SYNTH_BELL = new Tone("Synth Bell", 0, 68, 99);

	// Strings
	public static final Tone NYLON_STRING_GUITAR = new Tone("Nylon-str.Gt", 0, 0, 25);

	// Other

	public static final Tone CHOIR_1 = new Tone("Choir 1", 8, 64, 53);
	public static final Tone CHOIR_2 = new Tone("Choir 2", 8, 66, 53);
	public static final Tone CHOIR_3 = new Tone("Choir 3", 8, 68, 53);
	public static final Tone JAZZ_ORGAN_1 = new Tone("Jazz Organ 1", 0, 70, 19);
	public static final Tone JAZZ_SCAT = new Tone("Jazz Organ 1", 0, 65, 55);
	public static final Tone THUM_VOICE = new Tone("Thum Voice", 0, 66, 54);

}