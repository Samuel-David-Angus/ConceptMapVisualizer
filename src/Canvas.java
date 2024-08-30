import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

enum DRAWING_STATE {
    NOT_DRAWING,
    ADDING_LINE,
    DRAGGING,
    DELETING
}

public class Canvas extends JPanel {
    private Line2D.Double lineToAdd;
    private Connector connectorToDelete;
    private final List<List<Connector>> allConnectors;
    private ConceptView selectedConceptView;
    //flags
    private DRAWING_STATE drawingState;

    public Canvas() {
        drawingState = DRAWING_STATE.NOT_DRAWING;
        allConnectors = new ArrayList<>();
        this.setFocusable(true);
        this.setLayout(null);
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_I) {
                    showInstructionPopUp();
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    showAddConceptMenu();
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    drawingState = DRAWING_STATE.DELETING;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (drawingState == DRAWING_STATE.ADDING_LINE) {
                    drawInitialLine(e.getPoint());
                } else if (drawingState == DRAWING_STATE.DELETING) {
                    highlightSelectedLines(e.getPoint());
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (drawingState == DRAWING_STATE.DELETING && connectorToDelete != null) {
                    connectorToDelete.getSourceConceptView().removePrerequisiteConnection(connectorToDelete.getPrerequisiteConceptView());
                    connectorToDelete = null;
                    drawingState = DRAWING_STATE.NOT_DRAWING;
                    repaint();
                } else {
                    drawingState = DRAWING_STATE.NOT_DRAWING;
                    lineToAdd = null;
                }
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D) g;

        for (List<Connector> lineGroup: allConnectors) {
            for (Connector connector: lineGroup) {
                Line2D.Double line = connector.getConnectingLine();
                graphics2D.drawLine((int) line.x1, (int) line.y1, (int) line.x2, (int) line.y2);
            }
        }
        if (lineToAdd != null) {
            graphics2D.drawLine((int) lineToAdd.x1, (int) lineToAdd.y1, (int) lineToAdd.x2, (int) lineToAdd.y2);
        }
        if (connectorToDelete != null) {
            Line2D.Double lineToDelete = connectorToDelete.getConnectingLine();
            graphics2D.setColor(Color.red);
            graphics2D.drawLine((int) lineToDelete.x1,(int) lineToDelete.y1,(int) lineToDelete.x2,(int) lineToDelete.y2);
            graphics2D.setColor(Color.black);
        }
    }

    public ConceptView getSelectedConceptView() {
        return selectedConceptView;
    }

    public void setSelectedConceptView(ConceptView selectedConceptView) {
        this.selectedConceptView = selectedConceptView;
    }

    void showInstructionPopUp() {
        JOptionPane.showMessageDialog(this, "<html>Press i to show instructions popup<br>Press a to show add concept menu popup<br>Press d to cut connection between concepts</html>", "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    void showAddConceptMenu() {
        JTextField textField = new JTextField(10);

        Object[] options = {"Add", "Cancel"};

        Object[] message = {
                "Enter your Concept:", textField
        };

        int result = JOptionPane.showOptionDialog(
                this,
                message,
                "Add Concept",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (result == JOptionPane.OK_OPTION) {
            String input = textField.getText();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Input is empty!",
                        "Alert",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            ConceptView newConcept = new ConceptView(new ConceptModel(input));
            newConcept.setLocation(0,0);
            this.add(newConcept);
            newConcept.setCanvas(this);
            this.revalidate();
            this.repaint();
            System.out.println("Input: " + input);
        } else {
            System.out.println("Cancelled");
        }
    }

    boolean addLine(double startX, double startY) {
        requestFocusInWindow();
        if (getComponents().length == 1) {
            JOptionPane.showMessageDialog(this, "Nothing to connect to", "Message", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        lineToAdd = new Line2D.Double(startX, startY, 0, 0);
        drawingState = DRAWING_STATE.ADDING_LINE;
        return true;
    }
    void addConnectorGroup(List<Connector> connectorGroup) {
        allConnectors.add(connectorGroup);
    }
    void toggleDrawingState(DRAWING_STATE newDrawingState) {
        drawingState = newDrawingState;
    }
    DRAWING_STATE getDrawingState() {
        return drawingState;
    }
    void drawInitialLine(Point point) {
        lineToAdd.setLine(lineToAdd.getP1(), point);
        repaint();
    }
    void setConnection(ConceptView prerequisiteConceptView) {
        selectedConceptView.addPrerequisiteConnection(prerequisiteConceptView);
        drawingState = DRAWING_STATE.NOT_DRAWING;
        lineToAdd = null;
        repaint();
    }
    void highlightSelectedLines(Point point) {
        boolean isTouchingLine = false;
        for (List<Connector> connectorGroup: allConnectors) {
            for (Connector connector: connectorGroup) {
                isTouchingLine = connector.checkIfOnLine(point.x, point.y);
                if (isTouchingLine) {
                    connectorToDelete = connector;
                    repaint();
                    return;
                }
            }
        }
        if (connectorToDelete != null) {
            repaint();
            connectorToDelete = null;
        }
    }
}
