import java.util.Random;

import javax.vecmath.Color3f;

public class FractalTerrain {
    private float[][] terrain;
    private float roughness, min, max;
    private int divisions;
    private int height;
    private Random rng;

    public FractalTerrain(int lod, float roughness, int height) {
        this.height = height;
        this.roughness = roughness;
        this.divisions = 1 << lod;
        terrain = new float[divisions + 1][divisions + 1];
        rng = new Random();
        terrain[0][0] = rnd();
        terrain[0][divisions] = rnd();
        terrain[divisions][divisions] = rnd();
        terrain[divisions][0] = rnd();
        double rough = roughness;
        for (int i = 0; i < lod; ++i) {
            int q = 1 << i, r = 1 << (lod - i), s = r >> 1;
            for (int j = 0; j < divisions; j += r)
                for (int k = 0; k < divisions; k += r)
                    diamond(j, k, r, rough);
            if (s > 0)
                for (int j = 0; j <= divisions; j += s)
                    for (int k = (j + s) % r; k <= divisions; k += r)
                        square(j - s, k - s, r, rough);
            rough *= roughness;
        }
        min = max = terrain[0][0];
        for (int i = 0; i <= divisions; ++i)
            for (int j = 0; j <= divisions; ++j)
                if (terrain[i][j] < min)
                    min = terrain[i][j];
                else if (terrain[i][j] > max)
                    max = terrain[i][j];
    }

    private void diamond(int x, int y, int side, double scale) {
        if (side > 1) {
            int half = side / 2;
            double avg = (terrain[x][y] + terrain[x + side][y]
                    + terrain[x + side][y + side] + terrain[x][y + side]) * 0.25;
            terrain[x + half][y + half] = (float) (avg + rnd() * scale);
        }
    }

    private void square(int x, int y, int side, double scale) {
        int half = side / 2;
        double avg = 0.0, sum = 0.0;
        if (x >= 0) {
            avg += terrain[x][y + half];
            sum += 1.0;
        }
        if (y >= 0) {
            avg += terrain[x + half][y];
            sum += 1.0;
        }
        if (x + side <= divisions) {
            avg += terrain[x + side][y + half];
            sum += 1.0;
        }
        if (y + side <= divisions) {
            avg += terrain[x + half][y + side];
            sum += 1.0;
        }
        terrain[x + half][y + half] = (float) (avg / sum + rnd() * scale);
    }

    private float rnd() {
        return rng.nextFloat()*height;
    }

    public float getAltitude(int i, int j) {
        float alt = terrain[i][j];
        return (alt - min) / (max - min);
    }

    private Color3f blue = new Color3f(0.f, 0.f, 1.f);
    private Color3f green = new Color3f(0.f, 1.f, 0.f);
    private Color3f white = new Color3f(1.f, 1.f, 1.f);

    public Color3f getColor(int i, int j) {
        float a = getAltitude(i, j)/height;
        if (a < .5) {
            blue.scale((float)((a - 0.0) / 0.5));
            green.sub(blue);
            blue.add(green);
            return blue;
        }
        else {
            green.scale((float) ((a - 0.5) / 0.5));
            white.sub(green);
            green.add(white);
            return green;
        }
    }
}
