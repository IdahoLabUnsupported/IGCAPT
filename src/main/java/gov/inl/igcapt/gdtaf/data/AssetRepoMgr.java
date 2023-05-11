package gov.inl.igcapt.gdtaf.data;

import gov.inl.igcapt.gdtaf.model.Asset;
import java.util.HashMap;
import java.util.Map;


/**
 * AssetRepoMgr is a manager/accessor class for GTDAF
 * Asset Repository data.  Data is stored in a map keyed
 * on asset uuid.  The AssetRepoMgr class must be initialized
 * once the GDTAF Scenario has been loaded into memory
 */
public class AssetRepoMgr {
    private static final AssetRepoMgr instance = new AssetRepoMgr();
    private Map<String, Asset> m_assetMap = new HashMap<String, Asset>();
    private AssetRepoMgr(){
    }

    /**
     * Singleton accessor method
     * @return AssetRepoMgr
     */
    public static AssetRepoMgr getInstance(){
        return instance;
    }

    /**
     * populates the AssetRepoMgr Map with data from the GDTAF
     * Scenario
     * @param gdtaf
     */
    public void initRepo(gov.inl.igcapt.gdtaf.model.GDTAF gdtaf){
        var assetList = gdtaf.getAssetRepo().getAsset();
        for (var asset:assetList) {
            m_assetMap.put(asset.getUUID(), asset);
        }
    }

    /**
     * Asset Getter by uuid string
     * @param uuid
     * @return Asset
     */
    public Asset getAsset(String uuid){
        return m_assetMap.get(uuid);
    }

    /**
     * returns number of Assets in the map
     * @return int
     */
    public int count(){return m_assetMap.size();}

}
