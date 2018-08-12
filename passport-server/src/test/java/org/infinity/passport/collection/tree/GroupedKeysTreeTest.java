package org.infinity.passport.collection.tree;

import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupedKeysTreeTest {

    @Test
    public void commonPrefixSearch() throws IOException {
        GroupedKeysTree<String> tree = new GroupedKeysTree<String>();
        tree.insert("S001", "C001", "G001", "U001");
        tree.insert("S002", "C001", "G002");
        tree.insert("S003", "C003");
        tree.insert("S004", "C004");
        tree.insert("S005", "C005");
        tree.insert("S006", "C006");
        tree.insert("S007", "C007");
        tree.insert("S008", "C008");
        tree.insert("S009", "C009");
        tree.insert("S010", "C010");
        tree.insert("S011", "C011");
        tree.insert("S012", "C012");
        tree.insert("S013", "C013");
        tree.insert("S014", "C014");
        tree.insert("S015", "C015");
        tree.insert("S016", "C016");
        tree.insert("S017", "C017");
        tree.insert("S018", "C002");
        tree.insert("S019", "C001");
        tree.insert("S020", "C001", "G003");
        tree.insert("S021", "C001", "G002", "U003");
        tree.insert("S022", "C001", "G001", "U004");
        tree.insert("S023", "C001", "G002", "U005");

        assertThat(tree.getDataSize()).isEqualTo(23);
        assertThat(tree.getNodeSize()).isEqualTo(24);

        tree.searchAll().forEach(System.out::println);

        assertThat(tree.searchAll().size()).isEqualTo(tree.getDataSize());

        tree.searchAll().forEach(System.out::println);


        PrintWriter pw = new PrintWriter(System.out, true);
        tree.print(pw);
        pw.println();

        tree.commonPrefixSearch("C001", "G002", null);

        tree.commonPrefixSearch("C001", "G002", "U003").forEach(System.out::println);

        tree.commonPrefixSearch("C001", "G001", "U001").forEach(System.out::println);
        System.out.println(tree.preciseSearch("C001", "G001", "U001"));

        tree.update("S321", "C001", "G001", "U001");
        tree.commonPrefixSearch("C001", "G001", "U001").forEach(System.out::println);

        tree.remove("C001", "G002");
        assertThat(tree.getDataSize()).isEqualTo(22);
        assertThat(tree.getNodeSize()).isEqualTo(24);
        tree.print();

        tree.remove("C001", "G002", "U003");
        assertThat(tree.getDataSize()).isEqualTo(21);
        assertThat(tree.getNodeSize()).isEqualTo(23);
        tree.print();

        tree.remove("C001", "G002", "U005");
        tree.print();
        assertThat(tree.getDataSize()).isEqualTo(20);
        assertThat(tree.getNodeSize()).isEqualTo(21);

        tree.insert("S024", "C001", "G003");
        assertThat(tree.getDataSize()).isEqualTo(21);
        assertThat(tree.getNodeSize()).isEqualTo(21);

        assertThat(tree.preciseSearch("C001", "G003").size()).isEqualTo(2);
    }

    @Test
    public void commonPrefixSearchPerformance() {
        GroupedKeysTree<String> tree = new GroupedKeysTree<String>();
        String company = null;
        String group = null;
        String user = null;
        int count = 0;
        for (int c = 0; c < 100; c++) {
            String romdomCompany = "" + new Random(System.nanoTime() + c).nextInt(500000);
            if (c == 50) {
                company = romdomCompany;
            }

            for (int g = 0; g < 100; g++) {
                String romdomGroup = "" + new Random(System.nanoTime() + g).nextInt(500000);
                if (c == 50 && g == 50) {
                    group = romdomGroup;
                }

                for (int u = 0; u < 100; u++) {
                    String romdomUser = "" + new Random(System.nanoTime() + u).nextInt(500000);
                    if (c == 50 && g == 50 && u == 50) {
                        user = romdomUser;
                    }

                    tree.insert("data", romdomCompany, romdomGroup, romdomUser);
                    if (count % 1000000 == 0) {
                        System.out.println("Insert " + count);
                    }
                    count++;
                }
            }
        }
        System.out.println("Data count: " + tree.getDataSize());
        long start = System.currentTimeMillis();
        List<String> list = tree.commonPrefixSearch(company, group, user);
//        list.forEach(System.out::println);// Note: foreach很慢
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        long cost = System.currentTimeMillis() - start;
        System.out.println("Search time: " + cost + "ms");

        StopWatch watch = new StopWatch();
        watch.start();
        tree.searchAll();
        watch.stop();
        System.out.println("Search all time: " + watch.getTotalTimeMillis() + "ms");
    }

    @Test
    public void loopWhenFewerDataPerformanceTest() {
        StopWatch watch0 = new StopWatch();
        watch0.start();
        List<String> list = Arrays.asList("1");
        watch0.stop();
        System.out.println("The 1st created time: " + watch0.getTotalTimeMillis() + "ms");

        StopWatch watch1 = new StopWatch();
        watch1.start();
        list.get(0);
        watch1.stop();
        System.out.println("The 1st time: " + watch1.getTotalTimeMillis() + "ms");

        StopWatch watch2 = new StopWatch();
        watch2.start();
        list.forEach((item) -> item.toString());
        watch2.stop();
        System.out.println("The 2nd time: " + watch2.getTotalTimeMillis() + "ms");

        StopWatch watch3 = new StopWatch();
        watch3.start();
        list.stream().forEach((item) -> item.toString());
        watch3.stop();
        System.out.println("The 3rd time: " + watch3.getTotalTimeMillis() + "ms");

        StopWatch watch4 = new StopWatch();
        watch4.start();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).toString();
        }
        watch4.stop();
        System.out.println("The 4th time: " + watch4.getTotalTimeMillis() + "ms");

        StopWatch watch5 = new StopWatch();
        watch5.start();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString();
        }
        watch5.stop();
        System.out.println("The 5th time: " + watch5.getTotalTimeMillis() + "ms");
    }

    @Test
    public void loopWhenHugeDataPerformanceTest() {
        StopWatch watch0 = new StopWatch();
        watch0.start();
//        List<String> list = IntStream.range(1, 2).mapToObj(String::valueOf).collect(Collectors.toList());//mapToObj做了一次循环因此很慢
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            list.add("" + i);
        }
        watch0.stop();
        System.out.println("The 1st created time: " + watch0.getTotalTimeMillis() + "ms");

        StopWatch watch1 = new StopWatch();
        watch1.start();
        list.get(0);
        watch1.stop();
        System.out.println("The 1st time: " + watch1.getTotalTimeMillis() + "ms");

        StopWatch watch2 = new StopWatch();
        watch2.start();
        list.forEach((item) -> item.toString());
        watch2.stop();
        System.out.println("The 2nd time: " + watch2.getTotalTimeMillis() + "ms");

        StopWatch watch3 = new StopWatch();
        watch3.start();
        list.stream().forEach((item) -> item.toString());
        watch3.stop();
        System.out.println("The 3rd time: " + watch3.getTotalTimeMillis() + "ms");

        StopWatch watch4 = new StopWatch();
        watch4.start();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).toString();
        }
        watch4.stop();
        System.out.println("The 4th time: " + watch4.getTotalTimeMillis() + "ms");

        StopWatch watch5 = new StopWatch();
        watch5.start();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString();
        }
        watch5.stop();
        System.out.println("The 5th time: " + watch5.getTotalTimeMillis() + "ms");
    }

}
