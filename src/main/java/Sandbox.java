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

import mcs.MSequencer;
import mcs.devices.Roland_FP30;
import mcs.melody.Block;
import mcs.melody.Chord;
import mcs.melody.Note;
import mcs.melody.Time;
import mcs.midi.Drum;
import mcs.midi.Message;
import mcs.midi.MidiInterface;
import mcs.midi.SequenceUtils;
import mcs.midi.Tone;
import mcs.pattern.DrumPattern;
import mcs.pattern.MelodicPattern;
import mcs.pattern.Pattern;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;

public class Sandbox {

	public static void main(String[] args) throws MidiUnavailableException {
		launchExercisesTimer(9, 40);
	}

	public static void launchExercisesTimer(int laps, int exerciceDuration_s) throws MidiUnavailableException {

		MidiDevice device = MidiInterface.getMidiOutDevice();
		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		int restDuration_s = 60 - exerciceDuration_s;
		final int countdown = 5;

		try {
			device.open();
			Receiver receiver = device.getReceiver();

			Tone.selectInstrument(receiver, 0, Roland_FP30.GRAND_PIANO_1);
			final int velocity = Note.Dynamic.FORTE_FORTISSIMO.velocity;

			// Countdown before start

			for(int c = countdown - 1; c > 0; c--) {
				MSequencer.sendNote(receiver, 0, Note.C3, velocity, 200);
				Thread.sleep(1000);
			}

			for(int lap = 0; lap < laps; lap++) {
				MSequencer.sendNote(receiver, 0, Note.C4, velocity, 200);
				Thread.sleep(exerciceDuration_s * 1000);

				MSequencer.sendNote(receiver, 0, Note.C3, velocity, 200);

				if(lap < laps - 1) {
					Thread.sleep((restDuration_s - countdown + 1) * 1000);

					for(int c = countdown - 1; c > 0; c--) {
						MSequencer.sendNote(receiver, 0, Note.C3, velocity, 200);
						Thread.sleep(1000);
					}
				}
			}

			for(int c = countdown - 1; c > 0; c--) {
				MSequencer.sendNote(receiver, 0, Note.C4 + countdown * 2, velocity, 200);
				Thread.sleep(300);
			}

		} catch(InterruptedException e) {
			e.printStackTrace();
		} catch(InvalidMidiDataException e) {
			e.printStackTrace();
		} finally {
			device.close();
		}
	}

	public static void playLive() throws MidiUnavailableException {
		Block block = new Block(new Time.TimeSignature(4, 4), 4, Drum.CHANNEL);

		block.add(Note.C2, Note.Dynamic.FORTISSIMO.velocity, 1, 4);
		block.add(Note.C2 + 4, Note.Dynamic.FORTISSIMO.velocity, 2, 4);
		block.add(Note.C2 + 7, Note.Dynamic.FORTISSIMO.velocity, 3, 4);

		MidiDevice device = MidiInterface.getMidiOutDevice();
		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		try {
			device.open();
			Receiver receiver = device.getReceiver();

			MSequencer sequencer = new MSequencer(receiver, 60);
			sequencer.set(block);

			sequencer.start();

			Thread.sleep(block.getDuration_ms(sequencer.getTempo_bpm()));

			sequencer.stop();

		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			device.close();
		}

	}

	static void createSequenceWithPatterns() throws InvalidMidiDataException, IOException, InterruptedException, MidiUnavailableException {
		Sequence sequence = SequenceUtils.createSequence();
		Track track = sequence.createTrack();
		SequenceUtils.setTempo(sequence, 150);

		// Adding drums
		String drumPattern = "rock_1";
		long nextTick = DrumPattern.importPattern(new File(String.format("pattern/drum/%s.dpt", drumPattern)), track, 0);
		DrumPattern.importPattern(new File(String.format("pattern/drum/%s.dpt", drumPattern)), track, nextTick);

		// Adding piano
		//		Tone.selectInstrument(track, 0, Roland_FP30.VIBRAPHONE);
		String pianoPattern = "arpeggio_1";
		nextTick = MelodicPattern.importPattern(new File(String.format("pattern/melodic/%s.mpt", pianoPattern)), track, 0, Chord.K(Note.E3),
				0);
		nextTick = MelodicPattern.importPattern(new File(String.format("pattern/melodic/%s.mpt", pianoPattern)), track, 0, Chord.K(Note.E3),
				nextTick);

		play(sequence);
	}

	static void playDrumPattern(String patternName, int bpm)
			throws InvalidMidiDataException, IOException, MidiUnavailableException, InterruptedException {
		int ticksPerBeat = Pattern.DEFAULT_PATTERN_TICKS_PER_BEAT;
		Sequence sequence = new Sequence(Sequence.PPQ, ticksPerBeat);
		Track track = sequence.createTrack();
		File patternFile = new File(String.format("pattern/drum/%s.dpt", patternName));
		long tickOffset = DrumPattern.importPattern(patternFile, track, 0);

		track.add(new MidiEvent(Time.createTempoMessage(bpm), 0));

		log(track);

		play(sequence);
	}

	static void playMelodicPattern(String patternName, int bpm)
			throws InvalidMidiDataException, MidiUnavailableException, InterruptedException, IOException {
		int ticksPerBeat = Pattern.DEFAULT_PATTERN_TICKS_PER_BEAT;
		Sequence sequence = new Sequence(Sequence.PPQ, ticksPerBeat);
		Track track = sequence.createTrack();
		int channel = 0;
		File patternFile = new File(String.format("pattern/melodic/%s.mpt", patternName));
		long tickOffset = MelodicPattern.importPattern(patternFile, track, channel, Chord.K(Note.E3), 0);
		tickOffset = MelodicPattern.importPattern(patternFile, track, channel, Chord.Km(Note.G3), tickOffset);
		tickOffset = MelodicPattern.importPattern(patternFile, track, channel, Chord.K(Note.E3), tickOffset);
		tickOffset = MelodicPattern.importPattern(patternFile, track, channel, Chord.K7(Note.E3), tickOffset);

		track.add(new MidiEvent(Time.createTempoMessage(bpm), 0));

		log(track);

		play(sequence);
	}

	static void play(Sequence sequence) throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
		long sequenceDuration_ms = sequence.getMicrosecondLength() / 1000;
		log("Sequence duration: %d ms", sequenceDuration_ms);

		Sequencer sequencer = MidiSystem.getSequencer();

		sequencer.setSequence(sequence);

		sequencer.open();
		sequencer.start();
		Thread.sleep(sequenceDuration_ms);
		sequencer.stop();
		sequencer.close();
	}

	static void readMidiFile(String relativeFilePath)
			throws InvalidMidiDataException, IOException, MidiUnavailableException, InterruptedException {
		final String[] NOTE_NAMES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

		int bpm = 60;

		Sequence sequence = MidiSystem.getSequence(new File(relativeFilePath));
		int ticksPerBeat = sequence.getResolution();
		log("%d ticks per beat", ticksPerBeat);

		Track[] tracks = sequence.getTracks();
		log("%d track(s) loaded", tracks.length);

		Sequence newSequence = new Sequence(Sequence.PPQ, ticksPerBeat);
		Track newTrack = SequenceUtils.mergeTracks(tracks, newSequence);

		Time.changeTempo(newTrack, bpm);

		long ticks = newTrack.ticks();
		long beats = ticks / ticksPerBeat;
		int seconds = (int) (60.0 * (float) beats / (float) bpm);

		log("Track: size = %d, ticks = %d, beats = %d, seconds = %d", newTrack.size(), ticks, beats, seconds);

		for(int i = 0; i < newTrack.size(); i++) {
			MidiEvent event = newTrack.get(i);
			MidiMessage message = event.getMessage();

			log("@%d %s", event.getTick(), Message.toString(message));
		}

		Sequencer sequencer = MidiSystem.getSequencer();
		sequencer.setSequence(newSequence);
		sequencer.open();
		sequencer.start();
		Thread.sleep(50000);
		sequencer.stop();
		sequencer.close();
	}

	static void playDrums() throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
		MidiDevice device = MidiInterface.getMidiOutDevice();
		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}

		try {
			device.open();
			Receiver receiver = device.getReceiver();

			//			ReceiverHelper.selectInstrument(receiver, 0, Roland_FP30.NYLON_STRING_GUITAR);
			//
			//			for(int i = 0; i < 100; i++) {
			//				int key = ThreadLocalRandom.current().nextInt(Note.C1, Note.C5);
			//				sendNote(receiver, 0, key, 75, 50);
			//			}

			int duration_ms = 300;

			for(int f = 10; f < 100; f += 5) {
				MSequencer.sendNote(receiver, Drum.CHANNEL, Drum.ACOUSTIC_SNARE, f, 70);
			}

			for(int i = 0; i < 7; i++) {
				MSequencer.sendNotes(receiver, Drum.CHANNEL, new int[] { Drum.BASS_DRUM_1, Drum.OPEN_HIT_HAT }, 100, duration_ms);
				MSequencer.sendNote(receiver, Drum.CHANNEL, Drum.PEDAL_HIT_HAT, 100, duration_ms);
				MSequencer.sendNote(receiver, Drum.CHANNEL, Drum.ACOUSTIC_SNARE, 100, duration_ms);
				MSequencer.sendNote(receiver, Drum.CHANNEL, Drum.PEDAL_HIT_HAT, 100, duration_ms);
			}

			MSequencer.sendNotes(receiver, Drum.CHANNEL, new int[] { Drum.BASS_DRUM_1, Drum.OPEN_HIT_HAT }, 100, duration_ms);

		} finally {
			device.close();
		}
	}

	static void playNotes() throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
		Synthesizer synthesizer = MidiSystem.getSynthesizer();

		synthesizer.open();
		Receiver receiver = synthesizer.getReceiver();

		//int key = 60; // Middle C
		int velocity = 64; // default to middle volume

		int channel = 0;
		int instrument = 4;
		long duration_ms = 10;

		Instrument[] instruments = synthesizer.getAvailableInstruments();
		log("%d instrument(s)", instruments.length);
		for(int i = 0; i < instruments.length; i++) {
			log("#%d %s", i, instruments[i].getName());
		}

		MidiChannel[] channels = synthesizer.getChannels();
		log("%d channel(s)", channels.length);

		channels[channel].programChange(instrument);
		log("Instrument %d: %s", instrument, instruments[instrument].getName());

		for(int key = 20; key < 100; key++) {
			log("key %d", key);
			ShortMessage on = new ShortMessage();
			on.setMessage(ShortMessage.NOTE_ON, channel, key, velocity);
			ShortMessage off = new ShortMessage();
			off.setMessage(ShortMessage.NOTE_OFF, channel, key, velocity);
			receiver.send(on, -1);
			Thread.sleep(duration_ms);
			receiver.send(off, -1);
			Thread.sleep(duration_ms);
		}

		synthesizer.close();
	}

	static void log(String format, Object... args) {
		String message = String.format(format, args);
		System.out.println(message);
	}

	static void log(Track track) {
		for(int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);
			MidiMessage message = event.getMessage();

			log("@%d %s", event.getTick(), Message.toString(message));
		}
	}
}