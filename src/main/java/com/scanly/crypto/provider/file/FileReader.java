package com.scanly.crypto.provider.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A low-level utility for reading file content from the local filesystem.
 * <p>
 * This class serves as the primary I/O gateway for the {@code crypto} package
 * during local development, specifically for loading PEM-encoded key files.
 * </p>
 */
public class FileReader {

    /**
     * Reads the entire contents of a file into a single String.
     * <p>
     * This method utilizes {@link Files#readString(Path)} to decode the file
     * using the UTF-8 charset. It is intended for text-based files like
     * PEM keys or configuration files.
     * </p>
     *
     * @param path The absolute or relative path to the file.
     * @return The content of the file as a String.
     * @throws IOException If the file cannot be found, read, or is a directory.
     */
    public static String readFileAsString(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
}