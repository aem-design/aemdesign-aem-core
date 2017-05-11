package design.aem.impl.commands;

import design.aem.CommandHandler;
import design.aem.CommandService;
import org.apache.felix.scr.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Service(value = CommandService.class)
public class CommandServiceImpl implements CommandService {

    @Reference(
            referenceInterface = CommandHandler.class,
            bind = "registerHandler",
            unbind = "unregisterHandler",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            policy = ReferencePolicy.DYNAMIC
    )
    private final Collection<CommandHandler> commandHandlers;

    private static final Logger log = LoggerFactory.getLogger(CommandServiceImpl.class);

    public CommandServiceImpl() {
        commandHandlers = new CopyOnWriteArraySet<CommandHandler>();
    }

    public void registerHandler(CommandHandler commandHandler)
    {
        log.debug("Binding CommandHandler: {}", commandHandler);
        commandHandlers.add(commandHandler);
    }

    public void unregisterHandler(CommandHandler commandHandler)
    {
        log.debug("Unbinding CommandHandler: {}", commandHandler);
        commandHandlers.remove(commandHandler);
    }

    @Override
    public Iterator<CommandHandler> iterator() {
        return commandHandlers.iterator();
    }

    @Override
    public CommandHandler getCommandHandler(String cmd) {
        for (CommandHandler handler : commandHandlers) {
            if (handler.getSupportedCommands().contains(cmd)) {
                return handler;
            }
        }
        return null;
    }
}
