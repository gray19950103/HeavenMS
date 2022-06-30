package asia.wmj.ms.net.server.channel.handlers;

import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.client.keybind.MapleQuickslotBinding;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Shavit
 */
public class QuickslotKeyMappedModifiedHandler extends AbstractMaplePacketHandler
{
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c)
    {
        // Invalid size for the packet.
        if(slea.available() != MapleQuickslotBinding.QUICKSLOT_SIZE * Integer.BYTES ||
        // not logged in-game
            c.getPlayer() == null)
        {
            return;
        }

        byte[] aQuickslotKeyMapped = new byte[MapleQuickslotBinding.QUICKSLOT_SIZE];

        for(int i = 0; i < MapleQuickslotBinding.QUICKSLOT_SIZE; i++)
        {
            aQuickslotKeyMapped[i] = (byte) slea.readInt();
        }

        c.getPlayer().changeQuickslotKeybinding(aQuickslotKeyMapped);
    }
}
