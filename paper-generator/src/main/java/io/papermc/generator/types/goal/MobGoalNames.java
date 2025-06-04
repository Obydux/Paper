package io.papermc.generator.types.goal;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import io.papermc.generator.resources.DataFileLoader;
import io.papermc.generator.resources.DataFiles;
import io.papermc.generator.types.Types;
import io.papermc.generator.utils.Formatting;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.apache.commons.lang3.math.NumberUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MobGoalNames { // todo sync with MobGoalHelper ideally this should not be duplicated

    private static final Map<Class<? extends Goal>, ClassName> entityClassCache = new HashMap<>();
    private static final Map<String, String> deobfuscationMap = new HashMap<>();

    static {
        // TODO these kinda should be checked on each release, in case obfuscation changes
        deobfuscationMap.put("abstract_skeleton_1", "abstract_skeleton_melee");
    }

    private static final List<String> IGNORED_SUFFIXES = List.of("TargetGoal", "Goal");

    private static String getPathName(String name) {
        String pathName = name.substring(name.lastIndexOf('.') + 1);
        boolean needDeobfMap = false;

        // inner classes
        int firstInnerDelimiter = pathName.indexOf('$');
        if (firstInnerDelimiter != -1) {
            String innerClassName = pathName.substring(firstInnerDelimiter + 1);
            for (String nestedClass : innerClassName.split("\\$")) {
                if (NumberUtils.isDigits(nestedClass)) {
                    needDeobfMap = true;
                    break;
                }
            }
            if (!needDeobfMap) {
                pathName = innerClassName;
            }
            pathName = pathName.replace('$', '_');
            // mapped, wooo!
        }

        pathName = Formatting.stripInitialWords(pathName, IGNORED_SUFFIXES, true);
        pathName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pathName);

        if (needDeobfMap && !deobfuscationMap.containsKey(pathName)) {
            System.err.println("need to map " + name + " (" + pathName + ")");
        }

        // did we rename this key?
        return deobfuscationMap.getOrDefault(pathName, pathName);
    }

    static GoalKey getKey(Class<? extends Goal> goalClass) {
        String name = getPathName(goalClass.getName());
        return new GoalKey(getEntityClassName(goalClass), ResourceLocation.withDefaultNamespace(name));
    }

    private static ClassName getEntityClassName(Class<? extends Goal> goalClass) {
        return entityClassCache.computeIfAbsent(goalClass, key -> {
            for (Constructor<?> ctor : key.getDeclaredConstructors()) {
                for (Class<?> param : ctor.getParameterTypes()) {
                    if (net.minecraft.world.entity.Mob.class.isAssignableFrom(param)) {
                        //noinspection unchecked
                        return toBukkitClass((Class<? extends net.minecraft.world.entity.Mob>) param);
                    } else if (RangedAttackMob.class.isAssignableFrom(param)) {
                        return Types.RANGED_ENTITY; // todo move outside
                    }
                }
            }
            throw new RuntimeException("Can't figure out applicable entity for mob goal " + goalClass); // maybe just return Mob?
        });
    }

    private static ClassName toBukkitClass(Class<? extends net.minecraft.world.entity.Mob> nmsClass) {
        ClassName bukkitClass = DataFileLoader.get(DataFiles.ENTITY_CLASS_NAMES).get(nmsClass);
        if (bukkitClass == null) {
            throw new RuntimeException("Can't figure out applicable bukkit entity for nms entity " + nmsClass); // maybe just return Mob?
        }
        return bukkitClass;
    }
}
