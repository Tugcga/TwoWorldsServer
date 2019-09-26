/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game.Process;

import Game.DataClasses.GlobalGameData;
import Game.DataClasses.LocationClass;
import Game.DataClasses.NetworkKeys;
import Game.DataClasses.TowerClass;
import Game.DataClasses.TowerParametersClass;
import Game.DataClasses.Vector2;
import OpenWorldRoom.Logger;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.mmo.IMMOItemVariable;
import com.smartfoxserver.v2.mmo.MMOItem;
import com.smartfoxserver.v2.mmo.MMOItemVariable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class TowersManagement 
{
    public static void CreateTowers(String towersDataFileName) throws IOException
    {
        String dataText = FileUtils.readFileToString(new File(towersDataFileName));
        if(!"".equals(dataText))
        {
            String[] parts = dataText.split("\\|");
            for (String part : parts) 
            {
                if(!"".equals(part))
                {
                    String[] idAndCoords = part.split(",");
                    if(idAndCoords.length == 4 && !"".equals(idAndCoords[0]) && !"".equals(idAndCoords[1])&& !"".equals(idAndCoords[2]))
                    {
                        Vector2 tPosition = new Vector2(idAndCoords[1], idAndCoords[2]);
                        int tType = Integer.parseInt(idAndCoords[0]);
                        String towerName = idAndCoords[3];
                        CreateTower(tType, tPosition, towerName);
                    }
                }
            }
        }
    }
    
    static void CreateTower(int type, Vector2 position, String name)
    {
        TowerParametersClass tParams = GlobalGameData.serverConfig.GetTowerParameters(type);
        LocationClass towerLocation = new LocationClass();
        towerLocation.SetLocation(position.GetX(), position.GetY());
        MMOItem newTowerItem = new MMOItem();
        TowerClass newTower = new TowerClass(newTowerItem, type, name,
                                            tParams.GetRadius(), tParams.GetLife(), 
                                            tParams.GetMonstersCount(), tParams.GetMonstersMinRadius(), 
                                            tParams.GetMonstersMaxRadius(), tParams.GetVisibleRadius(),
                                            tParams.GetAtackCoolDawn(), tParams.GetBulletType(),
                                            tParams.GetTargetAccuracyRadius(), tParams.GetResurectTime(),
                                            tParams.GetIsSpawnInDead(), tParams.GetIsAgreMonstersToAtacker(),
                                            tParams.GetBulletsPerShot());
        newTower.SetLocation(towerLocation);
        newTowerItem.setVariables(GetTowerVariables(newTower, true));
        
        GlobalGameData.towers.put(newTower.GetId(), newTower);
        NetworkDataProcess.SetTowerState(newTower, false, true);
    }
    
    public static List<IMMOItemVariable> GetTowerVariables(TowerClass tower, boolean isFirstCall) 
    {
        List<IMMOItemVariable> vars = new ArrayList<IMMOItemVariable>();
        if(isFirstCall)
        {
            vars.add(new MMOItemVariable(NetworkKeys.key_id, tower.GetId()));
            vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_kind, NetworkKeys.towerKind));
            vars.add(new MMOItemVariable(NetworkKeys.key_serverItem_type, tower.GetTowerType()));
            vars.add(new MMOItemVariable(NetworkKeys.key_name, tower.GetName()));
            vars.add(new MMOItemVariable(NetworkKeys.key_person_maxLife, tower.GetMaxLife()));
            vars.add(new MMOItemVariable(NetworkKeys.key_person_radius, tower.GetRadius()));
            vars.add(new MMOItemVariable(NetworkKeys.key_location_position_x, tower.GetLocation().GetPosition().GetX()));
            vars.add(new MMOItemVariable(NetworkKeys.key_location_position_y, tower.GetLocation().GetPosition().GetY()));
            
            //special for tower
            vars.add(new MMOItemVariable(NetworkKeys.key_tower_monsterMinRadius, tower.GetMonsterSpawner().GetMinRadius()));
            vars.add(new MMOItemVariable(NetworkKeys.key_tower_monsterMaxRadius, tower.GetMonsterSpawner().GetMaxRadius()));
            vars.add(new MMOItemVariable(NetworkKeys.key_tower_atackRadius, tower.GetAtacker().GetAtackRadius()));
        }
        vars.add(new MMOItemVariable(NetworkKeys.key_person_life, tower.GetLife()));
        
        return vars;
    }
}
