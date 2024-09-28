package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OptionsSaverModClient implements ClientModInitializer {

    private static final String MOD_DIR = "example_mod";
    private static final String OPTIONS_BACKUP = "options_backup.txt";

    @Override
    public void onInitializeClient() {
    	
        restoreOptions();

        //command yayayay
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("saveoptions")
                .executes(context -> {
                    saveOptions();
                    context.getSource().sendFeedback(Text.literal("Options saved successfully!").formatted(Formatting.GREEN));
                    return 1;
                }));
        });
    }

    private void saveOptions() {
        try {
            // Get the Minecraft options.txt file path
            File optionsFile = getOptionsFile();
            if (!optionsFile.exists()) {
                System.err.println("Options file does not exist: " + optionsFile.getAbsolutePath());
                return;
            }
            // Create a backup directory if it doesn't exist
            File modDir = new File(MinecraftClient.getInstance().runDirectory, MOD_DIR);
            if (!modDir.exists()) {
                modDir.mkdir();
            }
            // Copy the options.txt file to the mod directory
            Files.copy(optionsFile.toPath(), Path.of(modDir.getPath(), OPTIONS_BACKUP), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving options: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void restoreOptions() {
        try {
            // Get the Minecraft options.txt file path
            File optionsFile = getOptionsFile();
            File backupFile = new File(MinecraftClient.getInstance().runDirectory, MOD_DIR + "/" + OPTIONS_BACKUP);
            if (backupFile.exists()) {
                // Replace the existing options.txt file with the backup :)
                Files.copy(backupFile.toPath(), optionsFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } else {
                System.err.println("Backup file does not exist: " + backupFile.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error restoring options: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private File getOptionsFile() {
        //this kinda assumes where it is I hope this doesn't cause a problem for others, this is also not compatible with any os that isn't windows.
    	//maybe find a better way in the future.
        return new File(System.getProperty("user.home"), "AppData/Roaming/.minecraft/options.txt");
    }
}
