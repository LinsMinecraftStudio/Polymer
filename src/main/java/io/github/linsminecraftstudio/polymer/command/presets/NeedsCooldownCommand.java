package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.other.CooldownMap;
import org.jetbrains.annotations.NotNull;

public abstract class NeedsCooldownCommand<K> extends PolymerCommand implements ICommand.INeedsCooldownCommand<K> {
    private final CooldownMap<K> cooldownMap;

    public NeedsCooldownCommand(@NotNull String name) {
        super(name);
        this.cooldownMap = new CooldownMap<>();
    }

    @Override
    public CooldownMap<K> getCooldownMap() {
        return cooldownMap;
    }
}
