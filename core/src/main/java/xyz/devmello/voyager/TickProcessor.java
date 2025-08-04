/*
 * Copyright (c) 2021.
 *
 * This file is part of the "Pathfinder2" project, available here:
 * <a href="https://github.com/Wobblyyyy/Pathfinder2">GitHub</a>
 *
 * This project is licensed under the GNU GPL V3 license.
 * <a href="https://www.gnu.org/licenses/gpl-3.0.en.html">GNU GPL V3</a>
 */

package xyz.devmello.voyager;

import xyz.devmello.voyager.execution.ExecutorManager;
import xyz.devmello.voyager.plugin.PathfinderPluginManager;

import xyz.devmello.voyager.pathgen.zones.ZoneProcessor;

/**
 * Utility class responsible for processing Pathfinder's ticking operations.
 * This class has very little use outside of the uses inside of the
 * {@link Voyager} class: it simply exists to improve code organization.
 *
 * @author Colin Robertson
 * @since 1.4.2
 */
public class TickProcessor {

    private TickProcessor() {}

    public static Voyager runPreTick(
        Voyager voyager,
        boolean isMinimal,
        PathfinderPluginManager pluginManager,
        ZoneProcessor zoneProcessor
    ) {
        voyager.getRobot().odometry().tick();
        pluginManager.preTick(voyager);

        if (!isMinimal) {
            zoneProcessor.update(voyager);
        }

        return voyager;
    }

    public static Voyager runExecutorTick(
        Voyager voyager,
        ExecutorManager executorManager
    ) {
        executorManager.tick();

        return voyager;
    }

    public static Voyager runOnTick(
        Voyager voyager,
        boolean isMinimal,
        PathfinderPluginManager pluginManager,
        Runnable runOnTickOperations
    ) {
        pluginManager.onTick(voyager);

        if (!isMinimal) {
            runOnTickOperations.run();
        }

        return voyager;
    }
}
