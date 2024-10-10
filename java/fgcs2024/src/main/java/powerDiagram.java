import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class powerDiagram {
    public static void main(String[] args) throws Exception {
        PowerDiagram diagram = new PowerDiagram();

/*
        double[][] pos = {  {110.0, 120.0},   {550.0, 840.0},   {440.0, 610.0},
                            {660.0, 460.0},   {150.0, 880.0},   {900.0, 120.0},
                            {820.0, 890.0},   {300.0, 420.0},   {300.0, 340.0}};

        double[] weight = {81120, 31960, 66480, 26610, 69970, 28510, 4280, 5220, 12970};
        */






        double[][] pos = {  {110.0, 120.0},   {550.0, 840.0},   {440.0, 610.0},
                            {660.0, 460.0},   {150.0, 880.0},   {900.0, 120.0},
                            {820.0, 890.0},   {300.0, 420.0},   {300.0, 340.0}};

        double[] weight = {81120, 31960, 66480, 26610, 69970, 28510, 4280, 5220, 12970};


        int switchNum = pos.length;

        PolygonSimple rootPolygon = new PolygonSimple();

        rootPolygon.add(0.0, 1000.0);
        rootPolygon.add(1000.0, 1000.0);
        rootPolygon.add(1000.0, 0.0);
        rootPolygon.add(0.0, 0.0);


        OpenList sites = new OpenList();

        for (int i = 0; i < switchNum ; i++){
            Site site = new Site(pos[i][0], pos[i][1]);
            site.setWeight(weight[i]);
            sites.add(site);
        }
        diagram.setSites(sites);
        diagram.setClipPoly(rootPolygon);

        diagram.computeDiagram();
        PolygonSimple[] polygonArray = new PolygonSimple[switchNum];

        for (int i=0;i<sites.size;i++){
            Site site=sites.array[i];
            PolygonSimple polygon=site.getPolygon();
            polygonArray[i] = polygon;
        }

        PolygonSimple[] sortedPolygon = new PolygonSimple[switchNum];

        for(int i = 0; i < switchNum; i++){
            for(int j = 0; j < switchNum; j++){
                if(pos[i][0] == sites.get(j).x && pos[i][1] == sites.get(j).y){
                    sortedPolygon[i] = polygonArray[j];
                }
            }
        }


        diagram.showDiagram();

        for(int i = 0; i < switchNum; i++){
            System.out.println("Site:" + sites.get(i).x + ",     "+ sites.get(i).y);
            System.out.println("Edge:");
            for(int j = 0; j < polygonArray[i].length; j++){
                System.out.print(polygonArray[i].getXPoints()[j]);
                System.out.print(",       ");
                System.out.print(polygonArray[i].getYPoints()[j]);
                System.out.println();

            }
            System.out.println("==========================");

        }


         String src = "D:\\Jupyter notebook\\fgcs 2024\\dataset-telecom\\generate_data_with_label.csv";
         String dst = "D:\\Jupyter notebook\\fgcs 2024\\dataset-telecom\\migration\\power_5point.csv";

        CsvReader csvReader = new CsvReader(src);
        CsvWriter csvWriter = new CsvWriter(dst, ',', Charset.forName("UTF-8"));
        csvReader.readHeaders();
        csvWriter.writeRecord(csvReader.getHeaders());
        int cnt = 1;

        while (csvReader.readRecord()) {
            String[] raw = csvReader.getValues();

            // 读这行的某一列
            double x = Double.parseDouble(csvReader.get("x"));
            double y = Double.parseDouble(csvReader.get("y"));
            Point2D rawPoint = new Point2D(x, y);
            for (int i = 0; i < switchNum; i++) {
                if (sortedPolygon[i].contains(rawPoint)) {
                    if(i >= 1){
                        raw[15] = String.valueOf(i + 2);
                    }else {
                        raw[15] = String.valueOf(i + 1);
                    }

                }
            }
            csvWriter.writeRecord(raw,false);

            System.out.println(cnt++);
        }
        csvWriter.close();
    }
}
