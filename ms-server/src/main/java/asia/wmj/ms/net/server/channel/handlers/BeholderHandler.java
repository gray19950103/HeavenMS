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
package asia.wmj.ms.net.server.channel.handlers;

import asia.wmj.ms.client.MapleClient;
import asia.wmj.ms.constants.skills.DarkKnight;
import java.util.Collection;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.server.maps.MapleSummon;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author BubblesDev
 */
public final class BeholderHandler extends AbstractMaplePacketHandler {//Summon Skills noobs

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        //System.out.println(slea.toString());
        Collection<MapleSummon> summons = c.getPlayer().getSummonsValues();
        int oid = slea.readInt();
        MapleSummon summon = null;
        for (MapleSummon sum : summons) {
            if (sum.getObjectId() == oid) {
                summon = sum;
            }
        }
        if (summon != null) {
            int skillId = slea.readInt();
            if (skillId == DarkKnight.AURA_OF_BEHOLDER) {
                slea.readShort(); //Not sure.
            } else if (skillId == DarkKnight.HEX_OF_BEHOLDER) {
                slea.readByte(); //Not sure.
            }            //show to others here
        } else {
            c.getPlayer().clearSummons();
        }
    }
}
