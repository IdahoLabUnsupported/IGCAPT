package gov.inl.igcapt.components;

public class CostAnalysisEntry {
    private String m_componentName;
    private int m_quantity;
    private int m_capex_unit_projected;
    private int m_capex_unit_actual;
    private int m_opex_unit_projected;
    private int m_opex_unit_actual;

    public CostAnalysisEntry(String componentName){
        m_componentName = componentName;
        m_quantity = 0;
    }

    public void addEntry(){
        m_quantity++;
    }

    public void setCapexProjected(int value){
        m_capex_unit_projected = value;
    }

    public void setCapexActual(int value){
        m_capex_unit_actual = value;
    }

    public void setOpexProjected(int value){
        m_opex_unit_projected = value;
    }

    public void setOpexActual(int value){
        m_opex_unit_actual = value;
    }

    public int getQuantity(){
        return m_quantity;
    }

    public int getCapexUnitProjected(){
        return m_capex_unit_projected;
    }

    public int getCapexUnitActual(){
        return m_capex_unit_actual;
    }

    public int getOpexUnitProjected(){
        return m_opex_unit_projected;
    }

    public int getOpexUnitActual(){
        return m_opex_unit_actual;
    }

    public int getCapexProjectedTotal(){
        return m_quantity * m_capex_unit_projected;
    }

    public int getCapexActualTotal(){
        return m_quantity * m_capex_unit_actual;
    }

    public int getOpexProjectedTotal(){
        return m_quantity * m_opex_unit_projected;
    }

    public int getOpexActualTotal(){
        return m_quantity * m_opex_unit_actual;
    }
}
