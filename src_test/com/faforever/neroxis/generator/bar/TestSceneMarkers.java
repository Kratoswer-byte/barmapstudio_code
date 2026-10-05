package com.faforever.neroxis.generator.bar;

import com.faforever.neroxis.generator.util.serial.TerrainStyle;
import com.faforever.neroxis.map.Symmetry;
import com.faforever.neroxis.util.vector.Vector2;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestSceneMarkers {
    public static void main(String[] a) throws Exception {
        GroundGenerator.GroundResult res = GroundGenerator.generateGroundOnly(
            256, 256, TerrainStyle.VALLEY, "FRITHEN", Symmetry.POINT2, 2, 2, 777L);
        System.out.println("mex=" + res.map().getMexes().size() + " spawns=" + res.map().getSpawns().size()
            + " props=" + res.map().getProps().size() + " hydro=" + res.map().getHydros().size());
        MapScene3DCanvas scene = new MapScene3DCanvas(() -> Path.of("C:\\Program Files\\Beyond-All-Reason\\data"));
        scene.setSize(960, 600);
        List<PlacedCustomEntity> ents = new ArrayList<>();
        ents.add(new PlacedCustomEntity(ObjectEntity.createDefaultWindmill(), new Vector2(100, 100), 0.5f));
        ents.add(new PlacedCustomEntity(ObjectEntity.createDefaultPineTree(), new Vector2(150, 120), 0f));
        scene.setMapAndGround(res.map(), res.preview(), ents);
        scene.setSelected(ents.get(0));
        BufferedImage img = new BufferedImage(960, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        scene.paint(g);
        g.dispose();
        javax.imageio.ImageIO.write(img, "png", new java.io.File("test_scene3d.png"));
        System.out.println("written test_scene3d.png");
        System.exit(0);
    }
}
