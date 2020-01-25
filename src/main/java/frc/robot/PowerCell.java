<<<<<<< HEAD
package frc.robot;

public class PowerCell{

    private static PowerCell m_powercell = null;

    private PowerCell(){

    }

    public static PowerCell getInstance(){
        if(m_powercell == null)
        m_powercell = new PowerCell();

        return m_powercell;
    }

=======
package frc.robot;

public class PowerCell{

    private static PowerCell m_powercell = null;

    private PowerCell(){

    }

    public static PowerCell getInstance(){
        if(m_powercell == null)
        m_powercell = new PowerCell();

        return m_powercell;
    }

>>>>>>> 95b55def0721c748cf6653657e1f82811854f827
}