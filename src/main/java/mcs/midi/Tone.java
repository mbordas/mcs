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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Tone {

	String m_name;
	int m_msb;
	int m_lsb;
	int m_pc;

	public Tone(String name, int msb, int lsb, int pc) {
		m_name = name;
		m_msb = msb;
		m_lsb = lsb;
		m_pc = pc;
	}

	public static void selectInstrument(Track track, int channel, Tone tone) throws InvalidMidiDataException {
		for(ShortMessage message : buildChangeInstrumentMessages(channel, tone.m_msb, tone.m_lsb, tone.m_pc)) {
			track.add(new MidiEvent(message, -1));
		}
	}

	public static ShortMessage[] buildChangeInstrumentMessages(int channel, int msb, int lsb, int pc) throws InvalidMidiDataException {
		ShortMessage[] messages = new ShortMessage[3];
		messages[0] = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0, msb);
		messages[1] = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 32, lsb);
		messages[2] = new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, pc, 0);
		return messages;
	}
}