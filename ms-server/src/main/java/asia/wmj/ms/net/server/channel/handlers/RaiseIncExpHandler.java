package asia.wmj.ms.net.server.channel.handlers;

import java.util.Map;

import asia.wmj.ms.client.MapleCharacter;
import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.client.MapleQuestStatus;
import asia.wmj.ms.client.inventory.MapleInventory;
import asia.wmj.ms.client.inventory.MapleInventoryType;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.client.inventory.manipulator.MapleInventoryManipulator;
import asia.wmj.ms.server.MapleItemInformationProvider;
import asia.wmj.ms.server.MapleItemInformationProvider.QuestConsItem;
import asia.wmj.ms.server.quest.MapleQuest;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Xari
 * @author Ronan - added concurrency protection and quest progress limit
 */
public class RaiseIncExpHandler extends AbstractMaplePacketHandler {

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte inventorytype = slea.readByte();//nItemIT
        short slot = slea.readShort();//nSlotPosition
        int itemid = slea.readInt();//nItemID
        
        if (c.tryacquireClient()) {
            try {
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                QuestConsItem consItem = ii.getQuestConsumablesInfo(itemid);
                if (consItem == null) {
                    return;
                }

                int infoNumber = consItem.questid;
                Map<Integer, Integer> consumables = consItem.items;
                
                MapleCharacter chr = c.getPlayer();
                MapleQuest quest = MapleQuest.getInstanceFromInfoNumber(infoNumber);
                if (!chr.getQuest(quest).getStatus().equals(MapleQuestStatus.Status.STARTED)) {
                    c.announce(MaplePacketCreator.enableActions());
                    return;
                }
                
                int consId;
                MapleInventory inv = chr.getInventory(MapleInventoryType.getByType(inventorytype));
                inv.lockInventory();
                try {
                    consId = inv.getItem(slot).getItemId();
                    if (!consumables.containsKey(consId) || !chr.haveItem(consId)) {
                        return;
                    }

                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.getByType(inventorytype), (short) slot, (short) 1, false, true);
                } finally {
                    inv.unlockInventory();
                }
                
                int questid = quest.getId();
                int nextValue = Math.min(consumables.get(consId) + c.getAbstractPlayerInteraction().getQuestProgressInt(questid, infoNumber), consItem.exp * consItem.grade);
                c.getAbstractPlayerInteraction().setQuestProgress(questid, infoNumber, nextValue);
                
                c.announce(MaplePacketCreator.enableActions());
            } finally {
                c.releaseClient();
            }
        }
    }
}
