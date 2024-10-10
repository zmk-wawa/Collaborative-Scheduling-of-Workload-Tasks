import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import kn.uni.voronoitreemap.datastructure.OpenList;
import kn.uni.voronoitreemap.diagram.PowerDiagram;
import kn.uni.voronoitreemap.j2d.Point2D;
import kn.uni.voronoitreemap.j2d.PolygonSimple;
import kn.uni.voronoitreemap.j2d.Site;

import java.nio.charset.Charset;

public class voronoiTessellation {
    public static void main(String[] args) throws Exception {
        PowerDiagram diagram = new PowerDiagram();

        /*
        double[][] pos = {  {110.0, 120.0},   {550.0, 840.0},   {440.0, 610.0},
                            {660.0, 460.0},   {150.0, 880.0},   {900.0, 120.0},
                            {820.0, 890.0},   {300.0, 420.0},   {300.0, 340.0}};

        double[][] target ={{167.0, 167.0},   {500.0, 833.0},   {500.0, 500.0},
                            {833.0, 500.0},   {167.0, 833.0},   {833.0, 167.0},
                            {833.0, 833.0},   {167.0, 500.0},   {500.0, 167.0}};


         */


        double[][] pos = {                     {150.0, 880.0},   {900.0, 120.0},
                             {820.0, 890.0},     {300.0, 340.0}};






        int switchNum = pos.length;
        // double[] weight = {81120, 31960, 66480, 26610, 69970, 28510, 4280, 5220, 12970};
        // double[] weight = new double[switchNum];
        PolygonSimple rootPolygon = new PolygonSimple();

        rootPolygon.add(0.0, 1000.0);
        rootPolygon.add(1000.0, 1000.0);
        rootPolygon.add(1000.0, 0.0);
        rootPolygon.add(0.0, 0.0);


        OpenList sites = new OpenList();

        for (int i = 0; i < switchNum ; i++){
            Site site = new Site(pos[i][0], pos[i][1]);
            site.setWeight(0);
            sites.add(site);
        }
        diagram.setSites(sites);
        OpenList preSites = sites.clone();
        OpenList sortedSites = new OpenList();
        diagram.setClipPoly(rootPolygon);
        diagram.computeDiagram();
        for(int i = 0 ; i < switchNum; i++){
            for(int j = 0; j < switchNum; j++){
                if(preSites.array[i].x == sites.array[j].x && preSites.array[i].y == sites.array[j].y){
                    sortedSites.add( sites.array[j] );
                    break;
                }
            }
        }
        for(int cnt = 0; cnt < 50; cnt++){
            for (int i = 0; i < sites.size; i++) {
                Site site = sortedSites.array[i];
                Point2D centroid = site.getPolygon().getCentroid();
                Site newSite = new Site(centroid.x,centroid.y);
                newSite.setWeight(0);
                sites.set(i,newSite);
                //preSites=sites.clone();
            }

            diagram.setSites(sites);
            preSites = sites.clone();
            sortedSites = new OpenList();
            diagram.setClipPoly(rootPolygon);
            diagram.computeDiagram();
            for(int i = 0 ; i < switchNum; i++){
                for(int j = 0; j < switchNum; j++){
                    if(preSites.array[i].x == sites.array[j].x && preSites.array[i].y == sites.array[j].y){
                        sortedSites.add( sites.array[j] );
                        break;
                    }
                }
            }
        }

        PolygonSimple[] sortedPolygon = new PolygonSimple[switchNum];

        for (int i=0;i<sites.size;i++){
            Site site=sortedSites.array[i];
            PolygonSimple polygon=site.getPolygon();
            sortedPolygon[i] = polygon;
        }


        diagram.showDiagram();




        String src = "D:\\Jupyter notebook\\fgcs 2024\\dataset-telecom\\generate_data_with_label.csv";
        String dst = "D:\\Jupyter notebook\\fgcs 2024\\dataset-telecom\\migration\\centroidal_4point.csv";
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
                    if(i >= 3){
                        raw[15] = String.valueOf(i + 2);
                    }else {
                        raw[15] = String.valueOf(i + 1);
                    }                }
            }
            csvWriter.writeRecord(raw,false);

            System.out.println(cnt++);
        }
        csvWriter.close();






    }
}
