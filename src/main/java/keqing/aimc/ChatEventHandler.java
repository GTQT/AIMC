package keqing.aimc;

import com.google.gson.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import okhttp3.*;

import java.io.IOException;

import static keqing.aimc.AIMCConfig.aiSwitch;

@Mod.EventBusSubscriber
public class ChatEventHandler {

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        if(!aiSwitch.startAI)return;
        String message = event.getMessage();
        if (message.startsWith(aiSwitch.startsWith)) { // 使用 "!ai" 作为触发AI聊天的前缀
            String query = message.substring(aiSwitch.startsWith.length()+1).trim();
            sendToAI(event.getPlayer(), query);
        }
    }

    private static void sendToAI(EntityPlayerMP player, String query) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(aiSwitch.TimeOut, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(aiSwitch.TimeOut, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String jsonBody = String.format(aiSwitch.jsonBody, query);
            RequestBody body = RequestBody.create(JSON, jsonBody);

            Request request = new Request.Builder()
                    .url(aiSwitch.url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 打印详细的错误信息到日志
                    System.err.println("连接AI服务器失败: " + e.getMessage());
                    player.sendMessage(new TextComponentString("连接AI服务器失败: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (response.isSuccessful()) {
                            String result = responseBody.string();
                            // 打印完整的响应内容到日志
                            System.out.println("AI Response: " + result);
                            // 解析JSON响应并提取AI回复
                            String aiResponse = parseAIResponse(result);
                            player.sendMessage(new TextComponentString("AI: " + aiResponse));
                        } else {
                            player.sendMessage(new TextComponentString("AI错误响应 (" + response.code() + "): " + responseBody.string()));
                        }
                    }
                }
            });
        } catch (Exception e) {
            // 打印详细的错误信息到日志
            System.err.println("创建请求失败: " + e.getMessage());
            player.sendMessage(new TextComponentString("创建请求失败: " + e.getMessage()));
        }
    }

    private static String parseAIResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
            if (jsonObject.has("choices") && jsonObject.get("choices").isJsonArray()) {
                JsonArray choices = jsonObject.getAsJsonArray("choices");
                for (JsonElement choiceElement : choices) {
                    JsonObject choice = choiceElement.getAsJsonObject();
                    if (choice.has("message") && choice.get("message").isJsonObject()) {
                        JsonObject message = choice.getAsJsonObject("message");
                        if (message.has("content") && message.get("content").isJsonPrimitive()) {
                            return message.get("content").getAsString();
                        }
                    }
                }
            }
            return "无法解析AI响应";
        } catch (Exception e) {
            return "解析AI响应时发生错误: " + e.getMessage();
        }
    }
}