package com.xenten9.hardcorequesting.proxies;

import com.xenten9.hardcorequesting.quests.Quest;
import com.xenten9.hardcorequesting.quests.QuestTicker;

public class CommonProxy {

    public void initSounds(String path) {

    }

    public void initRenderers() {

    }

    public void init() {
        Quest.serverTicker = new QuestTicker(false);
    }

    public boolean isClient() {
        return false;
    }

    public boolean isServer() {
        return true;
    }

}
