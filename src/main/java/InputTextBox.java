import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.KeyStroke;

public class InputTextBox extends TextBox {

    Server server;
    CommandParser parser;

    public InputTextBox(Server server) {
        this.server = server;
        this.parser = new CommandParser(server);
    }
    
    @Override
    public Interactable.Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Enter) {
            parser.parseCommand(getText());
            setText("");
            takeFocus();
            return Interactable.Result.HANDLED;
        }
        return super.handleKeyStroke(keyStroke);
    }
} 
