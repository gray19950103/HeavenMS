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
import asia.wmj.ms.client.inventory.Item;
import asia.wmj.ms.client.inventory.MapleInventoryType;
import asia.wmj.ms.tools.Randomizer;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.client.inventory.manipulator.MapleInventoryManipulator;
import asia.wmj.ms.server.MapleItemInformationProvider;
import asia.wmj.ms.server.life.MapleLifeFactory;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author AngelSL
 */
public final class UseSummonBagHandler extends AbstractMaplePacketHandler {

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        //[4A 00][6C 4C F2 02][02 00][63 0B 20 00]
        if (!c.getPlayer().isAlive()) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        slea.readInt();
        short slot = slea.readShort();
        int itemId = slea.readInt();
        Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemId) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(itemId);
            for (int z = 0; z < toSpawn.length; z++) {
                int[] toSpawnChild = toSpawn[z];
                if (Randomizer.nextInt(100) < toSpawnChild[1]) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(toSpawnChild[0]), c.getPlayer().getPosition());
                }
            }
        }
        c.announce(MaplePacketCreator.enableActions());
    }
}
