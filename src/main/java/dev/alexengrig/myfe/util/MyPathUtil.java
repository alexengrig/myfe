package dev.alexengrig.myfe.util;

import dev.alexengrig.myfe.model.MyFile;
import dev.alexengrig.myfe.model.MyPath;

import java.util.Objects;
import java.util.Set;

/**
 * A utility class for {@link MyPath}.
 */
public class MyPathUtil {

    private static final Set<String> IMAGE_FILE_EXTENSIONS = Set.of("JPEG", "JPG", "GIF", "XBM");
    private static final Set<String> TEXT_FILE_EXTENSIONS = Set.of("TXT", "LOG");

    private MyPathUtil() throws IllegalAccessException {
        throw new IllegalAccessException("This is utility class");
    }

    public static String getExtension(MyPath path) {
        if (requireNonNullPath(path).isDirectory()) {
            return "File folder";
        } else {
            String name = path.getName();
            int indexOfDot = name.lastIndexOf('.');
            if (indexOfDot >= 0) {
                return name.substring(indexOfDot + 1).toUpperCase();
            } else {
                return "File";
            }
        }
    }

    public static boolean isImage(MyFile file) {
        return IMAGE_FILE_EXTENSIONS.contains(requireNonNullFile(file).getExtension());
    }

    public static boolean isText(MyFile file) {
        return TEXT_FILE_EXTENSIONS.contains(requireNonNullFile(file).getExtension());
    }

    private static MyPath requireNonNullPath(MyPath path) {
        return Objects.requireNonNull(path, "The path must not be null");
    }

    private static MyFile requireNonNullFile(MyFile file) {
        return Objects.requireNonNull(file, "The file must not be null");
    }

}
