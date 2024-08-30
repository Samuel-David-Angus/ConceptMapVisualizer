import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ConceptView extends JPanel {
    private Point dragStart;
    private Point PosStart;
    private final JButton plusButton;
    private final List<Connector> connectors;
    private final ConceptModel conceptModel;

    private Canvas canvas;

    private enum COLORS {
        NORMAL(Color.gray),
        HOVERED(Color.darkGray),
        SELECTED(Color.blue),
        VALID(Color.GREEN),
        INVALID(Color.RED);

        private final Color color;

        COLORS(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }
    private boolean isValid = false;

    public ConceptView(ConceptModel conceptModel) {
        this.conceptModel = conceptModel;
        this.PosStart = getLocation();
        this.connectors = new ArrayList<>();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(COLORS.NORMAL.getColor());
        this.setBorder(new EmptyBorder(0, 10, 10, 10)); // Padding (top, left, bottom, right)

        plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(20, 20)); // Set size for the button
        plusButton.setMinimumSize(new Dimension(20, 20)); // Set minimum size for the button
        plusButton.setMaximumSize(new Dimension(20, 20)); // Set size for the button
        plusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point2D.Double plusButtonCenterTop = getButtonCenterTopPosition();
                boolean canConnect = canvas.addLine(plusButtonCenterTop.x, plusButtonCenterTop.y);
                if (canConnect) {
                    setBackground(COLORS.SELECTED.getColor());
                    canvas.setSelectedConceptView(ConceptView.this);
                }
            }
        });

        // Create a label
        JLabel label = new JLabel(conceptModel.getConcept());
        label.setHorizontalAlignment(SwingConstants.CENTER); // Center align text horizontally
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to the panel
        this.add(plusButton);
        this.add(Box.createRigidArea(new Dimension(0, 5))); // Add space between button and label
        this.add(label);

        int newWidth = Math.max(label.getPreferredSize().width + 20, 100);
        int newHeight = plusButton.getPreferredSize().height + label.getPreferredSize().height + 15;
        this.setBounds(0,0,newWidth,newHeight);


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getLocationOnScreen();
                if (canvas.getDrawingState() == DRAWING_STATE.NOT_DRAWING) {
                    canvas.toggleDrawingState(DRAWING_STATE.DRAGGING);
                } else if (canvas.getDrawingState() == DRAWING_STATE.ADDING_LINE) {
                    if (isValid) {
                        canvas.setConnection(ConceptView.this);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                PosStart = getLocation();
                if (canvas.getDrawingState() == DRAWING_STATE.DRAGGING) {
                    canvas.toggleDrawingState(DRAWING_STATE.NOT_DRAWING);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (canvas.getDrawingState() == DRAWING_STATE.ADDING_LINE) {
                    isValid = canvas.getSelectedConceptView().getConceptModel().checkIfValidPrerequisite(conceptModel);
                    if (isValid) {
                        setBackground(COLORS.VALID.getColor());
                    } else {
                        setBackground(COLORS.INVALID.getColor());
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(COLORS.NORMAL.getColor());
                isValid = false;
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (canvas.getDrawingState() == DRAWING_STATE.ADDING_LINE) {
                    int x = getX() + e.getX();
                    int y = getY() + e.getY();
                    canvas.drawInitialLine(new Point(x, y));
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (canvas.getDrawingState() == DRAWING_STATE.DRAGGING) {
                    int newX = (e.getXOnScreen() - dragStart.x) + PosStart.x;
                    int newY = (e.getYOnScreen() - dragStart.y) + PosStart.y;
                    setLocation(new Point(newX, newY));
                    canvas.repaint();
                }
            }
        });

        this.revalidate();
        this.repaint();
    }

    public Point2D.Double getCenterPoint() {
        double x = getX() +  getWidth() / 2.0;
        double y = getY() + getHeight() / 2.0;
        return new Point2D.Double(x, y);
    }
    public Point2D.Double getButtonCenterTopPosition() {
        double x = getX() + plusButton.getX() + plusButton.getWidth() / 2.0;
        double y = getY() + plusButton.getY() + plusButton.getHeight() / 2.0;
        return new Point2D.Double(x, y);
    }
    public void setCanvas(Canvas canvas) {
        canvas.addConnectorGroup(connectors);
        this.canvas = canvas;
    }

    public ConceptModel getConceptModel() {
        return conceptModel;
    }
    public void addPrerequisiteConnection(ConceptView prerequisiteConceptView) {
        conceptModel.addPrerequisite(prerequisiteConceptView.getConceptModel());
        connectors.add(new Connector(this, prerequisiteConceptView));
    }
    public void removePrerequisiteConnection(ConceptView prerequisiteConceptView) {
        conceptModel.removePrerequisite(prerequisiteConceptView.getConceptModel());
        connectors.removeIf(connector -> connector.getPrerequisiteConceptView() == prerequisiteConceptView);
    }
}
