
package gov.inl.igcapt.components;

import gov.inl.igcapt.graph.GraphManager;
import java.awt.Color;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import gov.inl.igcapt.view.IGCAPTgui;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import gov.inl.igcapt.graph.SgNode;
import gov.inl.igcapt.graph.SgNodeInterface;

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
    private float startColor;
    private float endColor;
    
    public Heatmap(double gridSize, KernelTypes kernelType, double kernelRadius, float startColor, float endColor) {
        
        this.gridSize = gridSize;
        this.kernelRadius = kernelRadius;
        this.startColor = startColor;
        this.endColor = endColor;
        
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
        
    private void DrawPolygon(JMapViewer map, Coordinate pt1, Coordinate pt2, Coordinate pt3, Coordinate pt4, float intensity) {
        MapPolygonImpl polygon = new MapPolygonImpl(pt1, pt2, pt3, pt4, pt1);
        polygon.setColor(new Color(0, 0, 0, 0));
        if (intensity > 1.0f) {
            intensity = 1.0f;
        }
        
        int rgb;
        float inverseIntensity = 1 - intensity;
        if (startColor > endColor) {
            // running the rainbow backwards
            rgb = Color.HSBtoRGB((startColor * intensity) - (1-endColor * inverseIntensity), intensity, 1.0f) & 0x00FFFFFF;           

        }
        else {
            rgb = Color.HSBtoRGB((startColor * intensity) + (endColor * inverseIntensity), intensity, 1.0f) & 0x00FFFFFF;
        }
        
        int alphaColor = (191 << 24) & 0xFF000000;
        if (intensity < 0.1f){
            alphaColor = 0x00000000;
        }

        int color = rgb | alphaColor;
        var polyColor = new Color(color, true);
        
        polygon.setBackColor(polyColor);
        map.addMapPolygon(polygon);
    }
    
    private int GetNodeTotalData(SgNode node) {
        int returnval = 0;
        
        var component = IGCAPTgui.getComponentByUuid(node.getType());
                    
        if (component != null) {
            // Add up all data elements.
            var fields = component.getFields();

            if (fields != null) {
                for (var field:fields) {
                    returnval += field.getPayload();
                }
            }   
        }
        
        return returnval;
    }
    
    /**
     * Draw the heatmap polygons to the map.
     * @param map
     * The map to which polygons are drawn. The polygons are added to
     * the map's polygon list and drawn in the next update.
     */
    public void Draw(JMapViewer map){
        // Add the polygons to the map.
        // Drawing will get done by the outer class.
        
//        MapPolygonImpl polygon = new MapPolygonImpl(new Coordinate(0.0, 0.0), new Coordinate(10.0, 0.0), new Coordinate(10.0, 10.0), new Coordinate(0.0, 10.0), new Coordinate(0.0, 0.0));
//        polygon.setColor(new Color(0, 0, 0, 0));
//        polygon.setBackColor(new Color(Color.pink.getRed(), Color.pink.getGreen(), Color.pink.getBlue(), 64));
//        
//        map.addMapPolygon(polygon);

        // Construct grid        
        // Find limits based on current objects.
        var igcaptGui = IGCAPTgui.getInstance();
        List<SgNodeInterface> nodes = new ArrayList<>(GraphManager.getInstance().getGraph().getVertices());
        double maxLat=0.0, maxLon=0.0;
        double minLat=0.0, minLon=0.0;
        boolean first = true;
        
        // We want the size of the heatmap for one piece of equipment to remain constant when we zoom.
        // Each higher zoom level is twice the size of the previous.
        double zoomLevel = map.getZoom();
        zoomLevel = pow(2.0, zoomLevel);
        double scaledGridSize = gridSize / zoomLevel;
        double scaledKernelRadius = kernelRadius / zoomLevel;
        
        for (SgNodeInterface node : nodes) {
            
            double nodeLat = node.getLat();
            double nodeLon = node.getLongit();
            
            // Skip the point if it is outside our viewable area.
            if (map.getMapPosition(nodeLat, nodeLon) == null) {
                continue;
            }
            
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
        if (scaledKernelRadius < scaledGridSize) {
            // Expand by gridSize
            minLat -= scaledGridSize;
            maxLat += scaledGridSize;
            
            minLon -= scaledGridSize;
            maxLon += scaledGridSize;
        }
        else {
            // Expand by ceil(kernelRadius/gridSize)*gridSize
            double expand = ceil(scaledKernelRadius/scaledGridSize) * scaledGridSize;
            minLat -= expand;
            maxLat += expand;
            
            minLon -= expand;
            maxLon += expand;
        }
        
        // Make the extents an even number of gridSize elements.
        int numLatElems = (int)(ceil((maxLat - minLat)/scaledGridSize));
        double latIncr = (numLatElems*scaledGridSize - (maxLat - minLat))/2.0;
        minLat -= latIncr;
        
        int numLonElems = (int)(ceil((maxLon - minLon)/scaledGridSize));
        double lonIncr = (numLonElems*scaledGridSize - (maxLon - minLon))/2.0;
        minLon -= lonIncr;
        
        double halfGridSize = scaledGridSize/2.0;
        float cellIntensity = 0.0f;
        float maxCellIntensity = 0.0f;
        float cellIntensities[][] = new float[numLonElems][numLatElems];
        
        for (int i = 0; i < numLonElems; i++) {
            for (int j = 0; j < numLatElems; j++) {
                
                for (SgNodeInterface node : nodes) {
            
                    double nodeLat = node.getLat();
                    double nodeLon = node.getLongit();

                    // Skip the point if it is outside our viewable area.
                    if (map.getMapPosition(nodeLat, nodeLon) == null) {
                        continue;
                    }
                    
                    int totalPayload = 0;
                    if (node instanceof SgNode sgNode){
                        totalPayload = GetNodeTotalData(sgNode);                        
                    }

                    Coordinate cellCenter = new Coordinate(minLat + j*scaledGridSize + halfGridSize, minLon + i*scaledGridSize + halfGridSize);
                    Coordinate nodeCoord = new Coordinate(nodeLat, nodeLon);
                    
                    double dist = Distance(cellCenter, nodeCoord);
                    // Need to multiply by magnitude of data value we are mapping.
                    cellIntensity += kernelFunction.evaluate(dist/scaledKernelRadius) * totalPayload;
                }
                
                if (cellIntensity > maxCellIntensity) {
                    maxCellIntensity = cellIntensity;
                }
                
                cellIntensities[i][j] = cellIntensity;
                cellIntensity = 0.0f;
            }
        }
        
        for (int i = 0; i < numLonElems; i++) {
            for (int j = 0; j < numLatElems; j++) {
                
                // Plot grid with cells colored by Intensity.
                // Need the four corners.
                Coordinate pt1 = new Coordinate(minLat + j*scaledGridSize, minLon + i*scaledGridSize);
                Coordinate pt2 = new Coordinate(minLat + j*scaledGridSize, minLon + (i+1)*scaledGridSize);
                Coordinate pt3 = new Coordinate(minLat + (j+1)*scaledGridSize, minLon + (i+1)*scaledGridSize);
                Coordinate pt4 = new Coordinate(minLat + (j+1)*scaledGridSize, minLon + i*scaledGridSize);

                if (maxCellIntensity > 0) {
                    DrawPolygon(map, pt1, pt2, pt3, pt4, cellIntensities[i][j]/maxCellIntensity);
                }
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
