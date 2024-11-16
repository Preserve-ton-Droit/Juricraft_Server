package com.vincent.gestionContrats.events;

import com.vincent.gestionContrats.SetupContrat.ContractManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCInteractionListener implements Listener {
    private final ContractManager contractManager;

    public NPCInteractionListener(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    @EventHandler
    public void onNPCInteraction(NPCRightClickEvent event) {
        // Vérifier si le nom du NPC est "Avocat"
        if (event.getNPC().getName().equalsIgnoreCase("Avocat")) {
            // Ouvrir l'inventaire du joueur uniquement si c'est l'NPC "Avocat"
            new InventoryGUI(event.getClicker(), contractManager).open();
        }
    }
}
