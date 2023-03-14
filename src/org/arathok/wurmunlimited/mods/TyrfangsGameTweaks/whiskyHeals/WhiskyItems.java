package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals;

import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;

public class WhiskyItems {

    public static ItemTemplate gauze;
    public static int gauzeId;
    public static void registerGauze() throws IOException {
        gauze = new ItemTemplateBuilder("arathok.TGT.gauze").name("Gauze", "Gauze",
                        "A sterilizing bandage that slowly heals wounds and is practical as first aid kit.")
                .modelName("model.cloth.bolt.")
                .imageNumber((short) IconConstants.ICON_CLOTH_BOLT)
                .itemTypes(new short[]{
                        ItemTypes.ITEM_TYPE_NAMED,
                        ItemTypes.ITEM_TYPE_CLOTH,
                        ItemTypes.ITEM_TYPE_OWNER_MOVEABLE,
                        ItemTypes.ITEM_TYPE_COMBINE,

                })

                .decayTime(Long.MAX_VALUE)
                .dimensions(5, 5, 5)
                .weightGrams(1000)
                .behaviourType((short) 1) // ITEM
                .build();

        gauzeId = gauze.getTemplateId();

        CreationEntryCreator.createSimpleEntry(SkillList.ALCHEMY_NATURAL, ItemList.clothYard, ItemList.whisky,
                gauzeId, false, true, 0.0f, false, false, CreationCategories.WEAPONS);
    }
}
