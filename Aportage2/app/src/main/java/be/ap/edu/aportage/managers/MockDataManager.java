package be.ap.edu.aportage.managers;


import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.ap.edu.aportage.models.Campus;
import be.ap.edu.aportage.models.Melding;
import be.ap.edu.aportage.models.Verdiep;

public class MockDataManager {

    private static MockDataManager instance = null;
    private static List<Campus> campussenLijst;
    private static List<Melding> meldingenLijst;

    public static MockDataManager getInstance () {
        if(instance!= null) {
            return instance;
        } else {
            instance = new MockDataManager();
            setMeldingenLijst();
            setCampussenLijst();
            return instance;
        }

    }

    private static void setCampussenLijst() {
        MockDataManager.campussenLijst = new ArrayList<>();
        List<Verdiep> verdiepList = new ArrayList<>();
        verdiepList.add(new Verdiep(-1, new int[]{0,1,2,3,4,5,6,7,8,9,10 }));
        verdiepList.add(new Verdiep(1, new int[]{0,1,2,3,4,5,6,7,8,9,10 }));
        verdiepList.add(new Verdiep(2, new int[]{0,1,2,3,4,5,6,7,8,9,10 }));
        verdiepList.add(new Verdiep(3, new int[]{0,1,2,3,4,5,6,7,8,9,10 }));
        MockDataManager.campussenLijst.add(new Campus("Ellerman", "ELL", verdiepList));
        MockDataManager.campussenLijst.add(new Campus("Noorderplaats", "NOO", verdiepList));
    }

    private static void setMeldingenLijst(){
        MockDataManager.meldingenLijst = new ArrayList<>();
        MockDataManager.meldingenLijst.add(new Melding("MockMelding", "Blablablablabla", new String[]{"ELL","-01","005"}, "behandeling", new Date()));
        MockDataManager.meldingenLijst.add(new Melding("MockMelding2", "Blablablablabla2", new String[]{"NOO","05","013"}, "behandeling", new Date()));
    }

    public static List<Melding> getMeldingenLijst() {

        return MockDataManager.meldingenLijst;
    }

    public static List<Campus> getCampussenLijst() {
        return MockDataManager.campussenLijst;
    }

    public static List<Verdiep> getVerdiepenLijst(String afk) {

        List<Verdiep> list = new ArrayList<>();
        for(int i = 0; i < MockDataManager.campussenLijst.size(); i++){
            if(afk.equals(campussenLijst.get(i).afkorting)){
               list =  campussenLijst.get(i).verdiepingen;
            }
        }

        return list;


    }

    public static int[] getLokalenLijst(String afk, int verdiep){
        List<Verdiep> verdiepenLijst = new ArrayList<>();
        int[] lokalen = null;

        for(int i = 0; i < MockDataManager.campussenLijst.size(); i++){
            if(afk.equals(MockDataManager.campussenLijst.get(i).afkorting)){
                verdiepenLijst =  MockDataManager.campussenLijst.get(i).verdiepingen;
            }
        }

        for (Verdiep v: verdiepenLijst) {
            if(v.verdiepnr == verdiep) {
                Log.d("verdiepen foreach", "verdiepnr: "+verdiep + " gevonden!");
                lokalen = v.lokalen;
            }
        }

        return lokalen;
    }

    public static List<Verdiep> getVerdiepLijst(int campusID) {
        return getCampussenLijst().get(campusID).verdiepingen;
    }


}