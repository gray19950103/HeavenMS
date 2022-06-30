package asia.wmj.ms.net.server.channel.handlers;

import asia.wmj.ms.client.MapleCharacter.DelayedQuestUpdate;
import asia.wmj.ms.client.MapleCharacter;
import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.client.MapleQuestStatus;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.scripting.quest.QuestScriptManager;
import asia.wmj.ms.server.quest.MapleQuest;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Xari
 */
public class RaiseUIStateHandler extends AbstractMaplePacketHandler {

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int infoNumber = slea.readShort();
        
        if (c.tryacquireClient()) {
            try {
                MapleCharacter chr = c.getPlayer();
                MapleQuest quest = MapleQuest.getInstanceFromInfoNumber(infoNumber);
                MapleQuestStatus mqs = chr.getQuest(quest);
                
                QuestScriptManager.getInstance().raiseOpen(c, (short) infoNumber, mqs.getNpc());
                
                if (mqs.getStatus() == MapleQuestStatus.Status.NOT_STARTED) {
                    quest.forceStart(chr, 22000);
                    c.getAbstractPlayerInteraction().setQuestProgress(quest.getId(), infoNumber, 0);
                } else if (mqs.getStatus() == MapleQuestStatus.Status.STARTED) {
                    chr.announceUpdateQuest(DelayedQuestUpdate.UPDATE, mqs, mqs.getInfoNumber() > 0);
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}