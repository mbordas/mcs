/*
 * Copyright (c) 2012-2019 Smart Grid Energy
 * All Right Reserved
 * http://www.smartgridenergy.fr
 */

package mcs.gui;

import mcs.MSequencer;
import mcs.melody.Time;
import mcs.midi.Drum;
import mcs.midi.MidiUtils;
import mcs.pattern.Pattern;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DrumPatternEditor {

    JButton m_clearBtn, m_playBtn, m_stopBtn;

    DrumGrid m_drumGrid;

    MSequencer m_sequencer;

    public DrumPatternEditor(MSequencer sequencer) {
        m_sequencer = sequencer;
    }

    ActionListener m_actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == m_clearBtn) {
                m_drumGrid.clear();
            } else if (e.getSource() == m_playBtn) {
                play();
            } else if (e.getSource() == m_stopBtn) {
                stop();
            }
        }
    };

    public void show() {
        // create main frame
        JFrame frame = new JFrame("Drum Pattern Editor");
        Container content = frame.getContentPane();
        // set layout on content pane
        content.setLayout(new BorderLayout());
        // create draw area
        m_drumGrid = new DrumGrid(new Time.TimeSignature(4, 4), 1, Drum.getBasicKeyMapping());

        // add to content pane
        content.add(m_drumGrid, BorderLayout.CENTER);

        // create controls to apply colors and call clear feature
        JPanel controls = new JPanel();

        m_clearBtn = new JButton("Clear");
        m_clearBtn.addActionListener(m_actionListener);
        m_playBtn = new JButton("Play");
        m_playBtn.addActionListener(m_actionListener);
        m_stopBtn = new JButton("Stop");
        m_stopBtn.addActionListener(m_actionListener);

        // add to panel
        controls.add(m_clearBtn);
        controls.add(m_playBtn);
        controls.add(m_stopBtn);

        // add to content pane
        content.add(controls, BorderLayout.NORTH);

        frame.pack();
        // can close frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // show the swing paint result
        frame.setVisible(true);
    }

    void play() {
        Pattern pattern = m_drumGrid.toPattern(Drum.CHANNEL);
        System.out.println("Pattern size: " + pattern.size());
        m_sequencer.set(pattern);
        m_sequencer.enableLooping(true);
        try {
            m_sequencer.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void stop() {
        try {
            m_sequencer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MidiUnavailableException {
        MidiDevice device = MidiUtils.getMidiOutDevice();

        if (device == null) {
            device = MidiSystem.getSynthesizer();
        }

        device.open();
        Receiver receiver = device.getReceiver();

        MSequencer sequencer = new MSequencer(receiver, 60);

        new DrumPatternEditor(sequencer).show();
    }
}