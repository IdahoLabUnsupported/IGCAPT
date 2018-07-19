/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package igcapt.inl.components;

import edu.uci.ics.jung.visualization.LayeredIcon;
import igcapt.inl.components.generated.SgCollapseIntoData;
import igcapt.inl.components.generated.SgComponentData;
import igcapt.inl.components.generated.SgComponentDataElement;
import igcapt.inl.components.generated.SgComponentUseCaseData;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *(c) 2018 BATTELLE ENERGY ALLIANCE, LLC
 *ALL RIGHTS RESERVED 
 *
 * @author FRAZJD
 */
public class SgComponent {

    public SgComponent(SgComponentData sgComponentData) {
        _name = sgComponentData.getName();
        _iconPath = sgComponentData.getIconPath();
        _typeUuid = UUID.fromString(sgComponentData.getUuid());
        _isPassthrough = sgComponentData.isPassthrough();
        _isAggregate = sgComponentData.isAggregate();
        SgCollapseIntoData sgCollapseIntoData = sgComponentData.getSgCollapseIntoData();
        
        if (sgCollapseIntoData != null) {
            _collapseIntoTypeUuids = (ArrayList<String>) sgCollapseIntoData.getCollapseInto();
        }
        else {
            _collapseIntoTypeUuids = null;
        }

        if (_iconPath != null && !_iconPath.isEmpty()) {
            BufferedImage img = null;
            try {
                File iconFile = new File(_iconPath);
                img = ImageIO.read(new FileInputStream(iconFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageIcon newicon = new ImageIcon(img);
            _icon = new SgLayeredIcon(newicon.getImage()); // LayeredIcon used for checking an icon
        }
        
        SgComponentUseCaseData sgComponentUseCaseData = sgComponentData.getSgUseCaseData();
        
        if (sgComponentUseCaseData != null) {
            ArrayList<SgComponentDataElement> useCaseElements = new ArrayList<>(sgComponentUseCaseData.getSgDataElement());

            for (SgComponentDataElement element : useCaseElements) {
                _useCaseData.add(new SgUseCase(element));
            }
        }
    }

    private ArrayList<SgUseCase> _useCaseData = new ArrayList<>();

    public ArrayList<SgUseCase> getUseCaseData() {
        return _useCaseData;
    }

    public void setUseCaseData(ArrayList<SgUseCase> _useCaseData) {
        this._useCaseData = _useCaseData;
    }
    
    private ArrayList<String> _collapseIntoTypeUuids = null;
    
    public ArrayList<String> getCollapseIntoTypeUuids() {
        return _collapseIntoTypeUuids;
    }
    
    public void setCollapseIntoTypeUuids(ArrayList<String> collapseIntoUuids) {
        _collapseIntoTypeUuids = collapseIntoUuids;
    }
    
    public boolean canCollapseInto(SgComponent collapseComponent) {
        boolean returnval = false;

        if (_collapseIntoTypeUuids != null) {
            for (String uuid : _collapseIntoTypeUuids) {
                if (uuid.equals(collapseComponent.getTypeUuid().toString())) {
                    returnval = true;
                    break;
                }
            }
        }

        return returnval;
    }
    
    private boolean _isPassthrough = false;
    
    private Boolean _isAggregate = false;

    public boolean isAggregate() {
        return _isAggregate;
    }
    
    /**
     * Get the value of isPassthrough
     *
     * @return the value of isPassthrough
     */
    public boolean getIsPassthrough() {
        return _isPassthrough;
    }

    /**
     * Set the value of isPassthrough
     *
     * @param isPassthrough new value of isPassthrough
     */
    public void setIsPassthrough(boolean isPassthrough) {
        this._isPassthrough = isPassthrough;
    }

    private UUID _typeUuid = null;

    public UUID getTypeUuid() {
        return _typeUuid;
    }
    
    public void setTypeUuid(String uuidStr) {
        _typeUuid = UUID.fromString(uuidStr);
    }
    
    public void setTypeUuid(UUID uuid) {
        _typeUuid = uuid;
    }
    
    private String _name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this._name = name;
    }

    private String _iconPath;

    /**
     * Get the value of _iconPath
     *
     * @return the value of _iconPath
     */
    public String getIconPath() {
        return _iconPath;
    }

    /**
     * Set the value of _iconPath
     *
     * @param _iconPath new value of _iconPath
     */
    public void setIconPath(String _iconPath) {
        this._iconPath = _iconPath;
    }
    
    private Icon _icon;

    /**
     * Get the value of _icon
     *
     * @return the value of _icon
     */
    public Icon getIcon() {
        return _icon;
    }

    /**
     * Set the value of _icon
     *
     * @param _icon new value of _icon
     */
    public void setIcon(Icon _icon) {
        this._icon = _icon;
    }

    /**
     * Get the BufferedImage associated with this icon.  If the icon is not a LayeredIcon or
     * the Image is not a BufferedImage, then we return null.
     * @return The BufferedImage associated with the Icon.
     */
    public BufferedImage getIconImage() {
        
        BufferedImage returnval = null;
        
        if (_icon != null &&
            _icon instanceof LayeredIcon &&
            ((LayeredIcon)_icon).getImage() instanceof BufferedImage) {
            
            returnval = (BufferedImage)((LayeredIcon)_icon).getImage();
        }
        
        return returnval;
    }
}
