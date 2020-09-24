package de.hub.mse.variantsync.variantdrift.refactoring;

import java.util.Set;

/***
 * This is a placeholder for a later implementation using WordNet which provides names that are logically connected
 * to the given name based on parent-sibling-child relationships.
 *
 * Currently, all returned names are random Strings.
 */
public class NameProvider {
    private static final RandomString randomString = new RandomString();

    public static String getRandomSiblingName(String name) {
        return randomString.nextString();
    }

    public static String getRandomChildName(String name) {
        return randomString.nextString();
    }

    public static String getRandomParentName(Set<String> name) {
        return randomString.nextString();
    }
}
