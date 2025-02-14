package keqing.aimc;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

public class AIMessagePacket implements IMessage {
    String message;

    // 必须有无参构造函数
    public AIMessagePacket() {}

    public AIMessagePacket(String message) {
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        this.message = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    // 添加消息处理器
    public static class Handler implements IMessageHandler<AIMessagePacket, IMessage> {
        @Override
        public IMessage onMessage(AIMessagePacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                // 处理接收到的消息
                Minecraft.getMinecraft().player.sendMessage(
                        new TextComponentString("[AI] 收到消息: " + message.message)
                );
            });
            return null;
        }
    }
}