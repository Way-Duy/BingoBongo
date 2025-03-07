/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package export.json;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.snagtype.modbingo.ModBingoLog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;


/**
 * handles the exporting including processing, transformation and persisting the information
 *
 * @author boijangle
 * @version 1.0
 * @since 1.0
 */
final class MinecraftItemJsonExporter implements export.json.Exporter
{
	private static final String ITEM_JSON_FILE_NAME = "items.json";
	private static final String MINIMAL_HEADER = "Mod:Item:MetaData, Localized Name";
	private static final String EXPORT_SUCCESSFUL_MESSAGE = "Exported successfully %d items into %s";
	private static final String EXPORT_UNSUCCESSFUL_MESSAGE = "Exporting was unsuccessful.";

	@Nonnull
	private final File exportDirectory;
	@Nonnull
	private final IForgeRegistry<Item> itemRegistry;

	/**
	 * @param exportDirectory directory of the resulting export file. Non-null required.
	 * @param itemRegistry the registry with minecraft items. Needs to be populated at that time, thus the exporting can
	 * only happen in init (pre-init is the
	 * phase when all items are determined)
	 */
	MinecraftItemJsonExporter(@Nonnull final File exportDirectory, @Nonnull final IForgeRegistry<Item> itemRegistry )
	{
		this.exportDirectory = Preconditions.checkNotNull( exportDirectory );
		Preconditions.checkArgument( !exportDirectory.isFile() );
		this.itemRegistry = Preconditions.checkNotNull( itemRegistry );

	}

	@Override
	public void export() {
		final Iterable<Item> items = this.itemRegistry;
		final List<Item> itemList = Lists.newArrayList(items);
		JsonObject itemsJson = convertItemsToJSON(items);

		final File file = new File(this.exportDirectory, ITEM_JSON_FILE_NAME);

		try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")))) {
			FileUtils.forceMkdir(this.exportDirectory);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			writer.write(gson.toJson(itemsJson));
			writer.flush();

			ModBingoLog.info(EXPORT_SUCCESSFUL_MESSAGE, itemList.size(), ITEM_JSON_FILE_NAME);
		} catch (final IOException e) {
			ModBingoLog.warn(EXPORT_UNSUCCESSFUL_MESSAGE);
		}
	}

	/**
	 * Convert all items into JSON format with name and each possible metadata value,
	 * 	skipping any items that cannot be gotten through the creative tabs
	 * @param items minecraft items to convert
	 * @return completed JSON object with all items
	 */
	JsonObject convertItemsToJSON(Iterable<Item> items) {
		JsonObject itemsJson = new JsonObject();
		JsonObject currMod, currItem;
		JsonArray currItemList, currMeta, modNames = new JsonArray();
		String currModName, currItemName;
		int curItemCount = 0;
		itemsJson.add("modnames", modNames);

		// Iterate over every item
		for (Item i : items) {

			// prepare item json and array for the meta numbers
			currItem = new JsonObject();
			currMeta = new JsonArray();
			currItemName = ForgeRegistries.ITEMS.getKey(i).toString();
			currModName = currItemName.split(":")[0];

			// check if mod has already been added to the json before
			if (!itemsJson.has(currModName)) {
				modNames.add(currModName);
				currMod = new JsonObject();
				currMod.addProperty("count", 0);
				currMod.add("items", new JsonArray());
				itemsJson.add(currModName, currMod);
			}

			// get all of the json objects needed for an item's placement
			currMod = (JsonObject) itemsJson.get(currModName);
			currItemList = (JsonArray) currMod.get("items");
			curItemCount = currMod.get("count").getAsInt();

			// check if the item has extra metadata values
			if (i.getHasSubtypes()) {
				CreativeTabs creativeTab = i.getCreativeTab();
				NonNullList<ItemStack> stacks = NonNullList.create();

				// skip any items that cant be retrieved out of the creative tab
				if (creativeTab == null) {
					continue;
				}

				// parse each metadata item separately
				i.getSubItems(creativeTab, stacks);
				for (ItemStack sItem : stacks) {
					currMeta.add(sItem.getItemDamage());
				}
			}

			// add the item's name and meta values
			currItem.addProperty("name", currItemName);
			currItem.add("meta", currMeta);

			// count the item and replace the count
			curItemCount++;
			currMod.addProperty("count", curItemCount);
			currItemList.add(currItem);
		}
		return itemsJson;
	}
}

