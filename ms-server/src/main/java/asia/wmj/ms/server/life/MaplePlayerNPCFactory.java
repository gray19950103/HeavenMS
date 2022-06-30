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
package asia.wmj.ms.server.life;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import asia.wmj.ms.net.server.Server;
import asia.wmj.ms.provider.MapleData;
import asia.wmj.ms.provider.MapleDataProvider;
import asia.wmj.ms.provider.MapleDataProviderFactory;
import asia.wmj.ms.provider.MapleDataTool;

/**
 *
 * @author RonanLana
 */
public class MaplePlayerNPCFactory {
    
    private static MapleDataProvider npcData = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Npc.wz"));
    
    private static final Map<Integer, List<MaplePlayerNPC>> dnpcMaps = new HashMap<>();
    private static Integer runningDeveloperOid = 2147483000;  // 647 slots, long enough
    
    public static boolean isExistentScriptid(int scriptid) {
        return npcData.getData(scriptid + ".img") != null;
    }
    
    public static void loadDeveloperRoomMetadata(MapleDataProvider npc) {
        MapleData thisData = npc.getData("9977777.img");
        if(thisData != null) {
            MapleDataProvider map = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Map.wz"));
            
            thisData = map.getData("Map/Map7/777777777.img");
            if(thisData != null) {
                MapleDataProvider sound = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Sound.wz"));
                
                thisData = sound.getData("Field.img");
                if(thisData != null) {
                    MapleData md = thisData.getChildByPath("anthem/brazil");
                    if(md != null) {
                        Server.getInstance().setAvailableDeveloperRoom();
                    }
                }
            }
        }
    }
    
    public static void loadFactoryMetadata() {
        MapleDataProvider npc = npcData;
        loadDeveloperRoomMetadata(npc);

        MapleDataProvider etc = MapleDataProviderFactory.getDataProvider(MapleDataProviderFactory.fileInWZPath("Etc.wz"));
        MapleData dnpcData = etc.getData("DeveloperNpc.img");
        if(dnpcData != null) {
            for (MapleData data : dnpcData.getChildren()) {
                int scriptId = Integer.parseInt(data.getName());

                String name = MapleDataTool.getString("name", data, "");
                int face = MapleDataTool.getIntConvert("face", data, 20000);
                int hair = MapleDataTool.getIntConvert("hair", data, 30000);
                int gender = MapleDataTool.getIntConvert("gender", data, 0);
                byte skin = (byte) MapleDataTool.getIntConvert("skin", data, 0);
                int dir = MapleDataTool.getIntConvert("dir", data, 0);
                int mapid = MapleDataTool.getIntConvert("map", data, 0);
                int FH = MapleDataTool.getIntConvert("fh", data, 0);
                int RX0 = MapleDataTool.getIntConvert("rx0", data, 0);
                int RX1 = MapleDataTool.getIntConvert("rx1", data, 0);
                int CX = MapleDataTool.getIntConvert("cx", data, 0);
                int CY = MapleDataTool.getIntConvert("cy", data, 0);

                Map<Short, Integer> equips = new HashMap<>();
                for (MapleData edata : data.getChildByPath("equips").getChildren()) {
                    short equippos = (short) MapleDataTool.getIntConvert("pos", edata);
                    int equipid = MapleDataTool.getIntConvert("itemid", edata);

                    equips.put(equippos, equipid);
                }

                List<MaplePlayerNPC> dnpcSet = dnpcMaps.get(mapid);
                if(dnpcSet == null) {
                    dnpcSet = new LinkedList<>();
                    dnpcMaps.put(mapid, dnpcSet);
                }

                dnpcSet.add(new MaplePlayerNPC(name, scriptId, face, hair, gender, skin, equips, dir, FH, RX0, RX1, CX, CY, runningDeveloperOid));
                runningDeveloperOid++;
            }
        } else {
            MapleData thisData = npc.getData("9977777.img");
            
            if(thisData != null) {
                byte[] encData = {0x52,0x6F,0x6E,0x61,0x6E};
                String name = new String(encData);
                int face = 20104, hair = 30215, gender = 0, skin = 0, dir = 0, mapid = 777777777;
                int FH = 4, RX0 = -143, RX1 = -243, CX = -193, CY = 117, scriptId = 9977777;

                Map<Short, Integer> equips = new HashMap<>();
                equips.put((short) -1, 1002067);
                equips.put((short) -11, 1402046);
                equips.put((short) -8, 1082140);
                equips.put((short) -6, 1060091);
                equips.put((short) -7, 1072154);
                equips.put((short) -5, 1040103);

                List<MaplePlayerNPC> dnpcSet = dnpcMaps.get(mapid);
                if(dnpcSet == null) {
                    dnpcSet = new LinkedList<>();
                    dnpcMaps.put(mapid, dnpcSet);
                }

                dnpcSet.add(new MaplePlayerNPC(name, scriptId, face, hair, gender, (byte) skin, equips, dir, FH, RX0, RX1, CX, CY, runningDeveloperOid));
                runningDeveloperOid++;
            }
        }
    }
    
    public static List<MaplePlayerNPC> getDeveloperNpcsFromMapid(int mapid) {
        return dnpcMaps.get(mapid);
    }
}
