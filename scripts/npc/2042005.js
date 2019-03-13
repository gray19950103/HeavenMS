var map = 980030000;
var minLvl = 30;
var maxLvl = 255;
var minAmt = 0;
var maxAmt = 6;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            if (cm.getParty() == null) {
                status = 10;
                cm.sendOk("#e� necess�rio criar um grupo antes de come�ar o Festival de Monstros!#k");
            } else if (!cm.isLeader()) {
                status = 10;
                cm.sendOk("Se voc� quer come�ar o Festival, avise o #bl�der do grupo#k para falar comigo.");
            } else {
                var party = cm.getParty().getMembers();
                var inMap = cm.partyMembersInMap();
                var lvlOk = 0;
                var isInMap = 0;
                for (var i = 0; i < party.size(); i++) {
                    if (party.get(i).getLevel() >= minLvl && party.get(i).getLevel() <= maxLvl) {
                        lvlOk++;
                    }
                    if (party.get(i).getPlayer().getMapId()!= 980030000) {
                        //isInMap = false;
                        isInMap++
                    }
                }

                if (party >= 1) {
                    status = 10;
                    cm.sendOk("Voc� n�o tem n�mero suficiente de pessoas em seu grupo. Voc� precisa de um grupo com #b" + minAmt + "#k - #r" + maxAmt + "#k membros e eles devem estar no mapa com voc�.");
                } else if (lvlOk != inMap) {
                    status = 10;
                    cm.sendOk("Certifique se todos em seu grupo est�o dentre os n�veis corretos (" + minLvl + "~" + maxLvl + ")!");
                } else if (isInMap > 0) {
                    status = 10;
                    cm.sendOk("Existe algu�m do grupo que n�o esta no mapa!");
                } else {
                    cm.sendCPQMapLists2();
                }
            }
        } else if (status == 1) {
            if (cm.fieldTaken2(selection)) {
                if (cm.fieldLobbied2(selection)) {
                    cm.challengeParty2(selection);
                    cm.dispose();
                } else {
                    cm.sendOk("A sala esta cheia.");
                    cm.dispose();
                }
            } else {
                var party = cm.getParty().getMembers();
                if ((selection === 0 || selection === 1 ) && party.size() < 2) {
                    cm.sendOk("Voc� precisa de no m�nimo 2 player para entrar na competi��o.");
                } else if ((selection === 2 ) && party.size() < 3) {
                    cm.sendOk("Voc� precisa de no m�nimo 3 player para entrar na competi��o.");
                } else {
                    cm.cpqLobby2(selection);
                }
                cm.dispose();
            }
        } else if (status == 11) {
            cm.dispose();
        }
    }
}