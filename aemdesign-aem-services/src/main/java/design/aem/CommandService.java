package design.aem;

public interface CommandService extends Iterable<CommandHandler> {
    CommandHandler getCommandHandler(String cmd);
}
