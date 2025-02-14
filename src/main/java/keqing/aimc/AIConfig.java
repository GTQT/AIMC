package keqing.aimc;

import keqing.aimc.aimc.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

@Config(modid = Tags.MOD_ID)
public class AIConfig {
    @Config.RequiresMcRestart
    @Config.Name("API Request Body")
    public static String jsonBody = "{\"model\":\"qwen2.5-7b-instruct-1m\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}";

    @Config.RequiresMcRestart
    @Config.Name("API Endpoint")
    public static String url = "http://26.184.80.94:1234/v1/chat/completions";

    public static void init(File configFile) {
        ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}