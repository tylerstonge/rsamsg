public class CommandParser {

    Server server;

    public CommandParser(Server server) {
        this.server = server;
    }

    public void parseCommand(String cmd) {
        if (cmd.length() > 0 && cmd.charAt(0) == '!') {
            // this is a command
            String[] split = cmd.split(" ");
            switch(split[0]) {
                case "!connect":
                    if (split.length < 2) {
                        GUI.log.addLine("usage: !connect <ip>");
                    } else {
                        server.connect(split[1]);
                    }
                    break;
            }
        } else{
            // this is just a message
            if (server.getSelectedConnection() != null) {
                if (server.getSelectedConnection().isClosed())
                    server.setSelectedConnection(null);
                if (server.getSelectedConnection() != null)
                    server.getSelectedConnection().sendMessage(cmd);
            } else {
                GUI.log.addLine("* you are not connected to anyone");
            }
        }
    }
}
