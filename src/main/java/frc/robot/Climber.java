package frc.robot;

public class Climber{

    private static Climber m_climber = null;

    private Climber(){

    }

    public static Climber getInstance(){
        if(m_climber == null)
        m_climber = new Climber();

        return m_climber;
    }


}