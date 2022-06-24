
package gov.inl.igcapt.components;

import java.awt.Color;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.IGCAPTgui;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.SgNodeInterface;

/**
 *
 * @author FRAZJD
 */
public class Heatmap {
    
    public enum KernelTypes {
        Uniform,        // K(u) = 1/2; support |u| <= 1
        Triangular,     // K(u) = (1 - |u|); support |u| <= 1
        Epanechnikov,   // K(u) = (3/4)(1 - u^2); support |u| <= 1
        Quartic,        // K(u) = (15/16)(1 - u^2)^2; support |u| <= 1
        Triweight,      // K(u) = (35/32)(1 - u^2)^3; support |u| <= 1
        Tricube,        // K(u) = (70/81)(1 - |u|^3)^3; support |u| <= 1
        Gaussian,       // K(u) = [1/(sqrt(2*pi))]e^((-1/2)u^2 
        Cosine,         // K(u) = (pi/4)cos((pi/2)u)
        Logistic,       // K(u) = 1/(e^u + 2 + e^-u)
        SigmoidFunction,// K(u) = (2/pi)(1/(e^u + e^-u))
        Silverman       // K(u) = (1/2)e^(-|u|/sqrt(2)) sin((|u|/sqrt(2) + (pi/4))
    }

    private KernelFunction kernelFunction;
    private double gridSize;
    private double kernelRadius;
    
    public Heatmap(double gridSize, KernelTypes kernelType, double kernelRadius) {
        
        this.gridSize = gridSize;
        this.kernelRadius = kernelRadius;
        
        switch(kernelType){
            case Uniform -> {
                kernelFunction = new UniformKernel();
                break;
            }
            
            case Triangular -> {
                kernelFunction = new TriangularKernel();
                break;
            }
            case Epanechnikov -> {
                kernelFunction = new EpanechnikovKernel();
                break;
            }
            case Quartic -> {
                kernelFunction = new QuarticKernel();
                break;
            }
            case Triweight -> {
                kernelFunction = new TriweightKernel();
                break;
            }
            case Tricube -> {
                kernelFunction = new TricubeKernel();
                break;
            }
            case Gaussian  -> {
                kernelFunction = new GaussianKernel();
                break;
            }
            case Cosine -> {
                kernelFunction = new CosineKernel();
                break;
            }
            case Logistic -> {
                kernelFunction = new LogisticKernel();
                break;
            }
            case SigmoidFunction -> {
                kernelFunction = new SigmoidFunctionKernel();
                break;
            }
            case Silverman -> {
                kernelFunction = new SilvermanKernel();
                break;
            }
                
            default -> {
                kernelFunction = null;
            }
        }
    }
    
    // Only accurate on a very localized scale. Should use the more intensive Haversine formula.
    private double Distance(Coordinate pt1, Coordinate pt2) {
        
        double returnval;
        
        double latDiff = pt2.getLat() - pt1.getLat();
        double lonDiff = pt2.getLon() - pt1.getLon();
        
        returnval = sqrt(latDiff*latDiff + lonDiff*lonDiff);
        
        return returnval;
    }
        
    private void DrawPolygon(JMapViewer map, Coordinate pt1, Coordinate pt2, Coordinate pt3, Coordinate pt4, double intensity) {
        MapPolygonImpl polygon = new MapPolygonImpl(pt1, pt2, pt3, pt4, pt1);
        polygon.setColor(new Color(0, 0, 0, 0));
        if (intensity > 1.0) intensity = 1.0;
        
        int alphaColor = 0x80000000;
        int hexColor = 0x00ff00ff; // No alpha
        hexColor = (int)(intensity * hexColor);
        var polyColor = new Color(hexColor | alphaColor, true);
        
        polygon.setBackColor(new Color((int)((1.0 - intensity)*255), 255, 255, 64));
        
        map.addMapPolygon(polygon);
    }
    
    public void Draw(JMapViewer map){
        // Add the polygons to the map.
        // Drawing will get done by the outer class.
        
//        MapPolygonImpl polygon = new MapPolygonImpl(new Coordinate(0.0, 0.0), new Coordinate(10.0, 0.0), new Coordinate(10.0, 10.0), new Coordinate(0.0, 10.0), new Coordinate(0.0, 0.0));
//        polygon.setColor(new Color(0, 0, 0, 0));
//        polygon.setBackColor(new Color(Color.pink.getRed(), Color.pink.getGreen(), Color.pink.getBlue(), 64));
//        
//        MapPolygonImpl polygon2 = new MapPolygonImpl(new Coordinate(0.0, -10.0), new Coordinate(10.0, -10.0), new Coordinate(10.0, 0.0), new Coordinate(0.0, 0.0), new Coordinate(0.0, -10.0));
//        polygon2.setColor(new Color(0, 0, 0, 0));
//        polygon2.setBackColor(new Color(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), 64));
//        
//        map.addMapPolygon(polygon);
//        map.addMapPolygon(polygon2);

        // Construct grid
        
        // Find limits based on current objects.
        var igcaptGui = IGCAPTgui.getInstance();
        List<SgNodeInterface> nodes = new ArrayList<>(igcaptGui.getGraph().getVertices());
        double maxLat=0.0, maxLon=0.0;
        double minLat=0.0, minLon=0.0;
        boolean first = true;
        
        for (SgNodeInterface node : nodes) {
            
            double nodeLat = node.getLat();
            double nodeLon = node.getLongit();
            
            if (first) {
                minLat = nodeLat;
                minLon = nodeLon;                
                maxLat = nodeLat;
                maxLon = nodeLon;    
                first = false;
            }
            else {
                if (nodeLat < minLat) {
                    minLat = nodeLat;
                }
                
                if (nodeLon < minLon) {
                    minLon = nodeLon;
                }
                
                if (nodeLat > maxLat) {
                    maxLat = nodeLat;
                }
                
                if (nodeLon > maxLon) {
                    maxLon = nodeLon;
                }
            }
        }
        
        // We want the grid to extend outside the min and max.
        if (kernelRadius < gridSize) {
            // Expand by gridSize
            minLat -= gridSize;
            maxLat += gridSize;
            
            minLon -= gridSize;
            maxLon += gridSize;
        }
        else {
            // Expand by ceil(kernelRadius/gridSize)*gridSize
            double expand = ceil(kernelRadius/gridSize) * gridSize;
            minLat -= expand;
            maxLat += expand;
            
            minLon -= expand;
            maxLon += expand;
        }
        
        // Make the extents an even number of gridSize elements.
        int numLatElems = (int)(ceil((maxLat - minLat)/gridSize));
        double latIncr = (numLatElems*gridSize - (maxLat - minLat))/2.0;
        minLat -= latIncr;
        maxLat += latIncr;
        
        int numLonElems = (int)(ceil((maxLon - minLon)/gridSize));
        double lonIncr = (numLonElems*gridSize - (maxLon - minLon))/2.0;
        minLon -= lonIncr;
        maxLon += lonIncr;
        
        double halfGridSize = gridSize/2.0;
        double cellIntensity = 0.0;
        
        for (int i = 0; i < numLonElems; i++) {
            for (int j = 0; j < numLatElems; j++) {
                
                for (SgNodeInterface node : nodes) {
            
                    double nodeLat = node.getLat();
                    double nodeLon = node.getLongit();

                    Coordinate cellCenter = new Coordinate(minLat + j*gridSize + halfGridSize, minLon + i*gridSize + halfGridSize);
                    Coordinate nodeCoord = new Coordinate(nodeLat, nodeLon);
                    
                    double dist = Distance(cellCenter, nodeCoord);
                    // Need to multiply by magnitude of data value we are mapping. For now
                    // we will just plot assuming a value of 1.0.
                    cellIntensity += kernelFunction.evaluate(dist/kernelRadius);
                }
                
                // Plot grid with cells colored by Intensity.
                // Need the four corners.
                Coordinate pt1 = new Coordinate(minLat + j*gridSize, minLon + i*gridSize);
                Coordinate pt2 = new Coordinate(minLat + j*gridSize, minLon + (i+1)*gridSize);
                Coordinate pt3 = new Coordinate(minLat + (j+1)*gridSize, minLon + (i+1)*gridSize);
                Coordinate pt4 = new Coordinate(minLat + (j+1)*gridSize, minLon + i*gridSize);
            
               DrawPolygon(map, pt1, pt2, pt3, pt4, cellIntensity);
               cellIntensity = 0.0;
            }
        }
    }
    
    public interface KernelFunction {
        public double evaluate(double u);
    }
    
    private class UniformKernel implements KernelFunction {
        
        public double evaluate(double u) {
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 0.5;
            }

            return returnval;
        }
    }  
    private class TriangularKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    } 
    private class EpanechnikovKernel implements KernelFunction {
        
         public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    }    
    private class QuarticKernel implements KernelFunction {

        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    }    
    private class TriweightKernel implements KernelFunction{
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    }
    private class TricubeKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }        
    }
    private class GaussianKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    }
    private class CosineKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }        
    }    
    private class LogisticKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    }    
    private class SigmoidFunctionKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }
    }    
    private class SilvermanKernel implements KernelFunction {
        
        public double evaluate(double u){
            double returnval = 0.0;

            if (abs(u) <= 1){
                returnval = 1 - abs(u);
            }

            return returnval;
        }        
    }
}
