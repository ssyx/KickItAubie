import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Football here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Football extends Actor
{

    double angleLaunchRadians;
    double dirSlope;
    int velocity;
    long kickBeginMS = 0;
    double kickFlightTime;
    int x0;
    int y0;
    long kickEndMS = 0;

    // used for debugging
    boolean singlestep = false;
    int kickInc = 0;
    double stepTime = 0.1;

    static double DEFAULT_LAUNCH_ANGLE_DEGREES = 45.0;
    static int DEFAULT_LAUNCH_VELOCITY = 30; // meters/sec
    static double DEFAULT_DIRECTION_SLOPE = 0;
    static double SCALE = 10.0; // meters to pixels scale factor
    static double GRAVITY = 9.8; // meters per second-squared
    static long END_OF_KICK_DELAY_MILLIS = 2000;
    
    GreenfootImage[] giF = new GreenfootImage[] { 
        new GreenfootImage( "football0.png" ),
        new GreenfootImage( "football1.png" ),
        new GreenfootImage( "football2.png" ),
        new GreenfootImage( "football3.png" ),
        new GreenfootImage( "football4.png" ) 
    };
    
    int iRotation = 0;
    boolean fRotating = false;
    
    public Football() {
        setRotation( iRotation );
    }
    
    /**
     * Act - do whatever the Football wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if (  Greenfoot.mouseDragged(this) ) {
            // Football placement via drag
            MouseInfo mouse = Greenfoot.getMouseInfo();
            int x = mouse.getX();
            int y = mouse.getY();
            this.setLocation(mouse.getX(), mouse.getY());
        }
        
        setLocation();
        
        rotate();
    }    
    
    public void reset() {
       
       if ( kickEndMS == 0 ) {
           ((Stadium)getWorld()).showKickResult( isKickGood() );
           kickEndMS = System.currentTimeMillis();
           setRotating( false );
       } else {
           
           if ( System.currentTimeMillis() - kickEndMS > END_OF_KICK_DELAY_MILLIS ) {
               // The ball is back to its launch height, the kick is over; so, reset the ball.
               kickBeginMS = 0;
               kickEndMS = 0;
               kickInc = 0;
               setLocation( x0, y0 );
            }
        }
    }
    
    public void setRotating( boolean f ) {
        fRotating = f;
    }
    
    public boolean isRotating() {
        return fRotating;
    }
    
    public void rotate() {
        if ( !isRotating() ) {
            iRotation = 0;
            setRotation( iRotation );
        }
        else {
            iRotation++;
            if (iRotation >= giF.length)
               iRotation = 0;
               
            setRotation( iRotation );
        }
    }
    
    public void setRotation(int i)
    {
        setImage( giF[i] );
    }
    
    public void kick() {
        kick( DEFAULT_LAUNCH_ANGLE_DEGREES, DEFAULT_LAUNCH_VELOCITY, DEFAULT_DIRECTION_SLOPE );
    }
    
    public void kick( int velocity ) {
        kick( DEFAULT_LAUNCH_ANGLE_DEGREES, velocity, DEFAULT_DIRECTION_SLOPE );
    }
    
    public void kick( int velocity, double dirSlope ) {
        kick( DEFAULT_LAUNCH_ANGLE_DEGREES, velocity, dirSlope );
    }
    
    public void kick( double angleLaunchDegrees, int velocity, double dirSlope ) {
        this.angleLaunchRadians = Math.toRadians(angleLaunchDegrees);
        this.velocity = velocity;
        this.dirSlope = dirSlope; // the slope of the direction of the kick
        this.kickBeginMS = System.currentTimeMillis();
        
        // Used for sanity checking the expected flight
        this.kickFlightTime = // seconds = (2 * velocity * sin(angle)) / gravity;
           2 * velocity * Math.sin( angleLaunchRadians ) / 9.8;
        
        // used for single steping the kick for debugging
        this.singlestep = false;
        this.kickInc = 0;
        
        this.x0 = getX();
        this.y0 = getY();
        
        setRotating(true);
    }
    
    /**
     * This method positions the football.  During a kick, it moves the football
     * along its trajectory based on the velocity and angle of the kick.
     */
    public void setLocation() {
        // The basic trajectory equations are:
        // xn = x0 + v * t * cos(theta)
        // yn = y0 + v * t * sin(theta) - (g*t*t)/2
        // where,
        // g=gravity which is approx 9.8 m/sec-squared;
        // t=time since launch in seconds
        // v=velocity (units are the issue here, but since g is in meters/sec-squared, 
        // v is in meters/sec
        // These positions, therefore would be in meters from some initial position.
        // They must be translated into the visual/graphical space.
        
        if ( kickBeginMS > 0 )
        {
            long t = System.currentTimeMillis() - kickBeginMS;
            double tSecs = (double)t / 1000;
            
            // to debug this method, set singlestep to true above and it will
            // increment time by a small amount on each iteration, regardless of
            // how long it actually has been since the kick
            if ( singlestep ) {
                tSecs = ++kickInc * stepTime;
            }
            
            // Trajectory deltas in meters
            double xdeltaTraj = velocity * tSecs * Math.cos( angleLaunchRadians );
            double ydeltaTraj = -(
                (velocity * tSecs * Math.sin( angleLaunchRadians ))
                - (GRAVITY * Math.pow(tSecs,2) / 2)
                );
            // Convert trajectory distances into 'world' distances
            xdeltaTraj = xdeltaTraj * SCALE;
            ydeltaTraj = ydeltaTraj * SCALE;
            
            // Now we must plot the location based on the kick angle.
            // Here, we want to move xdelta, ydelta based on the trajectory, but
            // it should be along the directional angle of the kick.
            // Here, the angular movement is along the line:
            //    deltay += mx, where m is the directional slope of the kick
            double xdeltaWorld = xdeltaTraj;
            double ydeltaWorld = ydeltaTraj + (dirSlope * xdeltaTraj);
            
            double x = x0 + xdeltaWorld;
            double y = y0 + ydeltaWorld;
            
            if (ydeltaTraj > 0 ) {
                reset();
            }
            else {
               setLocation( (int) Math.round(x), (int)Math.round(y) );
            }
        }
    }
    
    /**
     * This method determines whether the kick is good.
     * 
     * It uses the footballs original kick location, the direction of the kick and
     * the velocity of the kick to determine success.
     * 
     * This is done by some simple math that determines whether the angle of the kick
     * is such that it would pass through the uprights and whether the velocity was
     * high enough to cause the football to pass over the crossbar.
     * 
     * 
     */
    public boolean isKickGood() {
        
        boolean good = false;
        
        // We start with the line from the location of the kick based on the direction of the kick.
        // This line must cross, vertially between the two crossbars, which are located at
        // vertical lines of x=821 and x=876, from visual inspection of the world image.
        // At the same time the trajectory must cross above a line that runs through
        // x,y=821,265 and x,y=876,295, again just based on visual inspection.
        //
        // It's really a 3-dimensional approximation, x,y,z but we're using some
        // crude shortcuts here.  We project the goalpost lines to the "ground", where
        // we see that the coordinates the goalposts would hit the ground at about:
        // x,y=820,350 and x,y=875,380, again just based on visuals.
        // So, for step 1, we translate the 3d problem into a 2d problem by considering
        // the simple 2d space along the ground.
        // Does the line that starts at x0,y0, following the slope dirSlope, pass between
        // these points?  For that to be true the slope would have to be between
        // these two:
        //    the line from x0,y0 to 820,350  --> Slope= (350-y0)/(820-x0)
        //    the line from x0,y0 to 875,380  --> Slope= (380-y0)/(875-x0)
        
        // Ensure we don't step into a divide by zero for cases where the ball starts too
        // far right to be good.
        if ( x0 < 820 ) {
            // Was the kick, left-right-wise, between the uprights?
            double slopeMin = (double)(350 - y0) / (820-x0);
            double slopeMax = (double)(380 - y0) / (875-x0);
            
            if ( dirSlope >= slopeMin && dirSlope <= slopeMax )
            {
               // The kick was at least directionally between the uprights.
               
               // But, did it clear the crossbar?
               // Here, we need to know whether the trajectory would have taken the ball
               // far enough. That's a fairly simple thing to know, if we approximate the
               // distance to the goalline from the point of the kick.  We can then use
               // trajectory to determine whether the object's Y value at that point would
               // be at least as high as the crossbar.  From the above measurements, we can
               // tell that the crossbar is about (350-265)=85 units high (in world space
               // this is actually -85).
               // The distance to the goalline is the question.
               // Let's approximate by measuring the distance to the goaline under each
               // post and then use the relative angle of the slope between the two to
               // iterpolate between them.
               
               // The ole pythagorian theorem comes in handy here. x^2 + y^2 = z^2
               double distX = (820-x0);
               double distY = (350-y0);
               double distZ1 = Math.sqrt( Math.pow(distX,2) + Math.pow(distY,2) );
               distX = 850-x0;
               distY = 380-y0;
               double distZ2 = Math.sqrt( Math.pow(distX,2) + Math.pow(distY,2) );
               
               // Now the distance will be somewhere between Z1 and Z2 directly proportional
               // to the kick slope's relatve position between slopeMin and slopeMax.
               double slopeMaxToMin = slopeMax - slopeMin;
               double slopeKickToMin = dirSlope - slopeMin;
               
               double slopeRatio = slopeKickToMin / slopeMaxToMin;
               
               double distToGoalline = distZ1 + (distZ2 - distZ1) * slopeRatio;
               
               // Same sort of thing to figure out y of the literal goalline (not the crossbar)
               // along the path of the kick.
               double yOfGoalline = 350 + (double)(380-350) * slopeRatio;
               double yToClear = yOfGoalline - 85;
               
               // Whew! Now that we know the distance to the goal, we can use that
               // and trajectory math to determine the Y at that point in the trajectory.
               // The formula is:
               // y = x * tan(launch angle) -
               //     g * x-squared / 2 * velocity^2 * sin(launch angle)^2
               double deltayAtCrossbar = (distToGoalline/SCALE) * Math.tan(angleLaunchRadians)
                 - (GRAVITY * Math.pow((distToGoalline/SCALE),2))
                   / ( 2 * Math.pow(velocity,2) * Math.pow(Math.sin(angleLaunchRadians),2) );
                   
               double yAtCrossbar = y0 - (deltayAtCrossbar * SCALE);
               
               if ( yAtCrossbar < yToClear )
                  good = true;
            }
        }
        
        return good;
    }
    
    
    
}
