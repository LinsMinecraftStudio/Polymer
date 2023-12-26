package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.other.CooldownMap;
import org.jetbrains.annotations.NotNull;

public abstract class SubNeedsCooldownCommand<K> extends SubCommand implements ICommand.INeedsCooldownCommand<K> {
    private final CooldownMap<K> cooldownMap;

    public SubNeedsCooldownCommand(@NotNull String name) {
        super(name);
        this.cooldownMap = new CooldownMap<>();
    }

    @Override
    public CooldownMap<K> getCooldownMap() {
        return cooldownMap;
    }
}
