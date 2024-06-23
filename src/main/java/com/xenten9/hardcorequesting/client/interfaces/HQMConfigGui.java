package com.xenten9.hardcorequesting.client.interfaces;

import net.minecraft.client.gui.GuiScreen;

import com.xenten9.hardcorequesting.ModInformation;
import com.xenten9.hardcorequesting.config.ModConfig;

import cpw.mods.fml.client.config.GuiConfig;

public class HQMConfigGui extends GuiConfig {

    public HQMConfigGui(GuiScreen parentScreen) {
        super(
            parentScreen,
            ModConfig.getConfigElements(),
            ModInformation.ID,
            false,
            false,
            GuiConfig.getAbridgedConfigPath(ModConfig.config.toString()));
    }
}
