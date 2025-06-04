package io.papermc.generator.utils;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.math.NumberUtils;
import java.util.Comparator;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Formatting {

    private static final Pattern ILLEGAL_FIELD_CHARACTERS = Pattern.compile("[.-/]");

    public static String formatKeyAsField(String path) {
        return ILLEGAL_FIELD_CHARACTERS.matcher(path.toUpperCase(Locale.ENGLISH)).replaceAll("_");
    }

    @ApiStatus.Obsolete
    public static String formatTagFieldPrefix(String name, ResourceKey<? extends Registry<?>> registryKey) {
        if (registryKey == Registries.BLOCK) {
            return "";
        }
        if (registryKey == Registries.GAME_EVENT) {
            return "GAME_EVENT_"; // Paper doesn't follow the format (should be GAME_EVENTS_) (pre 1.21)
        }
        return name.toUpperCase(Locale.ENGLISH) + "_";
    }

    public static Optional<String> findTagKeyPath(String tagDir, String resourcePath) {
        int tagsIndex = resourcePath.indexOf(tagDir);
        int dotIndex = resourcePath.lastIndexOf('.');
        if (tagsIndex == -1 || dotIndex == -1) {
            return Optional.empty();
        }
        return Optional.of(resourcePath.substring(tagsIndex + tagDir.length() + 1, dotIndex)); // namespace/tags/registry_key/[tag_key_path].json
    }

    public static String quoted(String value) {
        return "\"" + value + "\"";
    }

    public static String stripInitialWord(String name, String word) { // both ends
        if (name.startsWith(word)) {
            return name.substring(word.length());
        }

        if (name.endsWith(word)) {
            return name.substring(0, name.length() - word.length());
        }

        return name;
    }

    public static String stripInitialWord(String name, String word, boolean fromEnd) {
        if (fromEnd) {
            if (name.endsWith(word)) {
                return name.substring(0, name.length() - word.length());
            }
        } else {
            if (name.startsWith(word)) {
                return name.substring(word.length());
            }
        }
        return name;
    }

    public static String stripInitialWords(String name, List<String> words, boolean fromEnd) {
        String foundWord = null;
        for (String word : words) {
            if (fromEnd ? name.endsWith(word) : name.startsWith(word)) {
                foundWord = word;
                break;
            }
        }

        if (foundWord != null) {
            return fromEnd ? name.substring(0, name.length() - foundWord.length()) : name.substring(foundWord.length());
        }

        return name;
    }

    public static <T> Comparator<T> alphabeticKeyOrder(Function<T, String> mapper) {
        return (o1, o2) -> {
            String path1 = mapper.apply(o1);
            String path2 = mapper.apply(o2);

            OptionalInt trailingInt1 = tryParseTrailingInt(path1);
            OptionalInt trailingInt2 = tryParseTrailingInt(path2);

            if (trailingInt1.isPresent() && trailingInt2.isPresent()) {
                int numericDelta = Integer.compare(trailingInt1.getAsInt(), trailingInt2.getAsInt());
                if (numericDelta != 0) {
                    return numericDelta;
                }
            }

            return path1.compareTo(path2);
        };
    }

    private static OptionalInt tryParseTrailingInt(String path) {
        int delimiterIndex = path.lastIndexOf('_');
        if (delimiterIndex != -1) {
            String score = path.substring(delimiterIndex + 1);
            if (NumberUtils.isDigits(score)) {
                return OptionalInt.of(Integer.parseInt(score));
            }
        }
        return OptionalInt.empty();
    }

    private Formatting() {
    }
}
