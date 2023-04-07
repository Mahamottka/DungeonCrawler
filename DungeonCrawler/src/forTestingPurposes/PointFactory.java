package forTestingPurposes;

import map.Convertion;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
    Algorithm for a map generatar
    It should return the coordinates that will be sent to a class that´s generating quads
 */
public class PointFactory {
    private final Convertion data = new Convertion();

    private final List<Point3D> firstPoint = new ArrayList<>();
    private final List<Point3D> secondPoint = new ArrayList<>();

    private final List<Point3D> thirdPoint = new ArrayList<>();
    private final List<Point3D> fourthPoint = new ArrayList<>();

    public PointFactory() {
        getFirstPoint();
    }

    /**
     * Method used for creating the beginning point of a quad
     * @return points, where should be a quad
     */
    public List<Point3D> getFirstPoint(){
        for (int n = 0; n < data.getIdList().size(); n++) {
            if (data.getIdList().get(n) == 1){
                continue;
            } else if (data.getIdList().get(n) == 2){ //TODO: doorway
                continue;
            } else if (data.getIdList().get(n) == 3) { //walls // je to 30x30
                for (int i = 0; i < 300; i += 10) {
                    for (int j = 0; j < 300; j += 10) {
                        Point3D point = new Point3D(j,i,0);
                        firstPoint.add(point);
                    }
                }
            }
        }
        return firstPoint;
    }

    public List<Point3D> getSecPt(){
        for (int i = 0; i < firstPoint.size(); i++) {
            for (int j = 0; j < firstPoint.size(); j++) {
                Point3D point = new Point3D(firstPoint.get(i).getX(),firstPoint.get(j).getY() + 10,0);
                secondPoint.add(point);
            }
        }
        return secondPoint;
    }


    public List<Point3D> getThrdPt(){
        for (int i = 0; i < firstPoint.size(); i++) {
            for (int j = 0; j < firstPoint.size(); j++) {
                Point3D point = new Point3D(firstPoint.get(j).getX() + 10,firstPoint.get(i).getY() + 10,0);
                thirdPoint.add(point);
            }
        }
        return thirdPoint;
    }

    public List<Point3D> getFrthPt(){
        for (int i = 0; i < firstPoint.size(); i++) {
            for (int j = 0; j < firstPoint.size(); j++) {
                Point3D point = new Point3D(firstPoint.get(j).getX() + 10 ,firstPoint.get(i).getY(),0);
                fourthPoint.add(point);
            }
        }
        return fourthPoint;
    }
    public List<Point3D> getSecondPoint() {
        return secondPoint;
    }

    public List<Point3D> getThirdPoint() {
        return thirdPoint;
    }

    public List<Point3D> getFourthPoint() {
        return fourthPoint;
    }

    public int zkouska(){
        return firstPoint.size();
    }
}
