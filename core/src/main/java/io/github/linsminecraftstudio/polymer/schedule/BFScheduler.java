package io.github.linsminecraftstudio.polymer.schedule;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A scheduler for compatible with Folia (A fork of paper).
 * Feel free to copy to your project(s) or edit it.
 */
public class BFScheduler {
    private final JavaPlugin plugin;
    private boolean modern;

    public BFScheduler(JavaPlugin plugin) {
        testClasses();
        this.plugin = plugin;
    }

    private void testClasses() {
        try {
            //for checking legacy paper builds or its forks
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            modern = true;
        } catch (ClassNotFoundException e) {
            modern = false;
        }
    }

    public void schedule(Runnable runnable) {
        if (modern) {
            Bukkit.getGlobalRegionScheduler().run(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public void scheduleAsync(Runnable runnable) {
        if (modern) {
            Bukkit.getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, t -> runnable.run());
        }
    }

    public void scheduleDelay(Runnable runnable, long delayTicks) {
        if (modern) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
        }
    }

    public void scheduleRepeating(Runnable runnable, long delayTicks, long repeatTicks) {
        if (modern) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayTicks, repeatTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, repeatTicks);
        }
    }

    public void scheduleDelayAsync(Runnable runnable, long delayTicks, long delayMilis) {
        if (modern) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, t -> runnable.run(), delayMilis, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks);
        }
    }

    public void scheduleRepeatingAsync(Runnable runnable, long delayTicks, long repeatTicks, long delayMilis, long repeatMilis) {
        if (modern) {
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayMilis, repeatMilis, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, repeatTicks);
        }
    }

    public void schedule(BFRunnable runnable) {
        runnable.run();
    }

    public void scheduleAsync(BFRunnable runnable) {
        runnable.run(true);
    }

    public void scheduleDelay(BFRunnable runnable, long delayTicks) {
        if (modern) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, Objects.requireNonNull(runnable.getPaper()), delayTicks);
        } else {
            Objects.requireNonNull(runnable.getBukkit()).runTaskLater(plugin, delayTicks);
        }
    }

    public void scheduleRepeating(BFRunnable runnable, long delayTicks, long repeatTicks) {
        if (modern) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, Objects.requireNonNull(runnable.getPaper()), delayTicks, repeatTicks);
        } else {
            Objects.requireNonNull(runnable.getBukkit()).runTaskTimer(TempPolymer.getInstance(), delayTicks, repeatTicks);
        }
    }

    public void scheduleDelayAsync(BFRunnable runnable, long delayTicks, long delayMilis) {
        if (modern) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, Objects.requireNonNull(runnable.getPaper()), delayMilis, TimeUnit.MILLISECONDS);
        } else {
            Objects.requireNonNull(runnable.getBukkit()).runTaskLaterAsynchronously(TempPolymer.getInstance(), delayTicks);
        }
    }

    public void scheduleRepeatingAsync(BFRunnable runnable, long delayTicks, long repeatTicks, long delayMilis, long repeatMilis) {
        if (modern) {
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, Objects.requireNonNull(runnable.getPaper()), delayMilis, repeatMilis, TimeUnit.MILLISECONDS);
        } else {
            Objects.requireNonNull(runnable.getBukkit()).runTaskTimerAsynchronously(TempPolymer.getInstance(), delayTicks, repeatTicks);
        }
    }

    public boolean isRunning(BFRunnable runnable) {
        if (runnable.getTaskType() == TaskType.PAPER) {
            try {
                return Objects.requireNonNull(Objects.requireNonNull(runnable.getAdvancedPaper()).getValue())
                        .getExecutionState() == ScheduledTask.ExecutionState.RUNNING;
            } catch (Exception e) {
                return false;
            }
        }
        try {
            return Bukkit.getScheduler().isCurrentlyRunning(Objects.requireNonNull(runnable.getBukkit()).getTaskId());
        } catch (Exception e) {
            return false;
        }
    }
}
