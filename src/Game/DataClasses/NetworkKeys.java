package Game.DataClasses;

public class NetworkKeys 
{
    public static final String key_location_position_x = "x";
    public static final String key_location_position_y = "y";
    public static final String key_speed = "sp";
    public static final String key_name = "n";
    public static final String key_id = "i";
    public static final String key_state = "st";
    //public static final String key_mmoItem_type = "mt";
    //public static final String key_bullet_type = "bt";
    public static final String key_serverItem_kind = "sik";//used for monsters, bullets and other server-side entities
    public static final String key_serverItem_type= "sit";//type inside each kind
    public static final String key_targetLocation_x = "tlx";
    public static final String key_targetLocation_y = "tly";
    public static final String key_targetEnemy_type = "tet";
    public static final String key_targetEnemy_id = "tei";
    //public static final String key_client_directionIndex = "di";
    public static final String key_client_moveAngle = "ma";//move direction, from ox-axis, conterclockwise direction
    public static final String key_client_isMove = "im";
    public static final String key_client_modelIndex = "mi";
    public static final String key_client_angle = "a";
    public static final String key_client_fireCoolDawn = "cd";
    public static final String key_person_life = "l";
    public static final String key_person_maxLife = "ml";
    public static final String key_person_radius = "r";
    public static final String key_monster_damage = "md";
    public static final String key_monster_damageRadius = "mdr";
    
    public static final String key_bullet_hostType = "bht";
    public static final String key_bullet_hostId = "bhi";
    
    public static final String key_tower_monsterMinRadius = "tmmi";
    public static final String key_tower_monsterMaxRadius = "tmma";
    public static final String key_tower_atackRadius = "tar";
    
    //kinds of the erver-side objects
    public static final int monsterKind = 0;
    public static final int bulletKind = 1;
    public static final int towerKind = 2;
    public static final int collectKind = 3;
    
}
