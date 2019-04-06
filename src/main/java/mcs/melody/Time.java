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

import mcs.midi.Message;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;
import java.math.BigInteger;
import java.util.Arrays;

public class Time {

	// Global time principles
	//
	// Tick: a tick is equivalent to the time resolution (like is the pixel for an image).
	// Beat: the beat is directly related to the tempo. With a tempo of 60 (BPM), a beat will last 1 second.
	// Time signature N/D: the denominator indicates the duration of a beat in a bar, expressed as divisions of a whole note. The
	// numerator indicates the number of beats in a bar. Example: 4/4 means 4 beats of quarter note, 2/4 indicates 2 beats of quarter
	// note.

	public enum Duration {WHOLE, HALF, QUARTER, EIGHTH, SIXTEENTH, THIRTY_SECOND, TRIPLET, TRIPLET_6}

	/**
	 * @param mspq Micro-Seconds Per Quarter
	 * @return
	 */
	public static MetaMessage createTempoMessage(long mspq) throws InvalidMidiDataException {
		byte[] data = { (byte) ((mspq & 0xff000000) >> 24), (byte) ((mspq & 0x00ff0000) >> 16), (byte) ((mspq & 0x0000ff00) >> 8),
				(byte) (mspq & 0x000000ff) };
		// Clear leading 0's
		int i;
		for(i = 0; i < data.length - 1 && data[i] == 0; i++)
			;
		if(i != 0) {
			data = Arrays.copyOfRange(data, i, data.length);
		}
		return new MetaMessage(Message.META_TYPE_TEMPO, data, data.length);
	}

	/**
	 * Overrides TEMPO meta message(s) with given BPM.
	 *
	 * @param track
	 * @param bpm
	 * @throws InvalidMidiDataException
	 */
	public static void changeTempo(Track track, int bpm) throws InvalidMidiDataException {
		for(int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);
			MidiMessage message = event.getMessage();

			if(message instanceof MetaMessage) {
				MetaMessage metaMessage = (MetaMessage) message;
				if(Message.META_TYPE_TEMPO == metaMessage.getType()) {
					MetaMessage tempoMessage = createTempoMessage(bpm);
					metaMessage.setMessage(Message.META_TYPE_TEMPO, tempoMessage.getData(), tempoMessage.getData().length);
				}
			}
		}
	}

	public static MetaMessage createTempoMessage(int bpm) throws InvalidMidiDataException {
		byte[] bytes = BigInteger.valueOf(60_000_000 / bpm).toByteArray();
		MetaMessage metaMessage = new MetaMessage(Message.META_TYPE_TEMPO, bytes, bytes.length);
		return metaMessage;
	}

}