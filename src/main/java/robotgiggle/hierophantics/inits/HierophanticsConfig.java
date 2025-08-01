package robotgiggle.hierophantics.inits;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

@Config(name = "hierophantics")
public class HierophanticsConfig implements ConfigData {
    public int maxMinds = 64;

    public static void init() {
        AutoConfig.register(HierophanticsConfig.class, JanksonConfigSerializer::new);
    }
}
