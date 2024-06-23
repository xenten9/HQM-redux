package hardcorequesting.proxies;

import com.xenten9.hardcorequesting.BlockHighlightRemover;
import com.xenten9.hardcorequesting.client.sounds.SoundHandler;
import com.xenten9.hardcorequesting.quests.Quest;
import com.xenten9.hardcorequesting.quests.QuestTicker;

public class ClientProxy extends CommonProxy {

    public SoundHandler soundHandler;

    @Override
    public void initSounds(String path) {
        // init all the sounds
    }

    @Override
    public void initRenderers() {
        // init the rendering stuff

        // MinecraftForge.EVENT_BUS.register(new GUIOverlay(Minecraft.getMinecraft()));
        new BlockHighlightRemover();
    }

    @Override
    public void init() {
        Quest.serverTicker = new QuestTicker(false);
        Quest.clientTicker = new QuestTicker(true);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public boolean isServer() {
        return false;
    }
}
