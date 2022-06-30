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
import asia.wmj.ms.client.MapleCharacter;
import asia.wmj.ms.client.MapleMount;
import asia.wmj.ms.client.inventory.Item;
import asia.wmj.ms.client.inventory.MapleInventory;
import asia.wmj.ms.client.inventory.MapleInventoryType;
import asia.wmj.ms.constants.game.ExpTable;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.client.inventory.manipulator.MapleInventoryManipulator;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author PurpleMadness
 * @author Ronan
 */
public final class UseMountFoodHandler extends AbstractMaplePacketHandler {
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.skip(4);
        short pos = slea.readShort();
        int itemid = slea.readInt();
        
        MapleCharacter chr = c.getPlayer();
        MapleMount mount = chr.getMount();
        MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);
        
        if (c.tryacquireClient()) {
            try {
                Boolean mountLevelup = null;
                
                useInv.lockInventory();
                try {
                    Item item = useInv.getItem(pos);
                    if (item != null && item.getItemId() == itemid && mount != null) {
                        int curTiredness = mount.getTiredness();
                        int healedTiredness = Math.min(curTiredness, 30);

                        float healedFactor = (float) healedTiredness / 30;
                        mount.setTiredness(curTiredness - healedTiredness);

                        if (healedFactor > 0.0f) {
                            mount.setExp(mount.getExp() + (int) Math.ceil(healedFactor * (2 * mount.getLevel() + 6)));
                            int level = mount.getLevel();
                            boolean levelup = mount.getExp() >= ExpTable.getMountExpNeededForLevel(level) && level < 31;
                            if (levelup) {
                                mount.setLevel(level + 1);
                            }
                            
                            mountLevelup = levelup;
                        }

                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, true, false);
                    }
                } finally {
                    useInv.unlockInventory();
                }
                
                if (mountLevelup != null) {
                    chr.getMap().broadcastMessage(MaplePacketCreator.updateMount(chr.getId(), mount, mountLevelup));
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}