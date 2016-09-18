import java.util.ArrayList;
import java.io.IOException;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;


public class GUI {
   
    private Label header; 
    private Server server;
    public static OutputTextBox log;

    public GUI() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        
        // Start server
        server = new Server("dropkick");
        server.start();
        
        // version data top of screen
        TerminalSize size = screen.getTerminalSize();
        
        Panel panel = new Panel();
        panel.setLayoutManager(new AbsoluteLayout());            
        panel.setTheme(new SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK));

        // header
        header = new Label("[sotrm v0.0.0-dev]");
        header.setSize(new TerminalSize(size.getColumns(), 1));
        header.setPosition(TerminalPosition.TOP_LEFT_CORNER);
        header.setTheme(new SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK));
        panel.addComponent(header);

        // main text area
        log = new OutputTextBox("", OutputTextBox.Style.MULTI_LINE);
        log.setSize(new TerminalSize(size.getColumns(), size.getRows() - 3));
        log.setPosition(new TerminalPosition(0, 1));
        log.setReadOnly(true);
        panel.addComponent(log);

        // user input textbox
        InputTextBox userin = new InputTextBox(server);
        userin.setSize(new TerminalSize(size.getColumns(), 1));
        userin.setPosition(new TerminalPosition(0, size.getRows()));
        userin.takeFocus();
        userin.setTheme(new SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK));
        panel.addComponent(userin);

        // window
        BasicWindow window = new BasicWindow();
        ArrayList<Window.Hint> hints = new ArrayList<Window.Hint>();
        hints.add(Window.Hint.FULL_SCREEN);
        window.setHints(hints);
        window.setTheme(new SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK));
        window.setSize(size);
        window.addWindowListener(new WindowListenerAdapter() {
            public void onResized(Window window, TerminalSize oldSize, TerminalSize newSize) {
                log.setSize(new TerminalSize(newSize.getColumns(), newSize.getRows() - 3));
                userin.setPosition(new TerminalPosition(0, newSize.getRows() - 1));
                userin.setSize(new TerminalSize(newSize.getColumns(), 1));
            } 
        });
        window.setComponent(panel);
        
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        gui.addWindowAndWait(window);
    }

}
