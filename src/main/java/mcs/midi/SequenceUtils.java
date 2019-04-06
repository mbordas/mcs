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

import mcs.melody.Time;
import mcs.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

public class SequenceUtils {

	public static Sequence createSequence() throws InvalidMidiDataException {
		return new Sequence(Sequence.PPQ, Pattern.DEFAULT_PATTERN_TICKS_PER_BEAT);
	}

	public static void setTempo(Sequence sequence, int bpm) throws InvalidMidiDataException {
		Track track = sequence.getTracks()[0];
		track.add(new MidiEvent(Time.createTempoMessage(bpm), 0));
	}

	/**
	 * Creates a new {@link Track} in the given {@link Sequence} and adds all MIDI messages from all given {@link Track}s.
	 *
	 * @param tracks   Original tracks to be merged.
	 * @param sequence The sequence where the merged track is created.
	 * @return
	 */
	public static Track mergeTracks(Track[] tracks, Sequence sequence) {
		Track result = sequence.createTrack();

		// Merging all tracks messages into 'newTrack'
		for(int trackNumber = 0; trackNumber < tracks.length; trackNumber++) {
			Track track = tracks[trackNumber];
			for(int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				result.add(event);
			}
		}

		return result;
	}
}