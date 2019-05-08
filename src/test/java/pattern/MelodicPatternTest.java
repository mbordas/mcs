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

package pattern;

import mcs.melody.Block;
import mcs.melody.Chord;
import mcs.melody.Note;
import mcs.melody.Time;
import mcs.midi.Message;
import mcs.pattern.MelodicPattern;
import org.junit.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MelodicPatternTest {

	@Test
	public void load() throws IOException {
		File inputFile = new File("target/test-classes/pattern/pattern_01.mpt");

		MelodicPattern pattern = MelodicPattern.load(inputFile);

		System.out.println(pattern.getContent());

		assertEquals(1, pattern.getBars());
		assertEquals(4, pattern.getTimeSignature().getBeatsInBar());
		assertEquals(4, pattern.getTimeSignature().getBeatDivision());
		assertEquals(16, pattern.getTicksPerBeat());
	}

	@Test
	public void toBlock() throws InvalidMidiDataException {
		MelodicPattern pattern = new MelodicPattern(new Time.TimeSignature(4, 4), 4);

		// Adding a whole note of fundamental
		pattern.add(1, Note.Dynamic.MEZZO_FORTE.velocity, 0, 3);

		int channel = 3;
		Block block = pattern.toBlock(channel, Chord.byName("A7", 3));

		List<ShortMessage> messages = block.toMessages(0);

		assertEquals(1, messages.size());
		ShortMessage message = messages.get(0);
		assertTrue(checkMessage(message, channel, Note.A3, Note.Dynamic.MEZZO_FORTE.velocity, true));

		messages = block.toMessages(3);

		assertEquals(1, messages.size());
		message = messages.get(0);
		assertTrue(checkMessage(message, channel, Note.A3, 0, false));
	}

	static boolean checkMessage(ShortMessage message, int channel, int note, int velocity, boolean isON) throws InvalidMidiDataException {
		ShortMessage expectedMessage = new ShortMessage(isON ? ShortMessage.NOTE_ON : ShortMessage.NOTE_OFF, channel, note, velocity);
		boolean result = expectedMessage.getChannel() == channel;
		result &= expectedMessage.getData1() == message.getData1();
		result &= expectedMessage.getData2() == message.getData2();

		if(!result) {
			System.out.println(Message.toString(expectedMessage) + " != " + Message.toString(message));
		}
		return result;
	}
}