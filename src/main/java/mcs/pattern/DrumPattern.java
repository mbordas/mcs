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

package mcs.pattern;

import mcs.melody.Note;
import mcs.midi.Drum;
import mcs.utils.FileUtils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;

public class DrumPattern {

	public static long importPattern(File patternFile, Track track, long tickStart) throws IOException, InvalidMidiDataException {
		long result = tickStart;
		int channel = Drum.CHANNEL;

		int tickPerBeatsFactor = 1;

		// Reading options
		for(String line : FileUtils.readLines(patternFile)) {
			// Reading options
			if(line.contains("=")) {
				String[] words = line.split("=");
				String name = words[0];
				String valueStr = words[1];

				if(Pattern.OPTION_TICKS_PER_BEAT.equalsIgnoreCase(name)) {
					int ticksPerBeat = Integer.valueOf(valueStr);
					tickPerBeatsFactor = Pattern.DEFAULT_PATTERN_TICKS_PER_BEAT / ticksPerBeat;
				}
			}
		}

		for(String line : FileUtils.readLines(patternFile)) {

			if(line.length() > 0 && !line.startsWith("#") && !line.contains("=")) {

				// Pattern

				String[] words = line.split(";");
				String keys = words[0];
				int tick = Integer.valueOf(words[1]);
				int duration = Integer.valueOf(words[2]);
				int velocity = Pattern.parseVelocity(words[3]);

				if(keys.contains(",")) {
					for(String keyStr : keys.split(",")) {
						int key = Integer.valueOf(keyStr);

						if(key != Note.NULL) {
							track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, channel, key, velocity),
									tickStart + tickPerBeatsFactor * tick));
							track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, channel, key, velocity),
									tickStart + tickPerBeatsFactor * (tick + duration)));
						}

						result = Math.max(result, tickStart + tickPerBeatsFactor * (tick + duration));
					}
				} else {
					int key = Integer.valueOf(words[0]);
					if(key != Note.NULL) {
						track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, channel, key, velocity),
								tickStart + tickPerBeatsFactor * tick));
						track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, channel, key, velocity),
								tickStart + tickPerBeatsFactor * (tick + duration)));
					}

					result = Math.max(result, tickStart + tickPerBeatsFactor * (tick + duration));
				}
			}
		}

		return result;
	}

}