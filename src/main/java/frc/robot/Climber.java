<<<<<<< HEAD
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

=======
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

>>>>>>> 95b55def0721c748cf6653657e1f82811854f827
}