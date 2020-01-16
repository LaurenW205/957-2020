package frc.robot;

public class Drivetrain{

    private static Drivetrain m_drivetrain = null;

    private Drivetrain(){

    }

    public static Drivetrain getInstance(){
        if(m_drivetrain == null)
        m_drivetrain = new Drivetrain();

        return m_drivetrain;
    }
}