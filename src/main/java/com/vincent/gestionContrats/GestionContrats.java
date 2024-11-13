package com.vincent.gestionContrats;

import com.vincent.gestionContrats.commands.ContratsCommand;
import com.vincent.gestionContrats.SetupContrat.ContractManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GestionContrats extends JavaPlugin {
    private ContractManager contractManager;

    @Override
    public void onEnable() {
        // Initialisation du gestionnaire de contrats
        contractManager = new ContractManager();

        // Enregistrement de la commande "contrats"
        this.getCommand("contrats").setExecutor(new ContratsCommand(contractManager));
    }

    @Override
    public void onDisable() {
        // Actions à effectuer lors de la désactivation du plugin (s'il y en a)
    }

    public ContractManager getContractManager() {
        return contractManager;
    }
}
