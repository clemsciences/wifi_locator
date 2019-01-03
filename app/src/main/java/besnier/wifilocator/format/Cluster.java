package besnier.wifilocator.format;


import java.util.List;

/**
 * Created by clement_besnier on 16/05/2018.
 */

public class Cluster {
    private List<Position> lp;
    private float[] weights;

    Cluster(List<Position> lp, float[] weights)
    {
        this.lp = lp;
        this.weights = weights;
    }
    public Position compute_barycenter()
    {
        int i = 0;
        Position barycenter = new Position(0, 0, 0);
        for(Position p : lp)
        {
            p.multiply_scalar(weights[i]);
            barycenter.add(p);
            i++;
        }
        return barycenter;
    }


}
