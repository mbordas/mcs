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

package mcs;

import mcs.melody.Time;
import mcs.midi.Message;
import mcs.pattern.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class MSequencer {

	private final Receiver m_receiver;
	private final int m_tempo_bpm;
	private Pattern m_pattern;

	private final AtomicBoolean m_running = new AtomicBoolean(false);
	private final Runnable m_loop;
	private Long m_restartAtTick;
	private Thread m_thread;

	private final Semaphore m_stopped = new Semaphore(0);
	private final Semaphore m_started = new Semaphore(0);

	public MSequencer(Receiver receiver, int tempo_bpm) {
		m_receiver = receiver;
		m_tempo_bpm = tempo_bpm;
		m_restartAtTick = 0L;
		m_loop = buildLoop();
	}

	public int getTempo_bpm() {
		return m_tempo_bpm;
	}

	//
	// Playback management
	//

	public void enableLooping(boolean enable) {
		if(enable) {
			m_restartAtTick = m_pattern.size();
		} else {
			m_restartAtTick = null;
		}
	}

	public void start() throws InterruptedException {
		if(!m_running.get()) {
			m_running.set(true);

			m_thread = new Thread(m_loop);
			m_thread.start();

			m_started.acquire(1);
		}
	}

	public void stop() throws InterruptedException {
		if(m_running.get()) {
			m_running.set(false);
			m_stopped.acquire(1);
		}
	}

	public void playNote(int channel, int key, int velocity, long duration_ms) throws InvalidMidiDataException, InterruptedException {
		sendNote(m_receiver, channel, key, velocity, duration_ms);
	}

	//
	// Pattern management
	//

	public void set(Pattern pattern) {
		m_pattern = pattern;
	}

	//
	// Internal
	//

	/**
	 * Creates the main loop of the Sequencer.
	 *
	 * @return
	 */
	private Runnable buildLoop() {
		return new Runnable() {
			@Override
			public void run() {
				long tick = 0;
				long tickDuration_ms = Time.computeTickDuration_ms(m_tempo_bpm, m_pattern.getTicksPerBeat());

				System.out.println("Tick duration: " + tickDuration_ms + " ms");

				long lastTick_ms = System.currentTimeMillis();

				while(m_running.get()) {

					m_started.release(1); // Semaphore is used to manage asynchronous start/stop of loop

					// If pattern not set or played to the end, escaping the loop
					System.out.print("t" + tick);
					if(m_pattern == null) {
						break;
					} else if(tick > m_pattern.size()) {
						break;
					}

					// Computing MIDI messages to be played simultaneously
					List<ShortMessage> events = null;

					if(m_pattern.hasEvents(tick)) {
						System.out.println();
						events = m_pattern.getEventList(tick);
						for(ShortMessage event : events) { // Just for logging
							System.out.println(" " + Message.toString(event));
						}
					} else {
						System.out.println(" -");
					}

					// Before playing the notes, we computes how long it took to build the MIDI messages
					// And this elapsed time will shorten the duration of sleep
					try {
						long elapsedTime_ms = System.currentTimeMillis() - lastTick_ms;
						Thread.sleep(tickDuration_ms - elapsedTime_ms);

						if(m_running.get() && events != null) {
							for(ShortMessage event : events) {
								m_receiver.send(event, -1);
							}
						}
						lastTick_ms = System.currentTimeMillis();
					} catch(InterruptedException e) {
						e.printStackTrace();
						break;
					}

					tick++;

					// If looping is enabled, then the 'tick' counter is reset
					if(m_restartAtTick != null && tick >= m_restartAtTick) {
						tick = 0;
					}
				}

				m_stopped.release(1);
			}
		};
	}

	/**
	 * Sends MIDI message to the receiver. In other words it really plays the note.
	 *
	 * @param receiver
	 * @param channel
	 * @param key
	 * @param velocity
	 * @param duration_ms
	 * @throws InvalidMidiDataException
	 * @throws InterruptedException
	 */
	public static void sendNote(Receiver receiver, int channel, int key, int velocity, long duration_ms)
			throws InvalidMidiDataException, InterruptedException {
		ShortMessage on = new ShortMessage();
		on.setMessage(ShortMessage.NOTE_ON, channel, key, velocity);
		ShortMessage off = new ShortMessage();
		off.setMessage(ShortMessage.NOTE_OFF, channel, key, velocity);
		receiver.send(on, -1);
		Thread.sleep(duration_ms);
		receiver.send(off, -1);
	}

	public static void sendNotes(Receiver receiver, int channel, int[] keys, int velocity, long duration_ms)
			throws InterruptedException, InvalidMidiDataException {
		for(int key : keys) {
			receiver.send(new ShortMessage(ShortMessage.NOTE_ON, channel, key, velocity), -1);
		}

		Thread.sleep(duration_ms);

		for(int key : keys) {
			receiver.send(new ShortMessage(ShortMessage.NOTE_OFF, channel, key, velocity), -1);
		}
	}

}