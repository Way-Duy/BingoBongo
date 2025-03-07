package com.snagtype.modbingo;

import com.google.common.base.Preconditions;
import export.json.ExportConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nonnull;

public final class BingoAdvancementConfig implements AdvancementConfig{

    private static final String GENERAL_CATEGORY = "general";

    private static final String FREE_SPACE_ENABLED_KEY = "isFreeSpaceEnabled";
    private static final boolean FREE_SPACE_ENABLED_DEFAULT = true;
    private static final String FREE_SPACE_ENABLED_DESCRIPTION = "If true, Bingo middle space will be Free and it will generate 1 less achievement.";

    private static boolean freeSpaceEnabled;
    private final Configuration config;

    public BingoAdvancementConfig( @Nonnull final Configuration config ) {
        this.config = Preconditions.checkNotNull( config );
        this.freeSpaceEnabled = this.config.getBoolean( FREE_SPACE_ENABLED_KEY, GENERAL_CATEGORY, FREE_SPACE_ENABLED_DEFAULT,
                FREE_SPACE_ENABLED_DESCRIPTION );
    }

    @Override
    public boolean isFreeSpaceEnabled()
    {
        return this.freeSpaceEnabled;
    }
    public void toggleFreeSpace(){
        if(freeSpaceEnabled)
         freeSpaceEnabled = false;
        else
            freeSpaceEnabled = true;
    }

}
/*
public final class ForgeExportConfig implements ExportConfig
{
    private static final String GENERAL_CATEGORY = "general";
    private static final String CACHE_CATEGORY = "cache";

    private static final String EXPORT_ITEM_NAMES_KEY = "exportItemNames";
    private static final boolean EXPORT_ITEM_NAMES_DEFAULT = true;
    private static final String EXPORT_ITEM_NAMES_DESCRIPTION = "If true, all registered items will be exported containing the internal minecraft name and the localized name to actually find the item you are using. This also contains the item representation of the blocks, but are missing items, which are too much to display e.g. FMP.";

    private static final String ENABLE_FORCE_REFRESH_KEY = "enableForceRefresh";
    private static final boolean ENABLE_FORCE_REFRESH_DEFAULT = false;
    private static final String ENABLE_FORCE_REFRESH_DESCRIPTION = "If true, the Json exporting will always happen. This will not use the cache to reduce the computation.";

    private static final String ENABLE_CACHE_KEY = "enableCache";
    private static final boolean ENABLE_CACHE_DEFAULT = true;
    private static final String ENABLE_CACHE_DESCRIPTION = "Caching can save processing time, if there are a lot of items.";

    private static final String ENABLE_ADDITIONAL_INFO_KEY = "enableAdditionalInfo";
    private static final boolean ENABLE_ADDITIONAL_INFO_DEFAULT = false;
    private static final String ENABLE_ADDITIONAL_INFO_DESCRIPTION = "Will output more detailed information into the Json like corresponding items";

    private static final String DIGEST_KEY = "digest";
    private static final String DIGEST_DEFAULT = "";
    private static final String DIGEST_DESCRIPTION = "Digest of all the mods and versions to check if a re-export of the item names is required.";

    private final boolean exportItemNamesEnabled;
    private final boolean cacheEnabled;
    private final boolean forceRefreshEnabled;
    private final boolean additionalInformationEnabled;
    private final String cache;
    private final Configuration config;


    public ForgeExportConfig( @Nonnull final Configuration config )
    {
        this.config = Preconditions.checkNotNull( config );

        this.exportItemNamesEnabled = this.config.getBoolean( EXPORT_ITEM_NAMES_KEY, GENERAL_CATEGORY, EXPORT_ITEM_NAMES_DEFAULT,
                EXPORT_ITEM_NAMES_DESCRIPTION );
        this.cacheEnabled = this.config.getBoolean( ENABLE_CACHE_KEY, CACHE_CATEGORY, ENABLE_CACHE_DEFAULT, ENABLE_CACHE_DESCRIPTION );
        this.additionalInformationEnabled = this.config.getBoolean( ENABLE_ADDITIONAL_INFO_KEY, GENERAL_CATEGORY, ENABLE_ADDITIONAL_INFO_DEFAULT,
                ENABLE_ADDITIONAL_INFO_DESCRIPTION );
        this.cache = this.config.getString( DIGEST_KEY, CACHE_CATEGORY, DIGEST_DEFAULT, DIGEST_DESCRIPTION );
        this.forceRefreshEnabled = this.config.getBoolean( ENABLE_FORCE_REFRESH_KEY, GENERAL_CATEGORY, ENABLE_FORCE_REFRESH_DEFAULT,
                ENABLE_FORCE_REFRESH_DESCRIPTION );
    }

    @Override
    public boolean isExportingItemNamesEnabled()
    {
        return this.exportItemNamesEnabled;
    }

    @Override
    public boolean isCacheEnabled()
    {
        return this.cacheEnabled;
    }

    @Override
    public boolean isForceRefreshEnabled()
    {
        return this.forceRefreshEnabled;
    }

    @Override
    public boolean isAdditionalInformationEnabled()
    {
        return this.additionalInformationEnabled;
    }

    @Override
    public String getCache()
    {
        return this.cache;
    }

    @Override
    public void setCache( @Nonnull final String digest )
    {
        final Property digestProperty = this.config.get( CACHE_CATEGORY, DIGEST_KEY, DIGEST_DEFAULT );
        digestProperty.set( digest );

        this.config.save();
    }

    @Override
    public void save()
    {
        this.config.save();
    }
}
 */
