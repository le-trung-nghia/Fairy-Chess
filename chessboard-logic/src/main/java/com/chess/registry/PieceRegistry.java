package com.chess.registry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.chess.logic.types.Piece;

public class PieceRegistry {
    private final Map<String, Map<String, Constructor<? extends Piece>>> registry = new HashMap<>();

    public void registerPiecePack(File pack) {
        try (URLClassLoader loader = new URLClassLoader(new URL[] { pack.toURI().toURL() },
                this.getClass().getClassLoader())) {
            URL manifestUrl = loader.findResource("META-INF/MANIFEST.MF");
            String packName = null;
            if (manifestUrl == null) {
                throw new IllegalArgumentException("Pack file %s does not have a manifest".formatted(pack.toString()));
            }
            try (InputStream is = manifestUrl.openStream()) {
                Manifest manifest = new Manifest(is);
                Attributes attrs = manifest.getMainAttributes();
                packName = attrs.getValue("Pack-Name");
            }
            if (packName == null || packName.isBlank()) {
                throw new IllegalArgumentException("Pack file %s does not have a pack name".formatted(pack.toString()));
            }

            if (registry.containsKey(packName)) {
                throw new IllegalArgumentException(
                        "Pack file %s: pack name %s is already registered"
                                .formatted(pack.toString(), packName));
            }

            registry.put(packName, new HashMap<>());

            ServiceLoader<Piece> serviceLoader = ServiceLoader.load(Piece.class, loader);

            for (var provider : (Iterable<ServiceLoader.Provider<Piece>>) () -> serviceLoader.stream().iterator()) {
                Constructor<? extends Piece> piece = provider.type().getDeclaredConstructor();
                String pieceName = piece.newInstance().identifier();

                // Bind the class reference to the namespace
                registry.get(packName).put(pieceName, piece);
            }
        } catch (IOException | InstantiationException | InvocationTargetException | IllegalAccessException
                | NoSuchMethodException e) {
            throw new RuntimeException("Error in loading pack file %s: %s".formatted(pack.toString(),
                    e.toString()));
        }
    }

    public Piece instantiatePiece(PiecePath path) {
        if (!registry.containsKey(path.packName())) {
            throw new NoSuchElementException(
                    "Pack %s is not registered in the piece registry".formatted(path.packName()));
        }
        Map<String, Constructor<? extends Piece>> pack = registry.get(path.packName());
        if (!pack.containsKey(path.pieceName())) {
            throw new NoSuchElementException(
                    "Piece %s is not present in pack %s".formatted(path.pieceName(), path.packName()));
        }
        try {
            return pack.get(path.pieceName()).newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new AssertionError("Unexpected error: Instantiation of piece %s failed due to %s"
                    .formatted(path.toString(), e.toString()));
        }
    }
}
