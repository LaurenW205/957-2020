package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotState.State;

public class PowerCell{

    double m_shooterSpeed = 0;
     DoubleSolenoid m_arm = new DoubleSolenoid(1, 0, 1);
     Compressor m_compressor = new Compressor(1);


    RobotState m_state = RobotState.getInstance();

    CANSparkMax m_neoIntake = new CANSparkMax(6, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANSparkMax m_neoPassthrough = new CANSparkMax(7, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANSparkMax m_neoPassthrough2 = new CANSparkMax(8, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANEncoder m_neoPassEncoder = m_neoPassthrough.getEncoder();
    CANPIDController m_pidController = m_neoPassthrough.getPIDController();

    DigitalInput m_intakeSensor = new DigitalInput(1);
    DigitalInput m_shooterSensor = new DigitalInput(0);

    CANSparkMax m_shooterMaster = new CANSparkMax(9, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANSparkMax m_shooterSlave = new CANSparkMax(10, CANSparkMaxLowLevel.MotorType.kBrushless);
    CANEncoder m_shooterMasterEncoder = m_shooterMaster.getEncoder();

    CANPIDController m_pidControllerShooter1 = m_shooterMaster.getPIDController();
    CANPIDController m_pidControllerShooter2 = m_shooterSlave.getPIDController();

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

    private static PowerCell m_powercell = null;

    double m_index_distance = 34;

    boolean m_armState = false;
    boolean m_passState = true;
    boolean m_armDeepStorage = false;

    private PowerCell(){
        reset();
        m_neoIntake.restoreFactoryDefaults();
        m_neoPassthrough.restoreFactoryDefaults();
        m_neoPassthrough2.restoreFactoryDefaults();
        m_shooterMaster.restoreFactoryDefaults();
        m_shooterSlave.restoreFactoryDefaults();

        m_neoIntake.setIdleMode(IdleMode.kCoast);

        m_neoPassthrough2.follow(m_neoPassthrough);
        m_shooterSlave.setInverted(true);

        m_pidController.setP(kP);
		m_pidController.setI(kI);
		m_pidController.setD(kD);
		m_pidController.setIZone(kIz);
		m_pidController.setFF(kFF);
		m_pidController.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidController.setSmartMotionMinOutputVelocity(0, 0);
		m_pidController.setSmartMotionMaxAccel(maxAcc,0);
        m_pidController.setOutputRange(-1, 1);	
        
        m_pidControllerShooter1.setP(kP);
		m_pidControllerShooter1.setI(kI);
		m_pidControllerShooter1.setD(kD);
		m_pidControllerShooter1.setIZone(kIz);
		m_pidControllerShooter1.setFF(kFF);
		m_pidControllerShooter1.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidControllerShooter1.setSmartMotionMinOutputVelocity(0, 0);
		m_pidControllerShooter1.setSmartMotionMaxAccel(maxAcc,0);
        m_pidControllerShooter1.setOutputRange(-1, 1);	

        m_pidControllerShooter2.setP(kP);
		m_pidControllerShooter2.setI(kI);
		m_pidControllerShooter2.setD(kD);
		m_pidControllerShooter2.setIZone(kIz);
		m_pidControllerShooter2.setFF(kFF);
		m_pidControllerShooter2.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidControllerShooter2.setSmartMotionMinOutputVelocity(0, 0);
		m_pidControllerShooter2.setSmartMotionMaxAccel(maxAcc,0);
        m_pidControllerShooter2.setOutputRange(-1, 1);	
        
    } 
     public void setIdleMode(IdleMode mode){
        m_neoPassthrough.setIdleMode(mode);
        m_neoPassthrough2.setIdleMode(mode);
        
     }
    public static PowerCell getInstance(){
        if(m_powercell == null)
        m_powercell = new PowerCell();

        return m_powercell;
    }

    public void run(){

        SmartDashboard.putBoolean("Start of Pass, Sensor 1", m_intakeSensor.get());
        SmartDashboard.putBoolean("Shooter Sensor 3", m_shooterSensor.get());
        SmartDashboard.putNumber("Ball Count", m_ballCount);
        SmartDashboard.putNumber("Set Point", m_setPoint);
        SmartDashboard.putNumber("Set Point - Encoder", m_setPoint+m_neoPassEncoder.getPosition());
        SmartDashboard.putNumber("Speed", m_shooterMasterEncoder.getVelocity());

        switch(m_state.state()){
            case GRAB_CELL:


            if(m_armState == false || m_ballCount == 5){
                m_state.setState(State.WAITING);
            }
            if(m_armDeepStorage == false){
                m_timer = m_timer + 20;
            }else{
                m_timer = 1000;
            }

            m_neoIntake.set(0.25);
            if(m_ballCount == 4 && !m_intakeSensor.get() && m_timer > 499){
                m_ballCount = m_ballCount + 1;
                m_state.setState(State.WAITING);
                m_neoIntake.set(0);
            }
            else if(m_intakeSensor.get() == false && m_ballCount < 5 && m_timer > 499){
                m_state.setState(State.WAITING);
                m_neoIntake.set(0);
                m_setPoint = m_setPoint + m_index_distance;
                m_ballCount = m_ballCount + 1; 
                
            }

            SmartDashboard.putString("State", "Grab Cell");

            break;
          
            case WAITING:

                m_timer = 0;
                if(m_armState == true && m_ballCount <= 4){
                    m_neoIntake.set(0.25);
                }else{
                    m_neoIntake.set(0);
                }

                if(m_setPoint+m_neoPassEncoder.getPosition() < 1 && m_armState && m_ballCount != 5){
                    m_state.setState(State.GRAB_CELL);
                }

                SmartDashboard.putString("State", "Waiting");

                break;
            
            case EJECT:
                
                m_shooterSpeed = 1000;
                m_passThroughSpeed = 1;
                m_timeCount = 1000;
                m_state.setState(State.SCORE);    

                SmartDashboard.putString("State", "Eject");
                            
                break;
            
            case SHOOT:
                m_shooterSpeed = 4275;
                m_passThroughSpeed = .4;
                m_timeCount = 3000;
                if(m_shooterMasterEncoder.getVelocity() > 4100){
                m_state.setState(State.SCORE);
                }

                m_pidControllerShooter1.setReference(m_shooterSpeed, ControlType.kSmartVelocity);
                m_pidControllerShooter2.setReference(m_shooterSpeed, ControlType.kSmartVelocity);
                SmartDashboard.putString("State", "Shoot");

                break;
            
            case SCORE:
                m_timer = m_timer + 20;

                m_pidControllerShooter1.setReference(m_shooterSpeed, ControlType.kSmartVelocity);
                m_pidControllerShooter2.setReference(m_shooterSpeed, ControlType.kSmartVelocity);
                m_neoIntake.set(.25);
                m_passState = false;
                

                if(!m_shooterSensor.get()){
                    m_timer = 0;
                }

                if(m_timer > m_timeCount){
                    m_passState = true;
                    reset();
                    m_pidControllerShooter1.setReference(0, ControlType.kSmartVelocity);
                    m_pidControllerShooter2.setReference(0, ControlType.kSmartVelocity);
                    m_timer = 0;
                    m_ballCount = 0;
                    m_state.setState(State.WAITING);

                }

                SmartDashboard.putString("State", "Score");

                break;
            
            default:

                m_armState = false;
                m_neoIntake.set(0);

                SmartDashboard.putString("State", "Default");

                break;
        }

        setMode(m_passState, -m_passThroughSpeed);
        
       if(m_armState){
            m_arm.set(Value.kForward);
        }else{
            m_arm.set(Value.kReverse);
        } 
        
    }

    public void reset(){
        m_neoPassEncoder.setPosition(0);
        m_setPoint = 0;
    }
    public void passthroughReset(){
        m_state.setState(State.WAITING);
        reset();
        m_setPoint = 221.925652648;
        reset(); 
    }
    public void setArm(boolean state){
        if(m_ballCount < 5){
        m_armState = state;
        }

        if(m_ballCount == 5){
            m_armState = true;
        }
    }

    public void record(){
        m_armDeepStorage = m_armState;
    }

    public void setMode(boolean m_passState, double speed){
        if(m_passState){
            m_pidController.setReference(-m_setPoint, ControlType.kSmartMotion);
        }else{
            m_neoPassthrough.set(speed);
        }
    }
}