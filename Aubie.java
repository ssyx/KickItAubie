import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Aubie is the prime actor in this game.  Aubie's job is to kick the field goal.
 * 
 * At the current time, the direction of Aubie's kick is based on plotting a line
 * from Aubie running through the football.  The strength of the kick is determined
 * by how far Aubie is from the ball. Aubie must start approach left of the football
 * for a kick to occur.
 * 
 * Aubie can be moved
 */
public class Aubie extends Actor
{
    static double V_SCALE = 0.5;  // multiplier on stepoff for kick velocity
    
    boolean dragStarted = false;  // simple state variable to eat the mouse-up after moving Aubie
    
    int xKickStart;
    int yKickStart;
    int xStepoff;     // the x distance from aubie to the ball
    int yStepoff;     // the y distance from aubie to the ball
    double xEachStep; // the x distance of each step
    double yEachStep; // the y distance of each step
    double kickVelocity;   // determined based on the lenght of the approach
    double kickDirection;  // angular slope (rise/run) of the approach to the ball
    long lKickBeginMS;
    
    static int APPROACH_STEPS = 3;
    static int APPROACH_MILLIS = 750;
    static long MILLIS_PER_STEP = APPROACH_MILLIS / APPROACH_STEPS;
    
    /**
     * Act - do whatever the Aubie wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if (lKickBeginMS != 0 ) {
            approach();
        }
        else {
             if (  Greenfoot.mouseDragged(this) ) {
                // Aubie placement via drag
                MouseInfo mouse = Greenfoot.getMouseInfo();
                this.setLocation(mouse.getX(), mouse.getY());
                dragStarted = true;
            }
            
            if ( Greenfoot.mouseClicked(this) ) {
                if ( dragStarted == false )
                {
                    kickTheBall();
                }
                dragStarted = false;
            }
        }
    } 
    
    public void approach() {
       long lNow = System.currentTimeMillis();
       long step = (lNow - lKickBeginMS) / MILLIS_PER_STEP;
       if ( step > APPROACH_STEPS )
          step = APPROACH_STEPS;
       int xStep = xKickStart + (int)Math.round(xEachStep * step);
       int yStep = yKickStart + (int)Math.round(yEachStep * step);
       if ( getX() != xStep ) {
           setLocation( xStep, yStep );
       }
       if ( step == 3 )
          impactBall();
    }
    
    public void kickTheBall() {
        
        xKickStart = getX();
        yKickStart = getY();
        
        // calculate the velocity for the kick based on how far Aubie is from the ball
        Football football = ((Stadium)getWorld()).getFootball();
        
        // All this is calculated from the lower right corner of aubie.
        // Understanding object-coordinate system mapping here is critical.
        // The location of an Actor is the center of its image.
        int aubieRightX = getX() + (getImage().getWidth() / 2);
        int aubieLowerY = getY() + (getImage().getHeight() / 2);
        // And 2/3 of the way down the left side of the football.
        int footballLeftX = football.getX() + (football.getImage().getWidth() / 2);
        int footballLowerY = football.getY() + (football.getImage().getHeight() / 3);
        
        // Aubies step off distance for the kick
        xStepoff = footballLeftX - aubieRightX;
        xEachStep = xStepoff / APPROACH_STEPS;
        yStepoff = footballLowerY - aubieLowerY;
        yEachStep = yStepoff / APPROACH_STEPS;
        
        if ( xStepoff > 0 ) {
            // Aubie is left of the ball. Can kick.
            kickVelocity = (xStepoff) * V_SCALE;
            
            if ( kickVelocity > 0 ) {
               kickDirection = (double)(footballLowerY - aubieLowerY) / xStepoff;
            }
        }
        
        lKickBeginMS = System.currentTimeMillis();
        
    }
    
    public void impactBall() {
       // uses kick parameters determined when the kick was started
       Football football = ((Stadium)getWorld()).getFootball();
       football.kick( (int)Math.round(kickVelocity), kickDirection );
       lKickBeginMS = 0;
    }
}
