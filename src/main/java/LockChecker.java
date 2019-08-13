import java.util.*;

public class LockChecker {

    private static final String LOCK = "lock";
    private static final String UNLOCK = "unlock";

    public static Set<String> validate(String input) {
        Set<Map<Integer, String>> validateVariants = getValidateVariants(geReentrantLocksPositions(input.toCharArray()));
        return applyValidator(getMostCompleteVariants(validateVariants), input);
    }

    private static Set<String> applyValidator(Set<Map<Integer, String>> validateVariants, String input){
        Set<String> result = new HashSet<>();
        for (Map<Integer, String> map : validateVariants) {
            result.add(applyValidator(map, input));
        }
        return result;
    }

    private static Map<Integer, String> geReentrantLocksPositions(char[] input) {
        Map<Integer, String> positions = new TreeMap<>();
        for (int i = 0; i < input.length; i++) {
            if (input[i] == '{') positions.put(i, LOCK);
            else if (input[i] == '}') positions.put(i, UNLOCK);
        }
        return positions;
    }

    private static Set<Map<Integer, String>> getMostCompleteVariants(Set<Map<Integer, String>> set) {
        int maxCount = 0;
        for (Map<Integer, String> map : set) {
            if (map.size() > maxCount) maxCount = map.size();
        }
        Set<Map<Integer, String>> newSet = new HashSet<>();
        for (Map<Integer, String> map : set) {
            if (map.size() == maxCount) newSet.add(map);
        }
        return newSet;
    }

    private static String applyValidator(Map<Integer, String> map, String input) {
        StringBuilder builder = new StringBuilder();
        List<Integer> badValues = findBadValues(map.keySet(), input);
        int startPosition = 0;
        for (Integer i : badValues) {
            builder.append(input, startPosition, i);
            startPosition = i + 1;
        }
        builder.append(input, startPosition, input.length());
        return builder.toString();
    }

    private static List<Integer> findBadValues(Set<Integer> goodValues, String input) {
        List<Integer> badValues = new ArrayList<>();
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] == '{' || chars[i] == '}') && !isGoodValue(i, goodValues)) {
                badValues.add(i);
            }
        }
        return badValues;
    }

    private static boolean isGoodValue(int value, Set<Integer> goodValues) {
        return goodValues.contains(value);
    }

    private static Set<Map<Integer, String>> getValidateVariants(Map<Integer, String> reentrantLocks) {
        Set<Map<Integer, String>> set = new HashSet<>();
        int countLocks = 0;
        Iterator<Map.Entry<Integer, String>> entries = reentrantLocks.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, String> entry = entries.next();
            if (entry.getValue().equals(LOCK)) countLocks++;
            if (entry.getValue().equals(UNLOCK)  && countLocks == 0 ) {
                entries.remove();
            }
            if (entry.getValue().equals(UNLOCK) && countLocks > 0 ) {
                Map<Integer, String> newMap = copy(reentrantLocks);
                newMap.remove(entry.getKey());
                set.addAll(getValidateVariants((newMap)));
                countLocks--;
            }
        }
        if (countLocks == 0) set.add(reentrantLocks);
        else set.add(removeLastUnlock(reentrantLocks, countLocks));
        return set;
    }


    private static Map<Integer, String> removeLastUnlock(Map<Integer, String> map, int count) {
        TreeMap<Integer, String> newMap = new TreeMap<>(map);
        List<Map.Entry<Integer, String>> list = new ArrayList<>(map.entrySet());
        for (int i = list.size() - 1; i > -1 && count != 0; i--) {
            Map.Entry<Integer, String> entry = list.get(i);
            if (entry.getValue().equals(LOCK)) {
                newMap.remove(entry.getKey());
                count--;
            }
        }
        return newMap;
    }

    private static Map<Integer, String> copy(Map<Integer, String> map) {
        return new TreeMap<>(map);
    }
}
