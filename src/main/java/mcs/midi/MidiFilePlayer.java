/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.midi;

import mcs.melody.Time;
import mcs.utils.FileUtils;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MidiFilePlayer {

	static Map<Long, List<MidiMessage>> toSynchronizedMessagesMap(Track track) {
		Map<Long, List<MidiMessage>> result = new TreeMap<>();

		for(int i = 0; i < track.size(); i++) {
			MidiEvent event = track.get(i);
			MidiMessage message = event.getMessage();
			long tick = event.getTick();

			List<MidiMessage> synchronizedMessages = result.get(tick);
			if(synchronizedMessages == null) {
				synchronizedMessages = new ArrayList<>();
				result.put(tick, synchronizedMessages);
			}
			synchronizedMessages.add(message);
		}

		return result;
	}

	public static void main(String[] args) throws InvalidMidiDataException, IOException, MidiUnavailableException, InterruptedException {
		File midiFile = new File(args[0]);
		int tempo_bpm = Integer.valueOf(args[1]);

		// Reading MIDI sequence
		Sequence sequence = MidiSystem.getSequence(midiFile);
		sequence.getResolution();
		int ticksPerBeat = sequence.getResolution();
		long beats = sequence.getTickLength() / ticksPerBeat;

		// Merging tracks together
		Sequence newSequence = new Sequence(Sequence.PPQ, ticksPerBeat);
		Track[] tracks = sequence.getTracks();
		Track track = SequenceUtils.mergeTracks(tracks, newSequence);

		long tickDuration_ms = Time.computeTickDuration_ms(tempo_bpm, ticksPerBeat);

		FileUtils.log("Tempo:\t%d bpm", tempo_bpm);
		FileUtils.log("Beats:\t%d", beats);
		FileUtils.log("Ticks per beat:\t%d", ticksPerBeat);
		FileUtils.log("Tick duration:\t%d ms", tickDuration_ms);

		// Getting device to play track
		MidiDevice device = MidiUtils.getMidiOutDevice();
		if(device == null) {
			device = MidiSystem.getSynthesizer();
		}
		device.open();

		Receiver receiver = device.getReceiver();

		for(int channel = 0; channel < 10; channel++) {
			if(channel != Drum.CHANNEL) {
				ReceiverHelper.stopAllNotes(receiver, channel);
			}
		}

		try {
			long start_ms = System.currentTimeMillis();

			for(Map.Entry<Long, List<MidiMessage>> entry : toSynchronizedMessagesMap(track).entrySet()) {
				long tick = entry.getKey();
				long timeToPlay_ms = start_ms + Time.computeTickTime_ms(tick, tempo_bpm, ticksPerBeat);

				Thread.sleep(Math.max(0, timeToPlay_ms - System.currentTimeMillis()));

				List<MidiMessage> messages = entry.getValue();
				for(MidiMessage message : messages) {
					receiver.send(message, -1L);
				}
			}
		} finally {

			for(int channel = 0; channel < 10; channel++) {
				if(channel != Drum.CHANNEL) {
					ReceiverHelper.stopAllNotes(receiver, channel);
				}
			}

			device.close();
		}
	}
}