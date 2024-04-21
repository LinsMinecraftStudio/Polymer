package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.other.CooldownMap;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class NeedsCooldownCommand<K> extends PolymerCommand implements ICommand.INeedsCooldownCommand<K> {
    private final CooldownMap<K> cooldownMap;

    public NeedsCooldownCommand(@NotNull String name, @NotNull PolymerPlugin plugin) {
        super(name, plugin);
        this.cooldownMap = new CooldownMap<>();
    }

    @Override
    public CooldownMap<K> getCooldownMap() {
        return cooldownMap;
    }
}
