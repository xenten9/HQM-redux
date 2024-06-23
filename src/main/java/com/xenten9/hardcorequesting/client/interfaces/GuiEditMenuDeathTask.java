package hardcorequesting.client.interfaces;

import net.minecraft.entity.player.EntityPlayer;

import com.xenten9.hardcorequesting.SaveHelper;
import com.xenten9.hardcorequesting.quests.QuestTaskDeath;

public class GuiEditMenuDeathTask extends GuiEditMenuExtended {

    private int deaths;
    private QuestTaskDeath task;

    public GuiEditMenuDeathTask(GuiBase gui, EntityPlayer player, QuestTaskDeath task) {
        super(gui, player, true, -1, -1, 25, 30);

        deaths = task.getDeaths();
        this.task = task;

        textBoxes.add(new TextBoxNumber(gui, 0, "hqm.deathTask.reqDeathCount") {

            @Override
            protected void setValue(int number) {
                deaths = number;
            }

            @Override
            protected int getValue() {
                return deaths;
            }
        });
    }

    @Override
    protected void save(GuiBase gui) {
        task.setDeaths(deaths);
        SaveHelper.add(SaveHelper.EditType.DEATH_CHANGE);
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
}
