package hardcorequesting.quests;

import net.minecraft.entity.player.EntityPlayer;

import com.xenten9.hardcorequesting.EventHandler;

public class QuestTaskReputationTarget extends QuestTaskReputation {

    public QuestTaskReputationTarget(Quest parent, String description, String longDescription) {
        super(parent, description, longDescription, 0);

        register(EventHandler.Type.OPEN_BOOK, EventHandler.Type.REPUTATION_CHANGE);
    }

    @Override
    public void onOpenBook(EventHandler.BookOpeningEvent event) {
        checkReputation(event.getPlayer());
    }

    @Override
    public void onReputationChange(EventHandler.ReputationEvent event) {
        checkReputation(event.getPlayer());
    }

    private void checkReputation(EntityPlayer player) {
        if (parent.isEnabled(player) && parent.isAvailable(player)
            && this.isVisible(player)
            && !this.isCompleted(player)) {
            if (isPlayerInRange(player)) {
                completeTask(
                    player.getGameProfile()
                        .getName());
                parent.sendUpdatedDataToTeam(player);
            }

        }
    }
}
