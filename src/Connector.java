import java.awt.geom.Line2D;

public class Connector {
    private ConceptView sourceConceptView;
    private ConceptView prerequisiteConceptView;

    private Line2D.Double connectingLine;

    public ConceptView getSourceConceptView() {
        return sourceConceptView;
    }

    public ConceptView getPrerequisiteConceptView() {
        return prerequisiteConceptView;
    }

    public Connector(ConceptView sourceConceptView, ConceptView prerequisiteConceptView) {
        this.sourceConceptView = sourceConceptView;
        this.prerequisiteConceptView = prerequisiteConceptView;
        connectingLine = new Line2D.Double();
    }
    public Line2D.Double getConnectingLine() {
        connectingLine.setLine(sourceConceptView.getButtonCenterTopPosition(), prerequisiteConceptView.getCenterPoint());
        return connectingLine;
    }
    public boolean checkIfOnLine(double x, double y) {
        double x1Difference = Math.abs(x - connectingLine.x1);
        double y1Difference = Math.abs(y - connectingLine.y1);
        double x2Difference = Math.abs(x - connectingLine.x2);
        double y2Difference = Math.abs(y - connectingLine.y2);
        double rise = connectingLine.y2 - connectingLine.y1;
        double run = connectingLine.x2 - connectingLine.x1;
        double detectionDistance = 10;
        if (run == 0) {
            return getDistance(x, y, connectingLine.x1, y) <= detectionDistance;
        }
        if (x1Difference + x2Difference == Math.abs(run) || y1Difference + y2Difference == Math.abs(rise)) {
            double m = rise / run;
            double b = connectingLine.y1 - m * connectingLine.x1;
            double resultY = m * x + b;
            return getDistance(x, y, x, resultY) <= detectionDistance;
        }
        return false;
    }

    public Double getDistance(double x1, double y1, double x2, double y2) {
        double a = x1 - x2;
        double b = y1 - y2;
        return Math.sqrt(a * a + b * b);
    }
}
