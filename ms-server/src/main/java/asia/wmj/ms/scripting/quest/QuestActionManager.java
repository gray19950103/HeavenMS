/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package asia.wmj.ms.scripting.quest;

import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.scripting.npc.NPCConversationManager;
import asia.wmj.ms.server.MapleItemInformationProvider;
import asia.wmj.ms.server.quest.MapleQuest;
import asia.wmj.ms.server.quest.actions.ExpAction;
import asia.wmj.ms.server.quest.actions.MesoAction;

/**
 *
 * @author RMZero213
 */
public class QuestActionManager extends NPCConversationManager {
    private boolean start; // this is if the script in question is start or end
    private int quest;

    public QuestActionManager(MapleClient c, int quest, int npc, boolean start) {
        super(c, npc, null);
        this.quest = quest;
        this.start = start;
    }

    public int getQuest() {
        return quest;
    }

    public boolean isStart() {
        return start;
    }

    @Override
    public void dispose() {
        QuestScriptManager.getInstance().dispose(this, getClient());
    }

    public boolean forceStartQuest() {
        return forceStartQuest(quest);
    }

    public boolean forceCompleteQuest() {
        return forceCompleteQuest(quest);
    }
    
    // For compatibility with some older scripts...
    public void startQuest() {
        forceStartQuest();
    }
    
    // For compatibility with some older scripts...
    public void completeQuest() {
        forceCompleteQuest();
    }
    
    @Override
    public void gainExp(int gain) {
        ExpAction.runAction(getPlayer(), gain);
    }
    
    @Override
    public void gainMeso(int gain) {
        MesoAction.runAction(getPlayer(), gain);
    }
    
    public String getMedalName() {  // usable only for medal quests (id 299XX)
        MapleQuest q = MapleQuest.getInstance(quest);
        return MapleItemInformationProvider.getInstance().getName(q.getMedalRequirement());
    }
}
