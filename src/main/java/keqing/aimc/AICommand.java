package keqing.aimc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AICommand {
    private static final ExecutorService NETWORK_EXECUTOR = Executors.newCachedThreadPool();

    public static void register() {
        ClientCommandHandler.instance.registerCommand(new CommandBase() {
            @Override
            public String getName() {
                return "askai";
            }

            @Override
            public String getUsage(ICommandSender sender) {
                return "/askai <message>";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
                if (args.length == 0) {
                    sender.sendMessage(new TextComponentString("用法: /askai <消息>"));
                    return;
                }

                String question = String.join(" ", args);
                NETWORK_EXECUTOR.execute(() -> {
                    try {
                        String response = AIRequestHandler.sendToAI(question);
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            sender.sendMessage(new TextComponentString("§a[AI] §r" + parseResponse(response)));
                        });
                    } catch (SocketTimeoutException e) {
                        sendErrorMessage(sender,"请求超时");
                    } catch (ConnectException e) {
                        sendErrorMessage(sender,"连接失败");
                    } catch (IOException e) {
                        sendErrorMessage(sender,"通信错误: " + e.getMessage());
                    }
                });
            }

            private void sendErrorMessage(ICommandSender sender,String msg) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    sender.sendMessage(new TextComponentString("§c[错误] §r" + msg));
                });
            }

            private String parseResponse(String json) {
                try {
                    JsonElement jsonElement = new JsonParser().parse(json);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonObject messageObject = jsonObject.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message");
                    String content = messageObject.get("content").getAsString();
                    return content.replace("\\n", "\n");
                } catch (Exception e) {
                    return "无法解析响应";
                }
            }
        });
    }
    private void sendToServer(String message) {
        AIMC.CHANNEL.sendToServer(new AIMessagePacket(message));
    }

    // 添加服务端消息处理
    public static void registerServerHandler() {
        AIMC.CHANNEL.registerMessage(
                (message, ctx) -> {
                    // 服务端接收处理
                    ctx.getServerHandler().player.sendMessage(
                            new TextComponentString("[AI] 服务器收到: " + message.message)
                    );
                    return null;
                },
                AIMessagePacket.class,
                1,
                Side.SERVER
        );
    }
}