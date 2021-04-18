package Game.DataClasses;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.mmo.MMOItem;

public class CollectableHealClass extends CollectableClass
{
    public int healValue;
    public int overhealValue;
    
    public CollectableHealClass(MMOItem collLink, int cType, float radius, Vector2 pos, ISFSObject attributes)
    {
        super(collLink, cType, radius, pos);
        
        healValue = attributes.getInt("heal");
        overhealValue = attributes.getInt("overheal");
    }
    
    @Override
    public void Apply(PersonClass person)
    {
        if(person.GetLife() == person.GetMaxLife())
        {
            person.IncreaseMaxLife(overhealValue, true);
        }
        else
        {
            person.IncreaseLife(healValue);
        }
    }
}
