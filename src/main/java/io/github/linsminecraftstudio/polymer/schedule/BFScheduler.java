package io.github.linsminecraftstudio.polymer.schedule;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

/**
 * A scheduler for compatible with Folia (A fork of paper).
 */
public class BFScheduler {
    private PolymerPlugin plugin;
    private boolean modern;

    public BFScheduler(PolymerPlugin plugin) {
        testClasses();
        this.plugin = plugin;
    }

    private void testClasses() {
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            modern = true;
        } catch (ClassNotFoundException e) {
            modern = false;
        }
    }

    public void scheduleAsync(Runnable runnable) {
        if (modern) {
            Bukkit.getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, t -> runnable.run());
        }
    }

    public void schedule(Runnable runnable) {
        if (modern) {
            Bukkit.getGlobalRegionScheduler().run(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
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
}
