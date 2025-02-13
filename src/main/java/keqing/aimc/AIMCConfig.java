package keqing.aimc;

import keqing.aimc.aimc.Tags;
import net.minecraftforge.common.config.Config;
@Config(modid = Tags.MOD_ID)
public class AIMCConfig {
    @Config.Comment("Config options for AIMC")
    public static AISwitch aiSwitch = new AISwitch();
    public static class AISwitch {
        @Config.Name("start AI,need okhttp-4.9.3")
        @Config.RequiresMcRestart
        public boolean startAI=true;

        @Config.Name("json Body")
        @Config.RequiresMcRestart
        public String jsonBody = "{\"model\":\"qwen2.5-7b-instruct-1m\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}";

        @Config.Name("url")
        @Config.RequiresMcRestart
        public String url = "http://26.184.80.94:1234/v1/chat/completions";

        @Config.Name("starts With World")
        @Config.RequiresMcRestart
        public String startsWith = "!ai";

        @Config.Name("Think Time Out")
        @Config.RequiresMcRestart
        public int TimeOut = 60;
    }
}
