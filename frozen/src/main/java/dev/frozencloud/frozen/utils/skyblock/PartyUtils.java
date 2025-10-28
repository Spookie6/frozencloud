package dev.frozencloud.frozen.utils.skyblock;

import dev.frozencloud.frozen.events.impl.ChatPacketEvent;
import net.minecraft.client.Minecraft;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

// Credit -> https://chattriggers.com/modules/v/BloomCore (Ty for the regex lines pookie <3)
public class PartyUtils {
    private static HashMap<String, String> members = new HashMap<>(); // {"DhrRico": "[MVP+]"};
    private static byte size = 0;
    private static String leader = null;
    private static boolean inParty = false;

    private final static String[] disbandRegexes = {
            "^.+ has disbanded the party!$",
            "^You have been kicked from the party by .+$",
            "^The party was disbanded because all invites expired and the party was empty\\.$",
            "^The party was disbanded because the party leader disconnected\\.",
            "^You left the party.$",
            "^You are not currently in a party\\.$",
            "^Party Members \\(\\d+\\)$"
    };

    @SubscribeEvent
    public void onChatReceive(ChatPacketEvent event) {
        String originalMsg = event.message;
        String msg = event.message;

        for (String regex : disbandRegexes) if (msg.matches(regex)) disband();

        if (msg.matches("^((\\[(.)+])?) *(\\w{3,16}) joined the party\\.$") || msg.matches("Party > ((\\[(.)+])?) *(\\w{3,16}): .+$") || msg.matches("You have joined ((\\[(.)+])?) *(\\w{3,16})'s party!")) {
            if (msg.startsWith("Party > ")) msg = msg.replace("Party > ", "");
            if (msg.startsWith("You have joined")) {
                msg = msg.replace("You have joined ", "");
                msg = msg.replaceAll("'s", "");
            }
            String username = msg.split(" ")[msg.startsWith("[") ? 1 : 0];
            String rank = msg.startsWith("[") ? msg.split(" ")[0] : "";
            addMember(username, rank);

            if (originalMsg.startsWith("You have joined")) leader = username;
        }
        if (msg.matches("^((\\[(.)+])?) *(\\w{3,16}) (has left the party\\.$|has been removed from the party\\.$)")) {
            String username = msg.split(" ")[msg.startsWith("[") ? 1 : 0];
            removeMember(username);
        }
        if (msg.matches("^Party (Leader|Moderators|Members): (.+)$")) {
            msg = msg.replace("Party Leader: ", "");
            msg = msg.replace("Party Moderators: ", "");
            msg = msg.replace("Party Members: ", "");
            String[] segments = msg.split(" ● ");
            for (String segment : segments) {
                String username = segment.split(" ")[segment.startsWith("[") ? 1 : 0];
                String rank = segment.startsWith("[") ? segment.split(" ")[0] : "";

                if (originalMsg.startsWith("Party Leader: ")) leader = username;
                addMember(username, rank);
            }
        }

//        [MVP+] DhrRico invited [MVP+] DhrEric to the party! They have 60 seconds to accept.

        if (msg.matches("^The party leader, ((\\[(.)+])?) *(\\w{3,16}) has disconnected, they have 5 minutes to rejoin before the party is disbanded\\.$")) {
            msg = msg.replace("The party leader, ", "");
            String username = msg.split(" ")[msg.startsWith("[") ? 1 : 0];
            String rank = msg.startsWith("[") ? msg.split(" ")[0] : "";
            leader = username;
            addMember(username, rank);
        }
        if (msg.matches("^((\\[(.)+])?) *(\\w{3,16}) has disconnected, they have 5 minutes to rejoin before they are removed from the party\\.$")) {
            msg = msg.replace("The party leader, ", "");
            String username = msg.split(" ")[msg.startsWith("[") ? 1 : 0];
            String rank = msg.startsWith("[") ? msg.split(" ")[0] : "";
            addMember(username, rank);
        }
        if (msg.matches("^The party was transferred to ((\\[(.)+])?) *(\\w{3,16}) by ((\\[(.)+])?) *(\\w{3,16})$")) {
            msg = msg.replace("The party was transferred to ", "");

            String[] segments = msg.split(" by ");

            String username = segments[0].split(" ")[segments[0].startsWith("[") ? 1 : 0];
            String rank = segments[0].startsWith("[") ? segments[0].split(" ")[0] : "";
            String username1 = segments[1].split(" ")[segments[1].startsWith("[") ? 1 : 0];
            String rank1 = segments[1].startsWith("[") ? segments[1].split(" ")[0] : "";

            leader = username;
            addMember(username, rank);
            addMember(username1, rank1);
        }
        if (msg.matches("^The party was transferred to ((\\[(.)+])?) *(\\w{3,16}) because ((\\[(.)+])?) *(\\w{3,16}) left$")) {
            msg = msg.replace("The party was transferred to ", "");
            removeMember(leader);
            String username = msg.split(" ")[msg.startsWith("[") ? 1 : 0];
            String rank = msg.startsWith("[") ? msg.split(" ")[0] : "";
            addMember(username, rank);
        }
        if (msg.matches("Party Finder > (\\w{3,16}) joined the dungeon group! \\((\\W+) Level (\\d+)\\)$")) {
            msg = msg.replace("Party Finder > ", "");
            String username = msg.split(" ")[0];
            addMember(username, "");
        }

        inParty = size >0;
    }

    public static void addMember(String user, String rank) {
        members.put(user, rank);

        if (!user.equals(Minecraft.getMinecraft().thePlayer.getName()))
            addMember(Minecraft.getMinecraft().thePlayer.getName(), "");

        size = (byte) members.size();
    }

    public static void removeMember(String user) {
        if (members.get(user) == null) return;
        members.remove(user);
        size = (byte) members.size();
    }

    public static void disband() {
        members.clear();
        size = 0;
        leader = null;
        inParty = false;
    }

    public static HashMap<String, String> getMembers() {
        return members;
    }

    public static boolean inParty() {
        return inParty;
    }

    public static boolean memberInParty (String username) {
        return members.get(username) != null;
    }
}
