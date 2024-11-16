package com.vincent.gestionContrats.events;

import com.vincent.gestionContrats.GestionContrats;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;

public class ContractTrait extends Trait {
    public ContractTrait() {
        super("contract_trait");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (event.getNPC() != this.getNPC()) return;

        // Ouvrir l'interface d'inventaire pour le joueur
        new InventoryGUI(event.getClicker(), GestionContrats.getInstance().getContractManager()).open();
    }
}
