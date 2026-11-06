package concentratequest.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import concentratequest.exception.SaveException;
import concentratequest.game.GameState;

/** Writes and reads the whole game state with Java serialization. */
public class SaveManager {
    private static final Path DEFAULT_FILE = Path.of("save", "concentratequest.sav");

    private final Path file;

    public SaveManager() {
        this(DEFAULT_FILE);
    }

    public SaveManager(Path file) {
        this.file = file;
    }

    public boolean exists() {
        return Files.exists(file);
    }

    /**
     * Writes to a temporary file first and then replaces the old save, so a
     * crash in the middle never leaves a broken save file.
     */
    public void save(GameState state) throws SaveException {
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Path folder = file.toAbsolutePath().getParent();
            Files.createDirectories(folder);
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
                out.writeObject(state);
            }
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new SaveException("Could not save the game to " + file, e);
        }
    }

    public GameState load() throws SaveException {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (GameState) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new SaveException("Could not load the save file " + file, e);
        }
    }
}
