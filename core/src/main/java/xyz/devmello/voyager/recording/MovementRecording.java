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

import xyz.devmello.voyager.logging.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A recording of Pathfinder's movement. A {@code MovementRecording} is little
 * more than a wrapper for a {@link List} that stores many instances of
 * {@link MovementRecord}. {@code MovementRecording}s can be serialized so
 * that they can be imported or exported, but it's generally suggested you
 * make use of a library like GSON instead of Java's built-in serialization
 * tooling so that your recordings are more accessible to non-JDK-based
 * environments.
 *
 * @author Colin Robertson
 * @since 0.7.1
 */
public class MovementRecording implements Serializable {
    private final List<MovementRecord> recording = new ArrayList<>(100);

    /**
     * Record a single {@link MovementRecording}. This will add the supplied
     * recording to the internal {@link List} of {@link MovementRecord}s.
     *
     * @param record the record to add.
     */
    public void record(MovementRecord record) {
        recording.add(record);
    }

    /**
     * Get a list of all of the recorded {@link MovementRecord}s.
     *
     * @return a {@code List}, containing all of the {@link MovementRecord}s
     * that have been recorded.
     */
    public List<MovementRecord> getRecording() {
        return recording;
    }

    /**
     * Clear the entire list of {@link MovementRecord}s.
     */
    public void clear() {
        recording.clear();
    }

    /**
     * Get the number of {@link MovementRecord}s that have been recorded.
     *
     * @return the number of {@link MovementRecord}s that have been recorded.
     */
    public int size() {
        return recording.size();
    }

    public MovementRecording() {
        // Default constructor for serialization
    }

    /**
     * Create a new {@code MovementRecording} with the supplied
     * {@link List} of {@link MovementRecord}s.
     *
     * @param file the file of {@link MovementRecord}s to use.
     */
    public MovementRecording(File file) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        load(file);
    }

    /**
     * Create a new {@code MovementRecording} with the supplied
     * {@link MovementRecord}s.
     *
     * @param recording the {@link MovementRecord}s to use.
     */
    public MovementRecording(MovementRecord... recording) {
        if (recording == null) {
            throw new NullPointerException("recording cannot be null");
        }
        this.recording.addAll(Arrays.asList(recording));
    }


    /**
     * Save the recording to a file. This will write the contents of the
     * recording to the supplied file.
     *
     * @param file the file to save to.
     * @return true if the file was saved successfully, false otherwise.
     */
    public boolean save(File file) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file))) {
            for (MovementRecord record : recording) {
                writer.write(record.toString());
                writer.newLine();
            }
            writer.flush();
            return true;
        } catch (Exception e) {
            Logger.error(MovementRecording.class, "Error saving recording (" +file.getName()+ "): "  + e.getMessage());

        }
        return false;
    }

    /**
     * Load a recording from a file. This will read the contents of the
     * supplied file and add them to the recording.
     *
     * @param file the file to load from.
     * @return true if the file was loaded successfully, false otherwise.
     */
    public boolean load(File file) {
        if (file == null) {
            throw new NullPointerException("file cannot be null");
        }
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                MovementRecord record = MovementRecord.parse(line);
                recording.add(record);
            }
            return true;
        } catch (Exception e) {
            Logger.error(MovementRecording.class, "Error loading recording (" + file.getName() + "): " + e.getMessage());
        }
        return false;
    }
}
