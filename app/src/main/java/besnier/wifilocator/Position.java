package besnier.wifilocator;

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

}
