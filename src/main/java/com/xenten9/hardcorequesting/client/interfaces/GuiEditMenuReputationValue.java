package com.xenten9.hardcorequesting.client.interfaces;

import net.minecraft.entity.player.EntityPlayer;

import com.xenten9.hardcorequesting.SaveHelper;
import com.xenten9.hardcorequesting.reputation.ReputationMarker;

public class GuiEditMenuReputationValue extends GuiEditMenuExtended {

    private ReputationMarker marker;
    private int value;

    public GuiEditMenuReputationValue(GuiBase gui, EntityPlayer player, ReputationMarker marker) {
        super(gui, player, true, -1, -1, 25, 30);

        this.marker = marker;
        this.value = marker.getValue();

        textBoxes.add(new TextBoxNumber(gui, 0, "hqm.repValue.tierValue") {

            @Override
            protected void setValue(int number) {
                value = number;
            }

            @Override
            protected int getValue() {
                return value;
            }

            @Override
            protected boolean isNegativeAllowed() {
                return true;
            }
        });
    }

    @Override
    protected void onArrowClick(boolean left) {

    }

    @Override
    protected String getArrowText() {
        return null;
    }

    @Override
    protected String getArrowDescription() {
        return null;
    }

    @Override
    protected void save(GuiBase gui) {
        marker.setValue(value);
        GuiQuestBook.selectedReputation.sort();
        SaveHelper.add(SaveHelper.EditType.REPUTATION_MARKER_CHANGE);
    }
}
