import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Aubie here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Aubie extends Actor
{
    static double V_SCALE = 0.5;  // multiplier on stepoff for kick velocity
    
    /**
     * Act - do whatever the Aubie wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
         if (  Greenfoot.mouseDragged(this) ) {
            // Aubie placement via drag
            MouseInfo mouse = Greenfoot.getMouseInfo();
            this.setLocation(mouse.getX(), mouse.getY());
        }
        
        if ( Greenfoot.mouseClicked(this) ) {
            // calculate the velocity for the kick based on how far Aubie is from the ball
            Football football = ((Stadium)getWorld()).getFootball();
            
            // All this is calculated from the lower right corner of aubie.
            int aubieRightX = getX() + getImage().getWidth();
            int aubieLowerY = getY() + getImage().getHeight();
            // And 2/3 of the way down the left side of the football.
            int footballLeftX = football.getX();
            int footballLowerY = football.getY() + football.getImage().getHeight() * 2 / 3;
            
            // Aubies step off distance for the kick
            int stepoff = footballLeftX - aubieRightX;
            
            if ( stepoff > 0 ) {
                // Aubie is left of the ball. Can kick.
                double velocity = (stepoff) * V_SCALE;
                
                if ( velocity > 0 ) {
                   double dirAngleSlope = (double)(footballLowerY - aubieLowerY) / stepoff;
                   football.kick( (int)Math.round(velocity), dirAngleSlope );
                }
            }
        }
    }    
}
