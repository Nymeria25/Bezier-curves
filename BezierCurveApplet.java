package source;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.border.*;
import javax.swing.event.*;


public class BezierCurveApplet extends JApplet 
                               implements ActionListener, ChangeListener, 
                                          WindowListener{

    BezierCurvePanel beziercurvepanel;
    static TextArea textarea;

    /* when pressed, adds a new curve with the control points in the TextArea */
    JButton enter; 
    
    Container contentpane;
    JFrame frame;

    @Override
    public void init(){
        InitializeWindow(600,600,"Bezier Curves");
	
	JPanel lower_panel = new JPanel( new BorderLayout() );
        SetLowerPanel(lower_panel);
        
        ArrayList<Entry<String,JPanel>> panels = new ArrayList();
        panels.add(new Pair<String,JPanel>("South",lower_panel));
        
        AddPanelsToWindow(panels);
        CreateBezierPanel();
        SetListeners();
        
        this.showFrame();
    }
    
    public void showFrame(){
        frame.setVisible( true );
    }
 		
    @Override
    public void actionPerformed( ActionEvent event ){
        Object obj = event.getSource();	
        if ( obj.equals(enter) ){
            beziercurvepanel.addCurve( new BezierCurve(textarea.getText()) );
        }
    }
        
    @Override
    public void stateChanged( ChangeEvent event ){}
    @Override
    public void windowActivated( WindowEvent event ){}
    @Override
    public void windowClosed( WindowEvent event ){}
    
    // when we close the window, we force the program to terminate.
    @Override
    public void windowClosing( WindowEvent event ){ 
        System.exit(0); 
    }
    @Override
    public void windowDeactivated( WindowEvent event ){}
    @Override
    public void windowDeiconified( WindowEvent event ){}
    @Override
    public void windowIconified( WindowEvent event ){}
    @Override
    public void windowOpened( WindowEvent event ){}
        
    // initializes the application's main window, by setting the frame's width and
    // height to x and y, and the title. Also sets contentpane.
    private void InitializeWindow(int x, int y, String title) {
        frame = new JFrame();
	frame.setVisible(false);
	frame.setSize(x, y);
	frame.setLocation(0,0);
	frame.setTitle(title);
        
        contentpane = frame.getContentPane();
        contentpane.setLayout( new BorderLayout() );
    }
    
    
    // sets the lower panel in the window, adding a TextArea for the control
    // points coordinates and an Enter button that would create a new curve
    // based on the current control polygon in the TextArea
    private void SetLowerPanel(JPanel panel) {
        JPanel control_panel = new JPanel( new BorderLayout() );
	control_panel.add( "Center", textarea = new TextArea("",2,5, TextArea.SCROLLBARS_VERTICAL_ONLY));
	control_panel.add( "East", enter = new JButton("Enter") );
	control_panel.setBorder( BorderFactory.createTitledBorder(
                control_panel.getBorder(), "Control points", TitledBorder.LEFT, 
                TitledBorder.TOP, new Font( "Helvetica", Font.BOLD, 15) ) );
	panel.add( "Center", control_panel );
    }
    
    // adds the ArrayList of panels to the contentpane of the window.
    // The key of the Entry represents the place of the panel (north,center,..),
    // while the value represents a JPanel.
    private void AddPanelsToWindow(ArrayList<Entry<String,JPanel>> panels) {
        for(Entry<String,JPanel> panel : panels)
            contentpane.add( panel.getKey(), panel.getValue() );    
    }
    
    private void CreateBezierPanel() {
        contentpane.add( "Center", beziercurvepanel = new BezierCurvePanel() );
    }
    
    private void SetListeners() {
        enter.addActionListener( this );
	frame.addWindowListener( this );
    }
    
        final class Pair<K, V> implements Entry<K, V> {
        private final K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
