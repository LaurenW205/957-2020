package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.networktables.NetworkTableInstance;
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

    // CAN 9 = Right, CAN 10 = Left
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
	double maxAcc = 5700;

    private static PowerCell m_powercell = null;

    double m_index_distance = 34;

    boolean m_armState = false;
    boolean m_passState = true;
    boolean m_armDeepStorage = false;

    double k_a = 532;
    double k_b = -1084;
    double k_c = 4944;

    /**PowerCell contructor */
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
        m_neoPassthrough.setInverted(true);
        m_neoPassthrough2.setInverted(true);

        m_pidController.setP(0.0001);
		m_pidController.setI(kI);
		m_pidController.setD(0.00001);
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
		m_pidControllerShooter1.setSmartMotionMaxAccel(5000,0);
        m_pidControllerShooter1.setOutputRange(-1, 1);	

        m_pidControllerShooter2.setP(kP);
		m_pidControllerShooter2.setI(kI);
		m_pidControllerShooter2.setD(kD);
		m_pidControllerShooter2.setIZone(kIz);
		m_pidControllerShooter2.setFF(kFF);
		m_pidControllerShooter2.setSmartMotionMaxVelocity(maxVel, 0);
		m_pidControllerShooter2.setSmartMotionMinOutputVelocity(0, 0);
		m_pidControllerShooter2.setSmartMotionMaxAccel(5000,0);
        m_pidControllerShooter2.setOutputRange(-1, 1);	
        
        m_shooterMaster.setSmartCurrentLimit(40, 40);
        m_shooterSlave.setSmartCurrentLimit(40, 40);
        m_neoIntake.setSmartCurrentLimit(30, 30);
        m_neoPassthrough.setSmartCurrentLimit(40, 40);
        m_neoPassthrough2.setSmartCurrentLimit(40, 40);
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

    public double ta(){
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);
    }

    public void run(){

        SmartDashboard.putBoolean("Start of Pass, Sensor 1", m_intakeSensor.get());
        SmartDashboard.putBoolean("Shooter Sensor 3", m_shooterSensor.get());
        SmartDashboard.putNumber("Ball Count", m_ballCount);
        SmartDashboard.putNumber("Set Point", m_setPoint);
        SmartDashboard.putNumber("Set Point - Encoder", m_setPoint+m_neoPassEncoder.getPosition());
        SmartDashboard.putNumber("Speed", m_shooterMasterEncoder.getVelocity());

        switch(m_state.state()){
        /**Intake powercell */ 
            case GRAB_CELL:

            //m_pidControllerShooter1.setReference(2500, ControlType.kSmartVelocity);
            //m_pidControllerShooter2.setReference(2500, ControlType.kSmartVelocity);

            m_passState = true;

            if(m_armState == false || m_ballCount == 5){
                m_state.setState(State.WAITING);
            }
            if(m_armDeepStorage == false){
                m_timer = m_timer + 20;
            }else{
                m_timer = 1000;
            }

            m_neoIntake.set(0.50);
            if(m_ballCount == 4 && !m_intakeSensor.get() && m_timer > 499){
                m_ballCount = m_ballCount + 1;
                m_state.setState(State.WAITING);
                m_neoIntake.set(0);
            }
            else if(m_intakeSensor.get() == false && m_ballCount < 5 && m_timer > 499){
                m_state.setState(State.WAITING);
                m_neoIntake.set(0);
                if(m_ballCount == 3){
                    m_setPoint = m_setPoint + m_index_distance*0.75;
                }else{
                m_setPoint = m_setPoint + m_index_distance;
                }
                m_ballCount = m_ballCount + 1; 
                
                
            }

            SmartDashboard.putString("State", "Grab Cell");

            break;
        
            case WAITING:

            //m_pidControllerShooter1.setReference(2500, ControlType.kSmartVelocity);
            //m_pidControllerShooter2.setReference(2500, ControlType.kSmartVelocity);

                m_passState = true;

                m_timer = 0;
                if(m_armState == true && m_ballCount <= 4){
                    m_neoIntake.set(0.5);
                }else{
                    m_neoIntake.set(0);
                }

                if(m_setPoint-m_neoPassEncoder.getPosition() < 1 && m_armState && m_ballCount != 5){
                    m_state.setState(State.GRAB_CELL);
                    m_armDeepStorage = m_armState;
                }

                SmartDashboard.putString("State", "Waiting");

                break;
       
         /**Dumping for low goal */
            case EJECT:
             
                m_shooterSpeed = 500;
                m_passThroughSpeed = 1;
                m_timeCount = 1000;
                m_state.setState(State.SCORE);    

                SmartDashboard.putString("State", "Eject");
                            
                break;
            
        /**Shooting for high goal*/ 
            case SHOOT:

                m_shooterSpeed = 4500;
                //k_a*ta()*ta()+k_b*ta()+k_c
                //System.out.println(m_shooterSpeed);
                m_passThroughSpeed = .3;
                m_timeCount = 3500;
                if(m_shooterMasterEncoder.getVelocity() > m_shooterSpeed-50){
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
                    m_pidControllerShooter1.setReference(2500, ControlType.kSmartVelocity);
                    m_pidControllerShooter2.setReference(2500, ControlType.kSmartVelocity);
                    m_timer = 0;
                    m_ballCount = 0;
                    m_state.setState(State.WAITING);

                }

                SmartDashboard.putString("State", "Score");

                break;

        /**Reverses intake and passthrough */
            case REVERSE_ALL:

                m_passState = false;
                m_neoIntake.set(-0.25);
                m_passThroughSpeed = -0.3;
                
                break;

        /**Reverses only intake */
            case REVERSE_INTAKE:

                m_passState = false;
                m_neoIntake.set(-0.25);
                m_passThroughSpeed = 0;

                break; 
            
            default:

                m_armState = false;
                m_neoIntake.set(0);

                SmartDashboard.putString("State", "Default");

                break;
        }

        setMode(m_passState, m_passThroughSpeed);
        
       if(m_armState){
            m_arm.set(Value.kForward);
        }else{
            m_arm.set(Value.kReverse);
        } 

        SmartDashboard.putNumber("lrpm", m_shooterMasterEncoder.getVelocity());
        SmartDashboard.putNumber("rrpn", m_shooterSlave.getEncoder().getVelocity());
        
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
            m_pidController.setReference(m_setPoint, ControlType.kSmartMotion);
        }else{
            m_neoPassthrough.set(speed);
        }
    }
}