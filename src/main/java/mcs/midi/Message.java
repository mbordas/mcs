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

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class Message {

	public static final int META_TYPE_TEMPO = 81;
	public static final int META_TYPE_TIME_SIGNATURE = 88;

	public static String toString(MidiMessage message) {
		String name = "UNKNOWN";
		String dataStr = "...";

		if(message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;
			int command = shortMessage.getCommand();
			switch(command) {
			case 254:
				name = "ACTIVE_SENSING";
				break;
			case 208:
				name = "CHANNEL_PRESSURE";
				break;
			case 251:
				name = "CONTINUE";
				break;
			case 176:
				name = "CONTROL_CHANGE";
				break;
			case 247:
				name = "END_OF_EXCLUSIVE";
				break;
			case 241:
				name = "MIDI_TIME_CODE";
				break;
			case 128:
				name = "NOTE_OFF";
				dataStr = String.format("chan #%d, key %d, velocity %d", shortMessage.getChannel(), shortMessage.getData1(),
						shortMessage.getData2());
				break;
			case 144:
				name = "NOTE_ON";
				dataStr = String.format("chan #%d, key %d, velocity %d", shortMessage.getChannel(), shortMessage.getData1(),
						shortMessage.getData2());
				break;
			case 224:
				name = "PITCH_BEND";
				break;
			case 160:
				name = "POLY_PRESSURE";
				break;
			case 192:
				name = "PROGRAM_CHANGE";
				break;
			case 242:
				name = "SONG_POSITION_POINTER";
				break;
			case 243:
				name = "SONG_SELECT";
				break;
			case 250:
				name = "START";
				break;
			case 252:
				name = "STOP";
				break;
			case 255:
				name = "SYSTEM_RESET";
				break;
			case 248:
				name = "TIMING_CLOCK";
				break;
			case 246:
				name = "TUNE_REQUEST";
				break;
			default:
				name = String.format("UNKNOWN SHORT COMMAND %d", command);
			}
		} else if(message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;
			int type = metaMessage.getType();
			switch(type) {
			case META_TYPE_TEMPO:
				name = "TEMPO";
				long tempo = toLong(metaMessage.getData());
				long bpm = 60_000_000 / tempo;
				dataStr = String.format("%d BPM (%d bytes)", bpm, metaMessage.getData().length);
				break;
			case META_TYPE_TIME_SIGNATURE:
				name = "TIME_SIGNATURE";
				long numerator = toLong(metaMessage.getData(), 0, 0);
				long denominator = toLong(metaMessage.getData(), 1, 1);
				long ticksPerMetronomeClick = toLong(metaMessage.getData(), 2, 2);
				long _32ndNotesPerMIDIQuarterNote = toLong(metaMessage.getData(), 3, 3);
				dataStr = String.format("%d/%d, %d ticks per metronome click, %d 32nd notes per quarter note", numerator, denominator,
						ticksPerMetronomeClick, _32ndNotesPerMIDIQuarterNote);
				break;
			default:
				name = String.format("UNKNOWN META TYPE %d", type);
			}
		}

		return String.format("%s(%d) %s : %s", message.getClass().getSimpleName(), message.getStatus(), name, dataStr);
	}

	static long toLong(byte[] bytes) {
		long result = 0L;
		long power = 1;
		for(int p = bytes.length - 1; p >= 0; p--) {
			result += (bytes[p] & 255) * power;
			power *= 256;
		}
		return result;
	}

	static long toLong(byte[] bytes, int firstIndex, int lastIndex) {
		byte[] cropedBytes = new byte[lastIndex + 1 - firstIndex];
		for(int i = firstIndex; i <= lastIndex; i++) {
			cropedBytes[i - firstIndex] = bytes[i];
		}
		return toLong(cropedBytes);
	}

}