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
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class ReceiverHelper {

	/**
	 * @param receiver
	 * @param channel  0 to 15.
	 * @param msb
	 * @param lsb
	 * @param pc
	 * @throws InvalidMidiDataException
	 */
	public static void selectInstrument(Receiver receiver, int channel, int msb, int lsb, int pc) throws InvalidMidiDataException {
		receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0, msb), -1);
		receiver.send(new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 32, lsb), -1);
		receiver.send(new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, pc, 0), -1);
	}

	public static void selectInstrument(Receiver receiver, int channel, int bank, int instrument) throws InvalidMidiDataException {
		selectInstrument(receiver, channel, bank >> 7, bank & 0x7f, instrument);
	}

	public static void selectInstrument(Receiver receiver, int channel, Tone tone) throws InvalidMidiDataException {
		selectInstrument(receiver, channel, tone.m_msb, tone.m_lsb, tone.m_pc);
	}
}