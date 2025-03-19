package com.zxy.wuhuclient.mixin.masa.pinyin;

import com.zxy.wuhuclient.Utils.PinYinSearch;
import com.zxy.wuhuclient.config.Configs;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WidgetListBase.class)
public class WidgetListBaseMixin{
    @Inject(at = @At(value = "INVOKE",target = "Ljava/lang/String;contains(Ljava/lang/CharSequence;)Z"),method = "matchesFilter(Ljava/lang/String;Ljava/lang/String;)Z",remap = false, cancellable = true)
    public void matchesFilter(String entryString, String filterText, CallbackInfoReturnable<Boolean> cir){
        if (!Configs.PINYIN.getBooleanValue()) return;
        String translate = StringUtils.translate(entryString);
        if (PinYinSearch.hasPinYin(translate,filterText) || translate.contains(filterText)) {
            cir.setReturnValue(true);
        }
    }
}