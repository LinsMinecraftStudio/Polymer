package io.github.linsminecraftstudio.polymer.schedule;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.objectutils.LockableValue;
import io.github.linsminecraftstudio.polymer.objectutils.StoreableConsumer;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BFRunnable {
    private LockableValue<BukkitRunnable> runnable = new LockableValue<>();
    private LockableValue<StoreableConsumer<ScheduledTask>> task = new LockableValue<>();

    public static BFRunnable of(BukkitRunnable r) {
        return new BFRunnable(r);
    }

    public static BFRunnable of(Consumer<ScheduledTask> r) {
        return new BFRunnable(r);
    }

    public BFRunnable(@NotNull BukkitRunnable bukkitRunnable) {
        if (this.task.isLocked()) {
            throw new IllegalStateException("There's already a task on this runnable!");
        }
        this.runnable = new LockableValue<>(bukkitRunnable);
        this.runnable.lock();
    }

    public BFRunnable(@NotNull Consumer<ScheduledTask> paper) {
        this(OtherUtils.toStoreableConsumer(paper));
    }

    public BFRunnable(@NotNull StoreableConsumer<ScheduledTask> paper) {
        if (this.runnable.isLocked()) {
            throw new IllegalStateException("There's already a task on this runnable!");
        }
        this.task = new LockableValue<>(paper);
        this.task.lock();
    }

    public @Nullable BukkitRunnable getBukkit() {
        return runnable.getValue();
    }

    public @Nullable Consumer<ScheduledTask> getPaper() {
        if (getAdvancedPaper() != null) {
            return getAdvancedPaper().getOriginal();
        }
        return null;
    }

    public @Nullable StoreableConsumer<ScheduledTask> getAdvancedPaper() {
        return task.getValue();
    }

    public @NotNull TaskType getTaskType() {
        return task.isValueNull() ?
                runnable.isValueNull() ? TaskType.UNKNOWN : TaskType.BUKKIT
                : TaskType.PAPER;
    }

    public void run() {
        run(false);
    }

    public void run(boolean async) {
        TaskType type = getTaskType();
        if (type == TaskType.BUKKIT) {
            if (runnable.getValue() != null) {
                BukkitRunnable bukkit = runnable.getValue();
                if (async) {
                    bukkit.runTaskAsynchronously(TempPolymer.getInstance());
                } else {
                    bukkit.runTask(TempPolymer.getInstance());
                }
            }
        } else {
            if (task.getValue() != null) {
                Consumer<ScheduledTask> consumer = task.getValue();
                if (async) {
                    Bukkit.getAsyncScheduler().runNow(TempPolymer.getInstance(), consumer);
                } else {
                    Bukkit.getGlobalRegionScheduler().run(TempPolymer.getInstance(), consumer);
                }
            }
        }
    }

    public void cancel() {
        if (runnable.isValueNull() && task.isValueNull()) {
            throw new NullPointerException("There's no task on this runnable!");
        } else if (!task.isValueNull()) {
            StoreableConsumer<ScheduledTask> storeableConsumer = task.getValue();
            if (storeableConsumer != null && storeableConsumer.getValue() != null) {
                ScheduledTask task1 = storeableConsumer.getValue();
                task1.cancel();
            }
        } else {
            BukkitRunnable runnable1 = runnable.getValue();
            if (runnable1 != null) {
                runnable1.cancel();
            }
        }
    }
}
