import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * This is the base world for the KickItAubie game.
 *
 * The background image is a picture of Jordan-Hare Stadium at Auburn University.
 * Aubie, as you probably know, is the team mascot.
 * 
 * Aubie is the primary actor in the stadium and will kick field goals in this
 * interactive game.
 * 
 * Explicit knowledge about the content of the background image is required by
 * the programmer, as the content of the game is dependent upon the background.
 *
 */
public class Stadium extends World
{

   boolean showgrid = false; // grid for debugging placement/alignment
   Football football;
   Aubie aubie;
   
    /**
     * Constructor for objects of class Stadium.
     * 
     */
    public Stadium()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(962, 652, 1, false); 
        
        football = new Football();
        aubie = new Aubie();
        
        addObject( football, 200, 400 );
        addObject( aubie, 100, 300 );
        addObject( new Referee(), 770, 280 );
        addObject( new Referee(), 920, 360 );
        
        GreenfootImage gi = getBackground();
        gi.setColor( java.awt.Color.RED );
        
        if ( showgrid == true ) {
            for ( int i = 0; i < gi.getHeight(); i += 100 ) {
                gi.drawLine( 0, i, getWidth(), i );
                gi.drawLine( i, 0, i, getHeight() );
            }
        }
    }
    
    public Football getFootball() {
        return football;
    }
        
    public Aubie getAubie() {
        return aubie;
    }
    
    public void showKickResult( boolean fGoodKick ) {
        java.util.List<Referee> refs = getObjects( Referee.class );
        java.util.Iterator<Referee> i = refs.iterator();
        while ( i.hasNext() ) {
            Referee ref = i.next();
            ref.showKickResult(fGoodKick);
        }
    }
    
}
