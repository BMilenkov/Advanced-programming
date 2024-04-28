package K2.BlockContainer;

import java.util.*;
import java.util.stream.Collectors;


class BlockContainer<T extends Comparable<T>> {
    private Map<Integer, Set<T>> blocks;
    private final int numOfElementsInBlock;
    private int blockNum;

    public BlockContainer(int n) {
        this.blocks = new HashMap<>();
        this.numOfElementsInBlock = n;
        this.blockNum = 1;
    }

    public void add(T element) {

        blocks.putIfAbsent(blockNum, new TreeSet<>());
        blocks.get(blockNum).add(element);

        if (blocks.get(blockNum).size() == numOfElementsInBlock)
            blockNum++;
    }

    public void remove(T element) {
        if (blocks.getOrDefault(blockNum, Collections.emptySet()).isEmpty())
            --blockNum;
        blocks.get(blockNum).remove(element);
        if (blocks.get(blockNum).size() == 0) {
            blocks.remove(blockNum);
            blockNum--;
        }
    }

    public void sort() {
        List<T> sorted = blocks.values().stream().flatMap(Collection::stream).sorted().collect(Collectors.toList());

        this.blocks = new HashMap<>();
        this.blockNum = 1;

        sorted.forEach(this::add);

    }

    @Override
    public String toString() {

        return blocks.values()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
}


public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<Integer>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for (int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<String>(size);
        String lastString = null;
        for (int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}
