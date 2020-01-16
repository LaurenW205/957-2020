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

}