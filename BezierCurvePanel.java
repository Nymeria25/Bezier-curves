package source;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Stack;
import javax.swing.*;

public class BezierCurvePanel extends JPanel implements MouseListener, MouseMotionListener{

    double width, height;
	
    int oldx, oldy;
    
    /* the selected curve will always be at the top of this stack */
    Stack <BezierCurve> bezier_curves;	
	
    boolean newT = false;
	
    static double T;  // use for showing parametrization
    static BezierPoint overPoint;
    
    
    public BezierCurvePanel(){
	this( null );
    }

    public BezierCurvePanel( BezierCurve curve ){
        super();
        setBackground( Color.white );
	addMouseListener( this );
	addMouseMotionListener( this );
        bezier_curves = new Stack<BezierCurve>();
        // there are no curves drawn yet, so we initialize the selected point's value
        BezierCurve.initializeSelectedPointIndex();
    }


    @Override
    public void paintComponent( Graphics graphics ){
	width = this.getWidth();
	height = this.getHeight();

	Graphics2D g = (Graphics2D)graphics;
                
        SetGraphics(g);
        DrawCurves(g);
    }
    

    public void addCurve( BezierCurve curve ){
        bezier_curves.add(new BezierCurve(curve));
        repaint();
    }        
    
    public void translate( int dx, int dy ){
	if ( bezier_curves.peek() != null ){
            if ( bezier_curves.peek().getSelectedPointIndex() >= 0 ){
                bezier_curves.peek().getSelectedPoint().translate( dx, dy );
            }
        }     
    }
    
    Point POINT;
    @Override
    public void mouseClicked( MouseEvent mouse_event ){
        POINT = mouse_event.getPoint();
            
        if ( !newT ){
            if( bezier_curves.isEmpty()) {
                bezier_curves.add(new BezierCurve( POINT.x, POINT.y));
            } else if ( overPoint != null ){
                int index = bezier_curves.peek().points.indexOf(overPoint);
                bezier_curves.peek().setSelectedPointIndex(index);
	       } else if ( !bezier_curves.isEmpty() ){
                  BezierPoint point = bezier_curves.peek(). 
                          getClosestControlPoint( POINT.x, POINT.y );      
		  if ( point == null ){
                    bezier_curves.peek().insertPoint( POINT.x, POINT.y );
                  }
                 }
	     BezierCurveApplet.textarea.setText( bezier_curves.peek().toString());
	     repaint();
        }

    }

    @Override
    public void mouseEntered( MouseEvent mouse_event ){
	requestFocus();
    }

    @Override
    public void mouseExited( MouseEvent mouse_event ){}

    @Override
    public void mousePressed( MouseEvent mouse_event ){
	requestFocus();
	mouseClicked( mouse_event );
    }

    @Override
    public void mouseReleased( MouseEvent mouse_event ){}	

    @Override
    public void mouseMoved( MouseEvent mouse_event ){
	Point p = mouse_event.getPoint();
	overPoint = null;
		
	// check if mouse is over BezierPoint on selected Curve
	if ( !bezier_curves.empty()){
            for ( BezierPoint point : bezier_curves.peek().points) {
                if ( point.distance(p) < BezierPoint.radius+1 ) overPoint = point;
            }
            
	}
	repaint();
    }
  
    @Override    
    public void mouseDragged(MouseEvent mouse_event){
	Point p = mouse_event.getPoint();
	translate( p.x-POINT.x, p.y-POINT.y );
	POINT = p;
	if ( !bezier_curves.empty() ) 
            BezierCurveApplet.textarea.setText( bezier_curves.peek().toString() );
	repaint();
    }
    
    
   private void DrawCurves(Graphics2D graphics) {
        if (!bezier_curves.isEmpty()){
        for ( BezierCurve bezier_curve : bezier_curves)
            bezier_curve.draw( this, graphics );
        }
        
        // if there are no curves to draw, set a start message
        if (bezier_curves.isEmpty()){
            String start_message = "Click anywhere to begin a new curve";
            graphics.setColor( new Color( 128,128,128,120 ) );
            graphics.setFont( new Font("Helvetica",Font.BOLD,25) );
            graphics.drawString( start_message, 
                                 (int)(width-graphics.getFontMetrics()
                                                  .stringWidth(start_message))/2,
                                 (int)(height/2) );
	}
    }
    
    private void SetGraphics( Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                  RenderingHints.VALUE_ANTIALIAS_ON);
		
	graphics.setColor( Color.white );
	graphics.fill( new Rectangle2D.Double(0,0,width,height) );
	graphics.setStroke( new BasicStroke(2.0f) );
    }
}

