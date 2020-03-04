package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climber{

    DoubleSolenoid m_drainSnake = new DoubleSolenoid(1, 3, 2);
   
    CANSparkMax m_spark = new CANSparkMax(12, CANSparkMaxLowLevel.MotorType.kBrushless);
    TalonSRX m_talon = new TalonSRX(13);
    CANEncoder m_encoder = m_spark.getEncoder();
    CANEncoder m_encoder2 = m_spark.getEncoder();
    CANPIDController m_pidController = m_spark.getPIDController();

    double m_setPoint = LiftLevels.LOW.encoderPosition();
   
    double kP = 0.00008;
    double kI = 5e-6;
    double kD = 0.00001;
    double kIz = 2;
    double kFF = 0.0002;
    int maxRPM = 5700;
    int maxVel = 5700;
    double maxAcc = 3750;


    private static Climber m_climber = null;

    /**Climber constuctor */
    private Climber(){

        m_spark.restoreFactoryDefaults();
      
        m_pidController.setP(kP);
		m_pidController.setI(kI);
		m_pidController.setD(kD);
		m_pidController.setIZone(kIz);
		m_pidController.setFF(kFF);
		m_pidController.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidController.setSmartMotionMinOutputVelocity(0, 0);
		m_pidController.setSmartMotionMaxAccel(maxAcc,0);
        m_pidController.setOutputRange(-1, 1);	

        m_talon.config_kF(0, 0.0334, 50);
		m_talon.config_kP(0, 0.8, 50);
		m_talon.config_kI(0, 0.004, 50);
        m_talon.config_kD(0, 0, 50);
        m_talon.configMotionCruiseVelocity(6000, 50);
        m_talon.configMotionAcceleration(6000, 50);
        m_talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 50);
        m_talon.setSelectedSensorPosition(0, 0, 50);
        m_talon.config_IntegralZone(0, 75, 50);

        down();

        
        m_spark.setIdleMode(IdleMode.kCoast);

        down();

        m_talon.configContinuousCurrentLimit(30);
        m_talon.configPeakCurrentLimit(30);
        m_talon.enableCurrentLimit(true);
        
		
    }

    /**Enum storing m_climber positional information */
    public enum LiftLevels{
        LOW(5), MID(6193), MAX(12000);
    
        private int encoderPosition;
        public int encoderPosition() {
            return this.encoderPosition;
        }
        LiftLevels(int encoderPosition) {
            this.encoderPosition = encoderPosition;
        }
    }

     public static Climber getInstance(){
        if(m_climber == null)
        m_climber = new Climber();

        return m_climber;
    }

    /**Set the m_climber position.
      * @param level: Liftlevels value storing m_climber level information. 
      */
    public void setLevel(LiftLevels level) {	

		m_setPoint = level.encoderPosition();
		m_talon.set(ControlMode.MotionMagic, -m_setPoint);

	//	m_talon.set(ControlMode.MotionMagic, m_setPoint);
    }
    
    /**Function used to control the climber with a joystick*/
    public void operateAnalog(double joystick){

        joystick = deadband(joystick);
        m_setPoint = m_setPoint + Math.round(joystick*100);

        if(m_setPoint > LiftLevels.MAX.encoderPosition()){
            m_setPoint = LiftLevels.MAX.encoderPosition();
        }
        if(m_setPoint < LiftLevels.LOW.encoderPosition()){
            m_setPoint = LiftLevels.LOW.encoderPosition();
        }

        m_talon.set(ControlMode.MotionMagic, -m_setPoint);

    }

    public void up(){
       
        m_drainSnake.set(Value.kForward);
    }

    public void down(){

        m_drainSnake.set(Value.kReverse);
    }

    public void run(boolean isOn){
         if(isOn){
            m_spark.set(0.2);
        }else{
            m_spark.set(0);
        }        
    }

    private double deadband(double value) {
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