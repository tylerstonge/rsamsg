public class CommandParser {

    Server server;

    public CommandParser(Server server) {
        this.server = server;
    }

    public void parseCommand(String cmd) {
        if (cmd.charAt(0) == '!') {
            // this is a command
            String[] split = cmd.split(" ");
            switch(split[0]) {
                case "!connect":
                    if (split.length < 2) {
                        System.out.println("usage: !connect <ip>");
                    } else {
                        server.connect(split[1]);
                    }
                    break;
            }
        } else{
            // this is just a message
            if (server.getSelectedConnection().isClosed())
                server.setSelectedConnection(null);
            if (server.getSelectedConnection() != null)
                server.getSelectedConnection().sendMessage(cmd);
        }
    }
}
