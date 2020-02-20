package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

public class Climber{

   
    CANSparkMax m_spark = new CANSparkMax(5, CANSparkMaxLowLevel.MotorType.kBrushless);
    TalonSRX m_talon = new TalonSRX(13);
    CANEncoder m_encoder = m_spark.getEncoder();
    CANEncoder m_encoder2 = m_spark.getEncoder();
    CANPIDController m_pidController = m_spark.getPIDController();

    DigitalInput m_climberSensor = new DigitalInput(0);
    DigitalInput m_climberSensor2 = new DigitalInput(0);
    
    double kP = 0.00008;
    double kI = 5e-6;
    double kD = 0.00001;
    double kIz = 2;
    double kFF = 0.0002;
    int maxRPM = 5700;
    int maxVel = 5700;
    double maxAcc = 3750;

    private static Climber m_climber = null;

    private Climber(){
      
        m_pidController.setP(kP);
		m_pidController.setI(kI);
		m_pidController.setD(kD);
		m_pidController.setIZone(kIz);
		m_pidController.setFF(kFF);
		m_pidController.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidController.setSmartMotionMinOutputVelocity(0, 0);
		m_pidController.setSmartMotionMaxAccel(maxAcc,0);
        m_pidController.setOutputRange(-1, 1);	
        
        m_spark.setIdleMode(IdleMode.kBrake);
		
    }

     public static Climber getInstance(){
        if(m_climber == null)
        m_climber = new Climber();

        return m_climber;
    }


}