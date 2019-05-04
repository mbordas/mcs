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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Phrase {

	public static class Instrument {
		public String label;
		public int channel;

		public Instrument(String label, int channel) {
			this.label = label;
			this.channel = channel;
		}
	}

	Map<Integer, String> m_chords; // Stores chords ordered by bar index starting by 0.
	Map<Instrument, Map<Integer, Pattern>> m_patterns;

	public Phrase() {
		m_chords = new TreeMap<>();
		m_patterns = new LinkedHashMap<>();
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

	public Instrument addInstrument(String label, int channel) {
		Instrument result = new Instrument(label, channel);
		m_patterns.put(result, new TreeMap<Integer, Pattern>());
		return result;
	}

	public Set<Instrument> getInstruments() {
		return m_patterns.keySet();
	}

	public void setChord(int index, String name) {
		m_chords.put(index, name);
	}
}