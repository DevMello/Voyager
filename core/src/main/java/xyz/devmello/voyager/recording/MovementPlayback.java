/*
 * Copyright (c) 2021.
 *
 * This file is part of the "Pathfinder2" project, available here:
 * <a href="https://github.com/Wobblyyyy/Pathfinder2">GitHub</a>
 *
 * This project is licensed under the GNU GPL V3 license.
 * <a href="https://www.gnu.org/licenses/gpl-3.0.en.html">GNU GPL V3</a>
 */

package xyz.devmello.voyager.recording;

import xyz.devmello.voyager.Voyager;
import xyz.devmello.voyager.logging.Logger;
import xyz.devmello.voyager.time.Time;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Used in conjunction with {@link MovementRecorder} to make Pathfinder
 * follow a set of pre-recorded motion snapshots.
 *
 * @author Colin Robertson
 * @since 0.6.1
 */
public class MovementPlayback {
    private final Voyager voyager;
    private MovementRecording recording;
    private boolean isPlaying = false;
    private int lastIndex = 0;
    private double lastSwitchMs = 0;

    /**
     * Create a new {@code MovementPlayback}.
     *
     * @param voyager the instance of Pathfinder that will be controlled
     *                   by this playback manager.
     */
    public MovementPlayback(Voyager voyager) {
        this.voyager = voyager;
    }

    /**
     * Start playing back a movement recording.
     *
     * @param recording the recording to play back.
     */
    public void startPlayback(MovementRecording recording) {
        lastIndex = 0;
        lastSwitchMs = 0;
        isPlaying = true;
        this.recording = recording;
    }

    /**
     * Start playing back a movement recording.
     *
     * @param file the file to load the recording from.
     */
    public void startPlayback(File file) {
        startPlayback(new MovementRecording(file));
    }

    /**
     * Stop the playback of a movement recording.
     */
    public void stopPlayback() {
        isPlaying = false;
    }

    public void tick() {
        if (!isPlaying) return;

        double currentMs = Time.ms();
        if (lastSwitchMs == 0) lastSwitchMs = currentMs;
        double elapsedMs = currentMs - lastSwitchMs;

        MovementRecord record = recording.getRecording().get(lastIndex);
        double requiredMs = record.getElapsedMs();
        if (elapsedMs >= requiredMs) {
            voyager.setTranslation(record.getTranslation());
            lastIndex += 1;
            lastSwitchMs = currentMs;
            if (lastIndex >= recording.getRecording().size()) stopPlayback();
        }
    }

    /**
     * Load a recording from a file. This will read the contents of the
     * supplied file and create a new {@link List<MovementRecord>} object.
     *
     * @param file the file to load from.
     * @return a new {@link List<MovementRecord>} object.
     */
    public List<MovementRecord> load(File file) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        List<MovementRecord> recording = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                MovementRecord record = MovementRecord.parse(line);
                recording.add(record);
            }
        } catch (Exception e) {
            Logger.error(MovementRecording.class, "Error loading recording (" +file.getName()+ "): "  + e.getMessage());
        }
        return recording;
    }
}
