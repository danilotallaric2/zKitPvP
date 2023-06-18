package it.danilotallaric.zkitpvp.tablist;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabComponent {

    private List<String> header = new ArrayList<>(), footer = new ArrayList<>();

    public TabComponent addLineToHeader(String line) {
        header.add(line);
        return this;
    }

    public TabComponent addLineToFooter(String line) {
        footer.add(line);
        return this;
    }

    public void sendToPlayer(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer nmsPlayer = craftPlayer.getHandle();

        IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + String.join("\n", this.header) + "\"}");
        IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + String.join("\n", this.footer) + "\"}");

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(header);

        try {
            Field field = packet.getClass().getDeclaredField("b");

            field.setAccessible(true);
            field.set(packet, footer);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        nmsPlayer.playerConnection.sendPacket(packet);
    }

}
