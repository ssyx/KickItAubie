import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Referee here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Referee extends Actor
{
    GreenfootImage giRefGood = new GreenfootImage( "images/refgood.png" );
    GreenfootImage giRef = new GreenfootImage( "images/referee.png" );
    GreenfootImage giRefNotGood1 = new GreenfootImage( "images/refnotgood1.png" );
    GreenfootImage giRefNotGood2 = new GreenfootImage( "images/refnotgood2.png" );
    
    
    long lGoodBegin = 0;
    long lNotGoodBegin = 0;
    static long GOOD_DELAY_MILLIS = 2000;

    static long NOT_GOOD_SWITCH = 1000;
    static long NOT_GOOD_DELAY_MILLIS = 4000;
    
    /**
     * Act - do whatever the Referee wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
       if (  Greenfoot.mouseDragged(this) ) {
           // Ref placement via drag
           MouseInfo mouse = Greenfoot.getMouseInfo();
           this.setLocation(mouse.getX(), mouse.getY());
       }
       
       if ( lNotGoodBegin > 0 ) {
           animateNotGood();
       }
       
       rest();
    }
    
    public void rest() {
        long lNow = System.currentTimeMillis();
        if (  (lGoodBegin > 0 && (lNow - lGoodBegin > GOOD_DELAY_MILLIS))
           || (lNotGoodBegin > 0 && (lNow - lNotGoodBegin > NOT_GOOD_DELAY_MILLIS)) )
        {
            restRef();
        }
    }
    
    public void animateNotGood() {
        long lNow = System.currentTimeMillis();
        long lDiff = lNow - lNotGoodBegin;
        if ( (lDiff % NOT_GOOD_SWITCH) % 2 == 1 ) {
            setImage( giRefNotGood2 );
        } else {
            setImage( giRefNotGood1 );
        }
    }
    
    public void showKickResult( boolean fGood ) {
        if ( fGood )
           showGood();
        else 
           showNotGood();
    }
    
    public void showGood() {
        
        setImage( giRefGood );
        lGoodBegin = System.currentTimeMillis();
    }
    
    public void showNotGood() {
        setImage( giRefNotGood1 );
        lNotGoodBegin = System.currentTimeMillis();
    }
    
    public void restRef() {
        setImage( giRef );
        lGoodBegin = lNotGoodBegin = 0;
    }
    
}
