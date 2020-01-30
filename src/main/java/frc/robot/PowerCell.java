package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DigitalInput;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

public class PowerCell{

    double shooterSpeed = 0;

    DoubleSolenoid m_arm = new DoubleSolenoid(0, 1);
    DoubleSolenoid m_arm2 = new DoubleSolenoid(2, 3);

    RobotState m_state = RobotState.getInstance();

    CANSparkMax m_neoIntake = new CANSparkMax(5, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANEncoder m_neoPassEncoder = m_neoIntake.getEncoder();
    CANPIDController m_pidController = m_neoIntake.getPIDController();

    DigitalInput m_passthroughSensor = new DigitalInput(0);
    DigitalInput m_shooterSensor = new DigitalInput(0);

    int m_ballCount = 0;
    double m_setPoint = 0;

    double kP = 0.00008;
    double kI = 5e-6;
    double kD = 0.00001;
    double kIz = 2;
    double kFF = 0.0002;
    int maxRPM = 5700;
    int maxVel = 5700;
	double maxAcc = 3750;

    private static PowerCell m_powercell = null;

    private PowerCell(){
        m_neoIntake.restoreFactoryDefaults();

        m_pidController.setP(kP);
		m_pidController.setI(kI);
		m_pidController.setD(kD);
		m_pidController.setIZone(kIz);
		m_pidController.setFF(kFF);
		m_pidController.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidController.setSmartMotionMinOutputVelocity(0, 0);
		m_pidController.setSmartMotionMaxAccel(maxAcc,0);
		m_pidController.setOutputRange(-1, 1);	
    }

    public static PowerCell getInstance(){
        if(m_powercell == null)
        m_powercell = new PowerCell();

        return m_powercell;
    }

    public void run(){

         boolean armState = false;

         switch(m_state.state()){

            case GRAB_CELL:

                armState = true;
                m_neoIntake.set(1);

                 break;

            default:
                armState = false;
               m_neoIntake.set(0);

               break;
         }

         if(armState){
            m_arm.set(Value.kReverse);
            m_arm2.set(Value.kReverse);
        }else{
            m_arm.set(Value.kForward);
            m_arm2.set(Value.kForward);
    
        }
        
       

        if(m_passthroughSensor.get() == false){
            m_ballCount = m_ballCount + 1;
            m_pidController.setReference(m_setPoint, ControlType.kSmartMotion);

        }

     if(m_shooterSensor.get() == false){
        shooterSpeed = 1;
     }else{
        shooterSpeed = 0;}
  }

    public void reset(){
        m_neoPassEncoder.setPosition(0);
    }
      
}