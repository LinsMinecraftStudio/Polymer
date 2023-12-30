package io.github.linsminecraftstudio.polymer.schedule;

import io.github.linsminecraftstudio.polymer.objectutils.StoreableConsumer;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Setter
@Getter
public class SpecialDayTask extends BFRunnable {
    private Date date;

    public SpecialDayTask(@NotNull BukkitRunnable bukkitRunnable) {
        super(bukkitRunnable);
    }

    public SpecialDayTask(@NotNull StoreableConsumer<ScheduledTask> paper) {
        super(paper);
    }

    public void run(JavaPlugin plugin) {
        run(plugin, false);
    }

    public void run(JavaPlugin plugin, boolean async) {
        throw new IllegalStateException("Can only run by BFScheduler");
    }

    public void cancel() {
        throw new UnsupportedOperationException("Can only cancel by BFScheduler");
    }
}
