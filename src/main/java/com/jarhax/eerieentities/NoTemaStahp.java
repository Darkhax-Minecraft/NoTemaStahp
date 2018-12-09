package com.jarhax.eerieentities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import com.rwtema.extrautils2.eventhandlers.SlimeSpawnHandler;
import com.rwtema.extrautils2.items.ItemLawSword;
import com.rwtema.extrautils2.utils.helpers.PlayerHelper;

@Mod(modid = "notemastahp", name = "NoTemaStahp", version = "@VERSION@", dependencies = "required-after:extrautils2", acceptableRemoteVersions = "*", certificateFingerprint = "@FINGERPRINT@")
public class NoTemaStahp {
    
    final Configuration config;
    
    public NoTemaStahp() {
    	
    	config = new Configuration("notemastahp");
    }
    
    @EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
    	
    	try {
    		
        	ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "listeners");
        	Set<Object> listenersToRemove = new HashSet<>();
        	
        	for (Object listener : listeners.keySet()) {
        		
        		Class<?> listenerClass = listener.getClass();
        		
        		if ((config.disableSlimeStopper && listenerClass == SlimeSpawnHandler.class) || (config.disableOpItemEffects && listenerClass == ItemLawSword.OPAnvilHandler.class)) {
        			
        			listenersToRemove.add(listener);
        		}
        	}
        	
        	for (Object toRemove : listenersToRemove) {
        		
        		MinecraftForge.EVENT_BUS.unregister(toRemove);
        	}
        	
        	if (config.disableCheatyTema) {
        		
        		MinecraftForge.EVENT_BUS.register(this);
        		ReflectionHelper.setPrivateValue(PlayerHelper.class, null, UUID.randomUUID(), "temaID");
        	}
    	}
    	
    	catch (Exception e) {
    		
    		e.printStackTrace();
    	}
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogic(PlayerLoggedInEvent event) {
    	
    	// Re-roll the tema user ID to a new random one if a user ever logs on with this ID.
		try {
			
	    	if (PlayerHelper.isThisPlayerACheatyBastardOfCheatBastardness(event.player)) {
	    		
	        	ReflectionHelper.setPrivateValue(PlayerHelper.class, null, UUID.randomUUID(), "temaID");
	    	}
		}
		
		catch (Exception e) {
			
			e.printStackTrace();
		}
    }
    
    class Configuration extends net.minecraftforge.common.config.Configuration {
    	
    	protected final boolean disableSlimeStopper;
    	protected final boolean disableOpItemEffects;
    	protected final boolean disableCheatyTema;
    	
        protected Configuration(String file) {
            
            super(new File("config/" + file + ".cfg"));
            
        	
        	disableSlimeStopper = this.getBoolean("disableSlimeStopper", "general", true, "Prevents XU2 from preventing slimes from spawning in surface worlds.");
        	disableOpItemEffects = this.getBoolean("disableOpItemEffects", "general", true, "Prevents certain XU2 Items such as the law sword, fire axe, and compound bow from having op effects in the anvil. This also prevents Tema from getting these items for free when they join the game or respawn.");
        	disableCheatyTema = this.getBoolean("disableCheatyTema", "general", true, "Prevents XU2 from giving RWTema special treatment in certain parts of the code.");
        	
        	if (this.hasChanged()) {
        		
        		this.save();
        	}
        }
    }
}