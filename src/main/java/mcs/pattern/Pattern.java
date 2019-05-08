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
import mcs.melody.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Pattern {

	public static final int DEFAULT_PATTERN_TICKS_PER_BEAT = 96;

	public static final String OPTION_TICKS_PER_BEAT = "ticks_per_beat";

	protected final Time.TimeSignature m_timeSignature;
	protected final int m_ticksPerBeat;
	protected int m_bars;
	protected final Map<Long, List<Event>> m_tickEvents = new TreeMap<>();

	protected Pattern(Time.TimeSignature timeSignature, int ticksPerBeat) {
		m_timeSignature = timeSignature;
		m_ticksPerBeat = ticksPerBeat;
		m_bars = 1;
	}

	public Time.TimeSignature getTimeSignature() {
		return m_timeSignature;
	}

	public int getBars() {
		return m_bars;
	}

	/**
	 * @param level     Starts with 1.
	 * @param velocity
	 * @param tickStart
	 * @param tickStop
	 */
	public void add(int level, int velocity, long tickStart, long tickStop) {
		List<Event> tickEvents = getOrCreateEventList(tickStart);
		tickEvents.add(new Event(new int[] { level }, velocity, tickStop - tickStart));

		// Updating bars count if new event makes the Pattern longer
		// Note tha bar index starts with 0, that's why we adds 1 to get the number of bars
		// 'tickStop' is the tick at which the event stops, so we must remove 1 to get the last tick of the event
		int _bars = computeBarIndex(tickStop - 1, m_ticksPerBeat, m_timeSignature.getBeatsInBar()) + 1;
		m_bars = Math.max(m_bars, _bars);
	}

	public int getTicksPerBeat() {
		return m_ticksPerBeat;
	}

	/**
	 * Returns the size of {@link Pattern} in ticks.
	 *
	 * @return
	 */
	public long size() {
		return m_timeSignature.getTicksInBar(m_ticksPerBeat) * m_bars;
	}

	public List<Event> getEvents(long tick) {
		return getOrCreateEventList(tick);
	}

	private List<Event> getOrCreateEventList(long tick) {
		List<Event> result = m_tickEvents.get(tick);
		if(result == null) {
			result = new ArrayList<>();
			m_tickEvents.put(tick, result);
		}
		return result;
	}

	public static int computeBarIndex(long tick, long m_ticksPerBeat, int beatsInBar) {
		int beats = (int) (tick / m_ticksPerBeat);
		return beats / beatsInBar;
	}

	/**
	 * Support direct integer value and {@link Note.Dynamic} label as well.
	 *
	 * @param word
	 * @return
	 */
	public static int parseVelocity(String word) {
		int velocity;
		if(word.matches("[0-9]+")) {
			velocity = Integer.valueOf(word);
		} else {
			velocity = Note.Dynamic.fromLabel(word).velocity;
		}
		return velocity;
	}

}