package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;

public class Drivetrain{

    CANSparkMax m_rightNeoMaster = new CANSparkMax(3, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANSparkMax m_rightNeoSlave = new CANSparkMax(4, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANEncoder m_rightEncoder = m_rightNeoMaster.getEncoder();
    
    CANSparkMax m_leftNeoMaster = new CANSparkMax(1, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANSparkMax m_leftNeoSlave = new CANSparkMax(2, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANEncoder m_leftEncoder = m_leftNeoMaster.getEncoder();

    private static final int k_freeCurrentLimit = 40;
    private static final int k_stallCurrentLimit = 40;

    private static Drivetrain m_drivetrain = null;

    public Drivetrain(){
        m_rightNeoMaster.restoreFactoryDefaults();
        m_leftNeoMaster.restoreFactoryDefaults();
        m_rightNeoSlave.restoreFactoryDefaults();
        m_leftNeoSlave.restoreFactoryDefaults();

        m_rightNeoSlave.follow(m_rightNeoMaster);
        m_leftNeoSlave.follow(m_leftNeoMaster);
            
        m_rightNeoMaster.setIdleMode(IdleMode.kCoast);
        m_rightNeoSlave.setIdleMode(IdleMode.kBrake);
        m_leftNeoMaster.setIdleMode(IdleMode.kCoast);
        m_leftNeoSlave.setIdleMode(IdleMode.kBrake);

        m_rightNeoMaster.setSmartCurrentLimit(k_stallCurrentLimit, k_freeCurrentLimit);
        m_rightNeoSlave.setSmartCurrentLimit(k_stallCurrentLimit, k_freeCurrentLimit);
        m_leftNeoMaster.setSmartCurrentLimit(k_stallCurrentLimit, k_freeCurrentLimit);
        m_leftNeoSlave.setSmartCurrentLimit(k_stallCurrentLimit, k_freeCurrentLimit);

    }

    public static Drivetrain getInstance(){
        if(m_drivetrain == null)
        m_drivetrain = new Drivetrain();

        return m_drivetrain;
    }

    double outputT = 0;
    double outputD = 0;
    double ramp = 0.1;

    public void arcadeDrive(double speed, double turn){

        turn = deadband(turn);
        speed = deadband(speed);

        outputD = outputD + (outputD - speed) * -ramp;
        outputT = outputT + (outputT - turn) * -ramp;
        
        m_rightNeoMaster.set(outputD+outputT);
        m_leftNeoMaster.set(outputD-outputT);

    }

  double deadband(double value) {
    /* Upper deadband */
    if (value >= +0.20 ) 
        return value-0.2;
    
    /* Lower deadband */
    if (value <= -0.20)
        return value+0.2;
    
    /* Outside deadband */
    return 0;
}
}