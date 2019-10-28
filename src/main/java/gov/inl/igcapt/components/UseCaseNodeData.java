package gov.inl.igcapt.components;

import java.util.HashMap;
import java.util.UUID;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 */
public class UseCaseNodeData {

    private String text;
    private boolean checked;
    private HashMap<UUID, Integer> useCaseApplyData;

    public HashMap<UUID, Integer> getUseCaseApplyData() {
        return useCaseApplyData;
    }

    public void setUseCaseApplyData(HashMap<UUID, Integer> useCaseApplyData) {
        this.useCaseApplyData = useCaseApplyData;
    }
    private Object clickObject = null;

    public Object getClickObject() {
        return clickObject;
    }

    public void setClickObject(Object clickObject) {
        this.clickObject = clickObject;
    }

    public UseCaseNodeData(final String text, final boolean checked) {
        this.text = text;
        this.checked = checked;
    }

    public UseCaseNodeData(UseCaseNodeData useCaseNodeData) {
        this.text = useCaseNodeData.text;
        this.checked = useCaseNodeData.checked;
        this.clickObject = useCaseNodeData.clickObject;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(final boolean checked) {
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + text + "/" + checked + "]";
    }
}
