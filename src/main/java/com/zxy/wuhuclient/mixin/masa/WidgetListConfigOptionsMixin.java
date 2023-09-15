package com.zxy.wuhuclient.mixin.masa;

import com.zxy.wuhuclient.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = WidgetListConfigOptions.class)
public class WidgetListConfigOptionsMixin {
    @Inject(at = @At("RETURN"),method = "getEntryStringsForFilter(Lfi/dy/masa/malilib/gui/GuiConfigsBase$ConfigOptionWrapper;)Ljava/util/List;",remap = false)
    public void getEntryString(GuiConfigsBase.ConfigOptionWrapper entry, CallbackInfoReturnable<List<String>> cir){
        IConfigBase config = entry.getConfig();
        if(config == null || !Configs.PINYIN.getBooleanValue()) return;
        List list = cir.getReturnValue();
        list.add(config.getName());
        if(config.getPrettyName() != null)list.add(config.getPrettyName());
    }
}
