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

import mcs.pattern.Event;
import mcs.pattern.Pattern;
import mcs.utils.StringUtils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link Block} is a piece of partition. It contains {@link ShortMessage}s. It can be constructed from {@link Pattern}.
 */
public class Block {

	public static final int DEFAULT_NOTE_OFF_VELOCITY = 0;

	private final Time.TimeSignature m_timeSignature;
	private final int m_ticksPerBeat;
	private int m_channel;
	protected final Map<Long, List<Event>> m_tickEvents = new TreeMap<>(); // Here events are stored with levels=keys

	public Block(Time.TimeSignature timeSignature, int ticksPerBeat, int channel) {
		m_timeSignature = timeSignature;
		m_ticksPerBeat = ticksPerBeat;
		m_channel = channel;
	}

	public long size() {
		return m_timeSignature.getTicks(m_ticksPerBeat);
	}

	public long getDuration_ms(int tempo_bpm) {
		return (size() + 1) * Time.computeTickDuration_ms(tempo_bpm, m_ticksPerBeat);
	}

	public int getTicksPerBeat() {
		return m_ticksPerBeat;
	}

	public void add(int note, int velocity, long tickStart, long tickStop) {
		List<Event> tickEvents = getOrCreateEventList(tickStart);
		tickEvents.add(new Event(new int[] { note }, velocity, tickStop - tickStart));
	}

	public String getContent() {
		StringBuilder result = new StringBuilder();
		result.append(Pattern.OPTION_TICKS_PER_BEAT + "=" + m_ticksPerBeat + "\n");

		for(Map.Entry<Long, List<Event>> entry : m_tickEvents.entrySet()) {
			long tick = entry.getKey();
			for(Event event : entry.getValue()) {
				result.append("" + event.getOctavePitch() + ";");
				result.append(StringUtils.toString(event.getLevels(), ",") + ";");
				result.append("" + tick + ";" + event.getDuration_ticks() + ";");
				result.append("" + event.getVelocity() + "\n");
			}
		}

		return result.toString();
	}

	/**
	 * Computes MIDI {@link ShortMessage}s.
	 *
	 * @param tick
	 * @return
	 * @throws InvalidMidiDataException
	 */
	public List<ShortMessage> toMessages(long tick) throws InvalidMidiDataException {
		List<ShortMessage> result = new ArrayList<>();

		// Reading any event that starts before or at 'tick'
		for(Map.Entry<Long, List<Event>> entry : m_tickEvents.entrySet()) {
			long _tick = entry.getKey();
			if(_tick > tick) {
				break;
			}

			if(_tick < tick) { // Looking in past events if some ends now at 'tick'
				long distance = tick - _tick;
				for(Event event : entry.getValue()) {
					if(event.getDuration_ticks() == distance) {
						for(int note : event.getLevels()) {
							if(note != Note.NULL) {
								result.add(new ShortMessage(ShortMessage.NOTE_OFF, m_channel, note, DEFAULT_NOTE_OFF_VELOCITY));
							}
						}
					}
				}
			} else {
				for(Event event : entry.getValue()) {
					for(int note : event.getLevels()) {
						if(note != Note.NULL) {
							result.add(new ShortMessage(ShortMessage.NOTE_ON, m_channel, note, event.getVelocity()));
						}
					}
				}
			}
		}
		return result;
	}

	private List<Event> getOrCreateEventList(long tick) {
		List<Event> result = m_tickEvents.get(tick);
		if(result == null) {
			result = new ArrayList<>();
			m_tickEvents.put(tick, result);
		}
		return result;
	}

}