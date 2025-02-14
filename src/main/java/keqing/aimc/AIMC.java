package keqing.aimc;

import keqing.aimc.aimc.Tags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        dependencies = "required-after:forge@[14.23.5.2847,)", // 明确Forge版本要求
        acceptedMinecraftVersions = "[1.12,1.13)" // 版本兼容范围
)
public class AIMC {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.Instance(Tags.MOD_ID)
    public static AIMC instance;

    // 必须添加的构造器
    public AIMC() {
        // 注册配置管理器（如果有）
        MinecraftForge.EVENT_BUS.register(this);
    }

    // 网络频道实例
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(
            String.valueOf(new ResourceLocation(Tags.MOD_ID, "main")));

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AIConfig.init(event.getSuggestedConfigurationFile());

        // 注册网络包（1.12.2正确方式）
        int packetId = 0;
        CHANNEL.registerMessage(
                AIMessagePacket.Handler.class,
                AIMessagePacket.class,
                packetId++,
                Side.CLIENT);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            AICommand.register();
        }
    }
}
