package com.vincent.gestionContrats;

import com.vincent.gestionContrats.commands.ContratsCommand;
import com.vincent.gestionContrats.SetupContrat.ContractManager;
import com.vincent.gestionContrats.events.ContractTrait;
import com.vincent.gestionContrats.events.NPCInteractionListener;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.plugin.java.JavaPlugin;

public class GestionContrats extends JavaPlugin {
    private ContractManager contractManager;
    private NPC contractNPC;
    private static GestionContrats instance; // Champ statique pour le singleton


    @Override
    public void onEnable() {
        // Initialisation du gestionnaire de contrats
        instance = this;
        contractManager = new ContractManager();

        this.getCommand("contrats").setExecutor(new ContratsCommand(contractManager));
        getServer().getPluginManager().registerEvents(new NPCInteractionListener(contractManager), this);
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ContractTrait.class).withName("contract_trait"));
        createContractNPC();
    }

    private void createContractNPC() {
        contractNPC = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, "Avocat");
        contractNPC.spawn(getServer().getWorlds().get(0).getSpawnLocation());
        contractNPC.addTrait(ContractTrait.class);
    }

    @Override
    public void onDisable() {
        if (contractNPC != null) {
            contractNPC.despawn();
            CitizensAPI.getNPCRegistry().deregisterAll();
        }
    }

    public ContractManager getContractManager() {
        return contractManager;
    }

    public static GestionContrats getInstance() {
        return instance;
    }
}
