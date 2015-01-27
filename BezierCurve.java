package source;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;


public class BezierCurve{

    ArrayList<BezierPoint> points;
    private static int selectedPointIndex;
	
    static double threshold = 5.0;
    Color color;
    
    static public void initializeSelectedPointIndex() {
        selectedPointIndex = -1;
    }
    
    public BezierPoint getSelectedPoint() {
        return points.get(selectedPointIndex);
    }
    
    public int getSelectedPointIndex() {
        return selectedPointIndex;
    }
    
    public void setSelectedPointIndex(int index) {
        selectedPointIndex = index;
    }
    
    
    public BezierCurve(){
        points = new ArrayList();
    }
    
       
    public BezierCurve( double x, double y ) {
        points = new ArrayList();
        points.add(new BezierPoint( x, y ));
    }


    public BezierCurve( BezierPoint point ){
        points = new ArrayList();
        points.add(new BezierPoint( point ));
    }


    public BezierCurve( BezierCurve curve ){
        points = new ArrayList();
        points = curve.points;
    }
     

    public BezierCurve( String str ){
        points = new ArrayList();
        StringTokenizer st = new StringTokenizer( str.replace('{',' '), "}" );
        while ( st.hasMoreTokens() ){
            points.add(new BezierPoint( st.nextToken()));
        }
    }
	
	

    public double distance( Point point ){
        double distance = 100, t = 0.0;
	double distanceFromA, dotProduct, distanceAB;
	BezierPoint A = getPoint( 0.0 );
	BezierPoint B;
		
	while ( t <= 1.0 && distance >= threshold ){
            // check distance from A
            distanceFromA = getPoint(t).distance(point.x,point.y);
            if ( distanceFromA < distance ) distance = distanceFromA;
			
            // check distance from line segment AB
            B = getPoint( t + 0.01 );
            BezierPoint left = getPointOfDifCoordinates(A,B);
            BezierPoint right = getPointOfDifCoordinates(A,point);
            dotProduct = getDotProduct(left, right);
            
            distanceAB = A.distance(B) * A.distance(B) ;
            if ( dotProduct > 0 && dotProduct < distanceAB ){
		double current = 
                    Math.sqrt( A.distance(point) * A.distance(point) - 
                        dotProduct*dotProduct/distanceAB );
		if ( current < distance ) distance = current;
            }

            A = B;
            t += 0.01;
        }

	return distance;
  }
    
    
    
    public void draw( BezierCurvePanel panel, Graphics2D g ){
        
        DrawLinesImplementation(panel, g);
        DrawCurveImplementation(panel, g);
        DrawDotsImplementation(panel, g);
        
    }

    public BezierPoint getPoint( double t ){
	if ( !points.isEmpty() ) {
		if ( points.size() == 1 || t == 0.0 ){
			return points.get(0);
		} else if ( t == 1.000 ){
			return points.get(points.size() - 1);
		} else {
                    BezierPoint point1 = points.get(0);
                    BezierPoint point2 = points.get(1);
                    
                    BezierPoint dif = getPointOfDifCoordinates(point2, point1).scale(t);
                    BezierPoint result = getPointOfSumCoordinates(dif, point1);
		    BezierCurve curve = new BezierCurve ( result);
                    
                    for ( int i=1; i < points.size() - 1; i++) {
                        dif = getPointOfDifCoordinates(points.get(i+1), points.get(i)).scale(t);
                        result = getPointOfSumCoordinates(dif, points.get(i));
              
                        curve.localInsertPoint(result);
                        
                    }
	
			return curve.getPoint( t );
		}
	}
	return null;
 }

   
    public void insertPoint( double x, double y ){
	insertPointImplementation( new BezierPoint(x,y) );
    }


    public void insertPoint( BezierPoint point ){
	insertPointImplementation(point);
    }

    // the localInsertPoint does not affect the selected point index,
    // we need this when we compute subdivisions
    public void localInsertPoint( BezierPoint point ){
	points.add(point);
    }

    @Override
    public String toString(){
	String out = "{";
        int position = 0;
        int length = points.size();
        for(BezierPoint point : points) {
            out += point.toString();
            position++;
            if ( position < length ) out += ", ";
        }
        
        out += "}";
        return out;
    }


    public void translate( double dx, double dy ){
        for(BezierPoint point : points) {
            point.translate(dx, dy);
        }
    }
    
    
    // returns first control point that is within 5 pixels of (x,y)
    // returns null if no such control point exists
    public BezierPoint getClosestControlPoint( int x, int y ){
        BezierPoint out = null;
        for(BezierPoint point : points) {
            if(out != null) break;
            if ( point.distance( x, y ) < 6 ){
		out = point;
	    }
        }
        return out;
    }
    
    
    
    private void insertPointImplementation(BezierPoint point) {
        points.add(point);
        selectedPointIndex++;      
    }
     
    // calculates the scalar product between two points
    private double getDotProduct( BezierPoint p1, BezierPoint p2 ){
	return p1.x*p2.x + p1.y*p2.y;
    }
    
    private BezierPoint getPointOfSumCoordinates( BezierPoint p1, BezierPoint p2 ){
	return new BezierPoint( p1.x + p2.x, p1.y + p2.y );
    }
    
    private BezierPoint getPointOfDifCoordinates( BezierPoint p1, Point p2 ){
	return new BezierPoint( p1.x - p2.x, p1.y - p2.y );
    }
    
    private BezierPoint getPointOfDifCoordinates( BezierPoint p1, BezierPoint p2 ){
	return new BezierPoint( p1.x - p2.x, p1.y - p2.y );
    }
	
    private void DrawLinesImplementation( BezierCurvePanel panel, Graphics2D g ) {
        BezierPoint previous = null, current;
        if ( this == panel.bezier_curves.peek() ) {
                g.setColor( Color.gray );
                if ( ! points.isEmpty()) previous = points.get(0);
                for(int i=1; i<points.size(); i++) {
                    current = points.get(i);

                    g.draw( new Line2D.Double(previous.x, previous.y,
                                              current.x, current.y));
                    previous = current;
                }
            }
    }
    
    private void DrawCurveImplementation( BezierCurvePanel panel, Graphics2D g ) {
	g.setColor( Color.black );
	g.setStroke( new BasicStroke( 2.0f ) );
	if ( this == panel.bezier_curves.peek() && selectedPointIndex == -1 ){
            g.setColor( Color.red );
            g.setStroke( new BasicStroke( 3.0f ) );
	}
       BezierPoint point = null;
       if (points.size() > 0) {
           point = points.get(0);
        }
		
        g.setStroke( new BasicStroke( 3.0f ) );
		
	for ( double t=0.01; t<=1.0; t+=.01 ){
            BezierPoint t_point = getPoint( t );
            if (point!= null && t_point!=null)
            g.draw( new Line2D.Double( point.x, point.y, t_point.x, t_point.y ) );
            point = t_point;
	}

        
    }
    
      
    
    private void DrawDotsImplementation( BezierCurvePanel panel, Graphics2D g ) {
	double r = BezierPoint.radius;
	g.setStroke( new BasicStroke( 1.0f ) );
	if ( this == panel.bezier_curves.peek() ){
            
            int selectedPointIndex = panel.bezier_curves.peek().
                                     getSelectedPointIndex();
            int index = 0;
            for ( BezierPoint point : panel.bezier_curves.peek().points) {
                g.setColor( Color.black );
                if ( selectedPointIndex == index ) {
                    g.setColor( Color.red );
                    g.draw( new Ellipse2D.Double( point.x-r, point.y-r, 2*r, 2*r) );
                }
                else {
                    g.fill( new Ellipse2D.Double( point.x-r, point.y-r, 2*r, 2*r) );
                }
                
                index++;
            }
            
            BezierPoint point = panel.overPoint;
            if ( selectedPointIndex == panel.bezier_curves.peek().points.indexOf(point) ) {
                 point = getPoint( panel.T );
                 g.fill( new Ellipse2D.Double( point.x-r+1.5, point.y-r+1.5, 2*r-3, 2*r-3) );
            }
       }
    }
}