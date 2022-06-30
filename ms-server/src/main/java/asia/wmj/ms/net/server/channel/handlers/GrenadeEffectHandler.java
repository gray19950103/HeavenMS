/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
import asia.wmj.ms.constants.skills.Gunslinger;
import asia.wmj.ms.constants.skills.NightWalker;
import asia.wmj.ms.net.AbstractMaplePacketHandler;
import asia.wmj.ms.tools.MaplePacketCreator;
import asia.wmj.ms.tools.data.input.SeekableLittleEndianAccessor;
import java.awt.Point;
import asia.wmj.ms.tools.FilePrinter;

/*
 * @author GabrielSin
 */
public class GrenadeEffectHandler extends AbstractMaplePacketHandler {
 
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        Point position = new Point(slea.readInt(), slea.readInt());
        int keyDown = slea.readInt();
        int skillId = slea.readInt();
       
        switch (skillId) {
            case NightWalker.POISON_BOMB:
            case Gunslinger.GRENADE:
                int skillLevel = chr.getSkillLevel(skillId);
                if (skillLevel > 0) {
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.throwGrenade(chr.getId(), position, keyDown, skillId, skillLevel), position);
                }
                break;
            default:
                FilePrinter.printError(FilePrinter.UNHANDLED_EVENT, "The skill id: " + skillId + " is not coded in " + this.getClass().getName() + ".");
        }
    }
 
}