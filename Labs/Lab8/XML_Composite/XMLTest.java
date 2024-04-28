package Labs.Lab8.XML_Composite;


import java.util.*;
import java.util.stream.Collectors;

//XMLLeaf
// <student type="redoven" smer="KNI"> Trajce Trajkov </student>

//XMLComposite
// <student type="redoven">
//    <name>
//        <first-name>Trajce</first-name>
//        <last-name>Trajkov</last-name>
//    </name>
//</student>


interface XMLComponent {

    void addAttribute(String key, String value);

    String print(String intend);
}

abstract class Element implements XMLComponent {
    String tag;
    final Map<String, String> atributes;

    public Element(String tag) {
        this.tag = tag;
        this.atributes = new LinkedHashMap<>();
    }

    @Override
    public void addAttribute(String key, String value) {
        atributes.put(key, value);
    }

}

class XMLLeaf extends Element {
    String value;

    public XMLLeaf(String tag, String value) {
        super(tag);
        this.value = value;
    }

    //<student type="redoven" smer="KNI"> Trajce Trajkov </student>
    public String print(String intend) {
        return String.format("%s<%s%s>%s</%s>",
                intend,
                tag,
                atributes.entrySet().stream()
                        .map(entry -> String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining("")),
                value,
                tag);
    }
}

class XMLComposite extends Element {
    final List<XMLComponent> children;

    public XMLComposite(String tag) {
        super(tag);
        this.children = new ArrayList<>();
    }

    public void addComponent(XMLComponent child) {
        children.add(child);
    }

    @Override
    public String print(String intend) {
        return String.format("%s<%s %s>\n%s\n%s</%s>",
                intend,
                tag,
                atributes.entrySet().stream()
                        .map(entry -> String.format("%s=\"%s\"", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(" ")),
                children.stream().map(child -> child.print(intend + "\t")).collect(Collectors.joining("\n")),
                intend,
                tag);
    }
}

public class XMLTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();
        XMLComponent component = new XMLLeaf("student", "Trajce Trajkovski");
        component.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");


        XMLComposite composite = new XMLComposite("name");
        composite.addComponent(new XMLLeaf("first-name", "trajce"));
        composite.addComponent(new XMLLeaf("last-name", "trajkovski"));
        composite.addAttribute("type", "redoven");
        component.addAttribute("program", "KNI");

        if (testCase == 1) {
            System.out.println(component.print(""));
        } else if (testCase == 2) {
            System.out.println(composite.print(""));
        } else if (testCase == 3) {
            XMLComposite main = new XMLComposite("level1");
            main.addAttribute("level", "1");
            XMLComposite lvl2 = new XMLComposite("level2");
            lvl2.addAttribute("level", "2");
            XMLComposite lvl3 = new XMLComposite("level3");
            lvl3.addAttribute("level", "3");
            lvl3.addComponent(component);
            lvl2.addComponent(lvl3);
            lvl2.addComponent(composite);
            lvl2.addComponent(new XMLLeaf("something", "blabla"));
            main.addComponent(lvl2);
            main.addComponent(new XMLLeaf("course", "napredno programiranje"));

            System.out.println(main.print(""));
        }
    }
}
