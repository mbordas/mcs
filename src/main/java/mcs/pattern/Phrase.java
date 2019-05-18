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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Phrase {

	public static final int DEFAULT_OCTAVE = 3;

	public static class Instrument {
		public String label;
		public int channel;

		public Instrument(String label, int channel) {
			this.label = label;
			this.channel = channel;
		}
	}

	Map<Integer, String> m_chords; // Stores chords ordered by bar index starting by 0.
	List<Instrument> m_instruments;
	List<List<MelodicPattern>> m_melodicPatterns; // column, row

	public Phrase() {
		m_chords = new TreeMap<>();
		m_melodicPatterns = new ArrayList<>();
		m_instruments = new ArrayList<>();
	}

	public void set(int row, int column, MelodicPattern pattern) {
		List<MelodicPattern> patterns = m_melodicPatterns.get(row);

		// Filling the list with 'null' values if column > size
		for(int i = patterns.size(); i <= column; i++) {
			patterns.add(null);
		}

		// Adding the new pattern
		patterns.set(column, pattern);
	}

	public int getLength() {
		int result = 0;
		for(int index : m_chords.keySet()) {
			result = Math.max(result, index + 1);
		}
		return result;
	}

	public String getChord(int index) {
		return m_chords.get(index);
	}

	public Map<Instrument, MelodicPattern> getMelodicPatterns(int index) {
		Map<Instrument, MelodicPattern> result = new LinkedHashMap<>();
		for(int i = 0; i < m_instruments.size(); i++) {
			Instrument instrument = m_instruments.get(i);
			List<MelodicPattern> patterns = m_melodicPatterns.get(i);
			result.put(instrument, patterns.get(index));
		}
		return result;
	}

	public MelodicPattern getMelodicPattern(int row, int column) {
		Instrument instrument = getInstrument(row);
		if(instrument == null) {
			return null;
		}
		List<MelodicPattern> instrumentPatterns = m_melodicPatterns.get(row);
		if(column > instrumentPatterns.size() - 1) {
			return null;
		}
		return instrumentPatterns.get(column);
	}

	public Instrument addInstrument(String label, int channel) {
		Instrument result = new Instrument(label, channel);
		m_instruments.add(result);
		m_melodicPatterns.add(new ArrayList<MelodicPattern>());
		return result;
	}

	public Instrument getInstrument(int row) {
		return m_instruments.get(row);
	}

	public List<Instrument> getInstruments() {
		return m_instruments;
	}

	public void setChord(int index, String name) {
		m_chords.put(index, name);
	}
}