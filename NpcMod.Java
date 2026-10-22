package com.example.npcmod;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
@Mod(modid = "npcmod", name = "NPC Mod", version = "1.0", acceptedMinecraftVersions = "[1.8.9]")
public class NpcMod {

    @Mod.Instance("npcmod")
    public static NpcMod instance;

    @SidedProxy(
        clientSide = "com.example.npcmod.NpcMod$ClientProxy",
        serverSide = "com.example.npcmod.NpcMod$CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    public static class CommonProxy {
        public void preInit() {
            int id = EntityRegistry.findGlobalUniqueEntityId();
            EntityRegistry.registerGlobalEntityID(EntityNpc.class, "Npc", id);
            EntityRegistry.registerModEntity(EntityNpc.class, "Npc", id, NpcMod.instance, 64, 1, true);
            EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, 0x3B2A1A, 0xD4A373));
        }
    }

    public static class ClientProxy extends CommonProxy {
        @Override
        public void preInit() {
            super.preInit();
            RenderingRegistry.registerEntityRenderingHandler(EntityNpc.class, new IRenderFactory<EntityNpc>() {
                @Override
                public Render<? super EntityNpc> createRenderFor(RenderManager manager) {
                    return new RenderNpc(manager);
                }
            });
        }
    }
    public static class EntityNpc extends EntityCreature {

        public EntityNpc(World worldIn) {
            super(worldIn);
            this.setSize(0.6F, 1.8F);
            ((PathNavigateGround) this.getNavigator()).setAvoidsWater(true);
            this.tasks.addTask(0, new EntityAISwimming(this));
            this.tasks.addTask(1, new EntityAIWander(this, 0.8D));
            this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
            this.tasks.addTask(3, new EntityAILookIdle(this));
            this.setCustomNameTag("NPC");
            this.setAlwaysRenderNameTag(true);
        }

        @Override
        protected void applyEntityAttributes() {
            super.applyEntityAttributes();
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        }

        @Override
        protected boolean interact(EntityPlayer player) {
            if (!this.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentText("<NPC> Ola, " + player.getName() + "!"));
            }
            return true;
        }

        @Override
        protected boolean canDespawn() {
            return false;
        }
    }
    public static class RenderNpc extends RenderBiped<EntityNpc> {
        private static final ResourceLocation TEXTURE = new ResourceLocation("npcmod", "textures/entity/npc.png");

        public RenderNpc(RenderManager manager) {
            super(manager, new ModelPlayer(0.0F, false), 0.5F);
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityNpc entity) {
            return TEXTURE;
        }
    }
}
