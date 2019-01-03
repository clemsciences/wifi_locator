package besnier.wifilocator.format;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by clement_besnier on 16/05/2018.
 */

public class Position {
    float x, y, z;

    public Position(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    private Position(float x, float y)
    {
        new Position(x, y, 0);
    }

    public Position()
    {
        new Position(0, 0, 0);
    }

    public void multiply_scalar(float scalar)
    {
        x = scalar*x;
        y = scalar*y;
        z = scalar*z;
    }
    public void add(Position other_pos)
    {
        x = x + other_pos.x;
        y = y + other_pos.y;
        z = z + other_pos.z;
    }
    public void fromJSON(JSONObject json_object) throws JSONException {
        x = (float) json_object.getDouble("x");
        y = (float) json_object.getDouble("y");
        z = (float) json_object.getDouble("z");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json_object = new JSONObject();
        json_object.put("x", x);
        json_object.put("y", y);
        json_object.put("z", z);
        return json_object;
    }

    public String toString()
    {
        return "("+x+", "+y+", "+z+")";
    }

}
