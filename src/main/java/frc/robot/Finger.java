package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.ControlType;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;
import frc.robot.RobotState.State;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

//https://frc-docs.readthedocs.io/en/latest/docs/software/wpilib-overview/2020-Game-Data.html

public class Finger {

    RobotState m_state = RobotState.getInstance();

    MiniPID m_fingerLoop = new MiniPID(1, 0, 0);

    CANSparkMax m_fingerMotor = new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANEncoder m_fingerEncoder = m_fingerMotor.getEncoder();
    CANPIDController m_fingerController = m_fingerMotor.getPIDController();
    Spark m_lightRing = new Spark(0);
    DoubleSolenoid m_cylinder = new DoubleSolenoid(1, 3, 2);

    int m_ballCount = 0;
    double m_setPoint = 1;
    double m_timer = 0;
    double m_passThroughSpeed = 0;
    double m_timeCount = 0;

    double kP = 0.00008;
    double kI = 5e-6;
    double kD = 0.00001;
    double kIz = 2;
    double kFF = 0.0002;
    int maxRPM = 5700;
    int maxVel = 5700;
    double maxAcc = 3750;

    int spin_switch = 0;
    
    private final I2C.Port i2cPort = I2C.Port.kOnboard;

    public final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

    public final ColorMatch m_colorMatcher = new ColorMatch();

    public final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    public final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    public final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    public final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    private static Finger m_finger = null;

    boolean spin = false;

    String m_starting_color = "";
    Color m_detectedColor = m_colorSensor.getColor();
    int m_colorCounts = 0;

    int m_rotationalState = 0;

    public Finger(){

        m_fingerController.setP(kP);
        m_fingerController.setI(kI);
        m_fingerController.setD(kD);
        m_fingerController.setIZone(kIz);
        m_fingerController.setFF(kFF);
        m_fingerController.setSmartMotionMaxVelocity(maxVel, 0);
        m_fingerController.setSmartMotionMinOutputVelocity(0, 0);
        m_fingerController.setSmartMotionMaxAccel(maxAcc,0);
        m_fingerController.setOutputRange(-1, 1);

        m_timer = (m_timer + 20);

        m_cylinder.set(Value.kReverse);
        
    }

    public static Finger getInstance(){
        if(m_finger == null)
        m_finger = new Finger();

        return m_finger;
    }

    public void up(){
       
        m_cylinder.set(Value.kForward);
    }

    public void down(){

        m_cylinder.set(Value.kReverse);
    }

    public void run(){

        String colorString;
        m_detectedColor = m_colorSensor.getColor();
        ColorMatchResult match = m_colorMatcher.matchClosestColor(m_detectedColor);

        if (match.color == kBlueTarget) {
            colorString = "Blue";
        } else if (match.color == kRedTarget) {
            colorString = "Red";
        } else if (match.color == kGreenTarget) {
            colorString = "Green";
        } else if (match.color == kYellowTarget) {
            colorString = "Yellow";
        } else {
            colorString = "Unknown";
        }
        
        String targetColor = "";

        String gameData;
        gameData = DriverStation.getInstance().getGameSpecificMessage();
        if(gameData.length() > 0){
            switch (gameData.charAt(0)){
                case 'B' :
                    targetColor = "Red";
                break;
                case 'G' :
                    targetColor = "Yellow";
                break;
                case 'R' :
                    targetColor = "Blue";
                break;
                case 'Y' :
                    targetColor = "Green";
                break;
                default :
                //This is corrupt data
                break;
            }
        }



        if(m_state.state() == State.SPIN){

            m_fingerController.setReference(5600, ControlType.kSmartVelocity);
            
            switch(m_rotationalState){

                case 0:

                    //Wait until the color wheel spins off of the current color
                    if(m_timer > 100 && !colorString.equals(m_starting_color))
                        m_rotationalState = 1;

                    break;

                case 1:

                    //Wait until the starting color is reached, loop back to waiting to leave color, and add 1 half-rotation tick to counter
                    if(colorString.equals(m_starting_color))
                        m_rotationalState = 0;
                        m_colorCounts++;

                    break;
            }

            if(m_colorCounts > 6){
                m_state.setState(State.WAITING);
            }

        }else if(m_state.state() == State.COLOR_SELECT) {

            m_fingerController.setReference(5600, ControlType.kSmartVelocity);

            if(colorString.equals(targetColor)){
                m_state.setState(State.WAITING);  
            }
            
        }else{
            m_starting_color = colorString;
            m_colorCounts = 0;
            m_rotationalState = 0;
            m_timer = 0;
            m_fingerController.setReference(0, ControlType.kSmartVelocity);
        }

        m_timer = m_timer+20;

    }
}