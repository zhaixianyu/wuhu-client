package com.zxy.wuhuclient.Utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.zxy.wuhuclient.MyThreadManager;
import com.zxy.wuhuclient.features_list.litematica_helper.LitematicaHelper;
import fi.dy.masa.litematica.Litematica;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Matrix4f;
//#if MC > 11802
import org.joml.Matrix4fStack;
//#endif

//#if MC > 12104
//#if MC < 12106
//$$ import com.mojang.blaze3d.buffers.BufferUsage;
//#endif
import com.mojang.blaze3d.vertex.VertexFormat;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
//#endif

//#if MC == 12105
//$$ import net.minecraft.client.renderer.FogParameters;
//#endif
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.zxy.wuhuclient.Utils.ZxyUtils.searchBlockId;
import static com.zxy.wuhuclient.Utils.ZxyUtils.searchBlockThread;
import static com.zxy.wuhuclient.WuHuClientMod.client;
import static com.zxy.wuhuclient.config.Configs.LITEMATICA_HELPER;
import static com.zxy.wuhuclient.config.Configs.SEARCH_BLOCK_LIMIT;
import static fi.dy.masa.malilib.render.RenderUtils.*;
import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR;

public class HighlightBlockRenderer implements IRenderer {
    public static HighlightBlockRenderer instance = new HighlightBlockRenderer();
    public record HighlightTheProject(ConfigColor color4f, Set<BlockPos> pos){}
    public static Map<String,HighlightTheProject> highlightTheProjectMap = new ConcurrentHashMap<>();
    public static String threadName = "wuhuRenderThread";
    public static boolean shaderIng = false;
    public static void createHighlightBlockList(String id,ConfigColor color4f){
        if (highlightTheProjectMap.get(id) == null) {
            highlightTheProjectMap.put(id,new HighlightTheProject(color4f,new LinkedHashSet <>()));
        }
    }
    public static Set<BlockPos> getHighlightBlockPosList(String id){
        if(highlightTheProjectMap.get(id) != null){
            return highlightTheProjectMap.get(id).pos();
        }
        return null;
    }
    public static List<String> clearList = new CopyOnWriteArrayList<>();
    public static void clear(String id){
        if (!clearList.contains(id)) clearList.add(id);
    }
    public static Map<String,Set<BlockPos>> setMap = new HashMap<>();
//    public static Map<String,Set<BlockPos>> setMap = new HashMap<>();
    public static void setPos(String id,Set<BlockPos> posSet){
        HighlightTheProject highlightTheProject = highlightTheProjectMap.get(id);
        if (highlightTheProject != null && posSet != null) {
            setMap.put(id,posSet);
        }
    }
    public static Method method;



    //#if MC > 12004
    public void highlightBlock(Matrix4f matrices, Color4f color4f, Set<BlockPos> posSet){
    //#else
    //$$ public void highlightBlock(PoseStack matrices ,Color4f color4f, Set<BlockPos> posSet){
    //#endif

//        for (BlockPos pos : posSet) {
//            renderAreaSides(pos,pos,color4f,matrices,client);
//        }
        //#if MC <= 12104
        //$$ RenderSystem.disableDepthTest();
        //#endif

        //#if MC > 12104
            //#if MC > 12105
            RenderSystem.setShaderFog(RenderSystem.getShaderFog());
            //#else
            //$$ RenderSystem.setShaderFog(FogParameters.NO_FOG);
            //#endif
        //#else
        //$$ RenderSystem.enableBlend();
        //$$ RenderSystem.disableCull();
        //#endif


        //#if MC >= 12101
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //#endif

        Tesselator tessellator = Tesselator.getInstance();

        //#if MC > 12006
            //#if MC > 12104
                //#if MC == 12105
                //$$ RenderContext ctx = new RenderContext(MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_DEPTH_MASK, BufferUsage.STATIC_WRITE);
                //#else
                RenderContext ctx = new RenderContext(() -> threadName ,MaLiLibPipelines.POSITION_COLOR_TRANSLUCENT_DEPTH_MASK);
                //#endif
            BufferBuilder buffer = ctx.getBuilder();
            //#else
            //$$ BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            //#endif
        MeshData meshData;
        //#else
        //$$ BufferBuilder buffer = tessellator.getBuilder();
        //$$ buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        //#endif
        for (BlockPos pos : posSet) {
            //#if MC >= 12105
            RenderUtils.renderAreaSidesBatched(pos, pos, color4f, 0.002, buffer);
            //#else
            //$$ fi.dy.masa.litematica.render.RenderUtils.renderAreaSidesBatched(pos, pos, color4f, 0.002, buffer, client);
            //#endif
        }

        try
        {
            if(buffer != null){
                //#if MC > 12006
                meshData = buffer.buildOrThrow();
                    //#if MC > 12104
                    ctx.upload(meshData, true);
                    ctx.startResorting(meshData, ctx.createVertexSorter(fi.dy.masa.malilib.render.RenderUtils.camPos()));
                    meshData.close();
                    ctx.drawPost();
                    //#else
                    //$$ BufferUploader.drawWithShader(meshData);
                    //$$ meshData.close();
                    //#endif
                //#else
                //$$ tessellator.end();
                //#endif
            }
        }
        catch (Exception e)
        {
//            Litematica.logger.error("renderAreaSides: Failed to draw Area Selection box (Error: {})", e.getLocalizedMessage());
        }

        //#if MC > 12104
        RenderSystem.setShaderFog(RenderSystem.getShaderFog());
        //#else
        //$$ RenderSystem.enableCull();
        //$$ RenderSystem.disableBlend();
        //#endif

        //#if MC <= 12104
        //$$ RenderSystem.enableDepthTest();
        //#endif

//        fi.dy.masa.litematica.render.RenderUtils.renderAreaSides(pos, pos, color4f, matrices, client);
    }

    public static void init(){
        //如果不注册无法渲染，
        RenderEventHandler.getInstance().registerWorldLastRenderer(instance);
        MyThreadManager.createThread(threadName,new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()){
                try {
                    Thread.sleep(80);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                //投影材料助手、搜索方块渲染
                searchBlockThread();

            }
        }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client1) -> {
            for (Map.Entry<String, HighlightTheProject> stringHighlightTheProjectEntry : highlightTheProjectMap.entrySet()) {
                stringHighlightTheProjectEntry.getValue().pos.clear();
            }
        });
    }

    @Override
    //#if MC > 12004
    public void onRenderWorldLast(Matrix4f matrices, Matrix4f projMatrix){
    //#else
    //$$ public void onRenderWorldLast(PoseStack matrices, Matrix4f projMatrix){
    //#endif
        //更改渲染
        setMap.forEach((k,v) -> {
            HighlightTheProject highlightTheProject = highlightTheProjectMap.get(k);
            if(highlightTheProject != null){
                highlightTheProject.pos.clear();
                highlightTheProject.pos.addAll(v);
            }
        });
        setMap.clear();

        for (String string : clearList) {
            HighlightTheProject highlightTheProject = highlightTheProjectMap.get(string);
            if (highlightTheProject != null) {
                highlightTheProject.pos.clear();
            }
        }
        clearList.clear();

        shaderIng = true;
        highlightTheProjectMap.forEach((key, value) -> {
            if (!LITEMATICA_HELPER.getBooleanValue() && LitematicaHelper.instance.litematicaHelper.equals(key)) return;
            Color4f color = value.color4f.getColor();
            highlightBlock(matrices, color, value.pos);

        });
        shaderIng = false;
    }
}