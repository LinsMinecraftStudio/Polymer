package io.github.linsminecraftstudio.polymer.objects;

import io.github.linsminecraftstudio.polymer.utils.FileUtils;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created for tag polymer plugin and make useful methods
 */
public abstract class PolymerPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        if (requireVersion() > OtherUtils.getPolymerVersionWorth()){
            getLogger().log(Level.SEVERE, """
                    Plugin %1$s requires Polymer version %2$d.
                    But the version is %3$d instead.
                    It will disable automatically.
                    """.formatted(getPluginMeta().getName(), requireVersion(), OtherUtils.getPolymerVersionWorth()));
        }
    }

    public abstract int requireVersion();
    protected void completeDefaultConfig(){
        FileUtils.completeFile(this, "config.yml");
    }
    protected void completeLangFile(String... langNames){
        for (String lang : langNames){
            FileUtils.completeLangFile(this, "lang/"+lang+".yml");
        }
    }
}
