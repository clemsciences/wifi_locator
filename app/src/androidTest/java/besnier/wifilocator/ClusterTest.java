package besnier.wifilocator;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by clement_besnier on 16/05/2018.
 */
public class ClusterTest extends TestCase {
    public void testCompute_barycenter() throws Exception {
        Position p1 = new Position(-1, 2, 6);
        Position p2 = new Position(3, -1, 2);
        ArrayList<Position> lp = new ArrayList<>();
        lp.add(p1);
        lp.add(p2);

        float[] weights = {1, 2};

        Cluster cluster = new Cluster(lp, weights);
        Position res = cluster.compute_barycenter();

        Position pos_manuelle = new Position(5, 0, 10);
        assertEquals(res.x, pos_manuelle.x);
        assertEquals(res.y, pos_manuelle.y);
        assertEquals(res.z, pos_manuelle.z);
    }
    public void main()
    {
        try {
            testCompute_barycenter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}