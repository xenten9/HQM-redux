package hardcorequesting.commands;

import net.minecraft.command.ICommandSender;

import com.xenten9.hardcorequesting.ModInformation;
import com.xenten9.hardcorequesting.Translator;

public class CommandVersion extends CommandBase {

    public CommandVersion() {
        super("version");
        permissionLevel = 0;
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments) {
        sendChat(sender, "\u00A7a" + Translator.translate("hqm.message.version", ModInformation.VERSION));
    }
}
