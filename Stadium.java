import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Stadium here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Stadium extends World
{

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
