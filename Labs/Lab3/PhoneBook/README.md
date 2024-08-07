Во оваа задача треба да имплементирате класа која ќе претставува телефонски именик. Именикот се состои од повеќе контакти при што, за секој контакт се чуваат неговото име и максимум до 5 телефонски броја за тој контакт. За потребите на класата Contact треба да ги имплементирате следниве методи.

* Contact(String name, String... phonenumber) - конструктор со параметри - името треба да е подолго од 4 караткери, но максимум до 10 карактери и не смее да содржи други знаци освен латинични букви и бројки во спротивно се фрла исклучок од тип InvalidNameException - телефонските броеви мора да се состојат од точно 9 цифри при што првите три цифри се "070", "071", "072", "075","076","077" или "078" во спротивно се фрла исклучок од тип InvalidNumberException - контактот може да содржи максимум 5 броја во спротивно се фрла исклучок MaximumSizeExceddedException
* getName():String - get метод за името
* getNumbers():String[] - get метод за броевите кои треба да се лексикографски подредени (нека враќа копија од оригиналната низа)
* addNumber(String phonenumber) - метод кој додава нов број во контактот - за овој метод важат истите ограничувања за форматот на броевите како и во конструкторот
* toString():String - враќа текстуален опис во следниот формат Во прв ред името на контактот, во втор ред бројот на телефонски броеви и понатаму во одделни редови секој број поединечно повторно сортирани лексикографски
* valueOf(String s):Contact - статички метод кој за дадена тексутална репрезентација на контактот ќе врати соодветен објект - доколку настане било каков проблем при претварањето од тексутална репрезентација во објект Contact треба да се фрли исклучок од тип InvalidFormatException

Користејќи ја класата Contact која ја напишавте сега треба да се развие и класа за телефонски именик PhoneBook. Оваа класа содржи низа од не повеќе од 250 контакти и ги нуди следниве методи

* PhoneBook() - празен конструктор
* addContact(Contact contact):void - додава нов контакт во именикот, притоа доколку се надмине максималниот капацитет од 250 се фрла исклучок MaximumSizeExceddedException - дополнително ограничување е што сите имиња на контакти мора да бидат единствени, доколку контактот што сакате да го додадете има исто име со некој од веќе постоечките контакти треба да фрлите исклучок од типот InvalidNameException
* getContactForName(String name):Contact - го враќа контактот со соодветното име доколку таков постои во спротивно враќа null
* numberOfContacts():int - го враќа бројот на контакти во именикот
* getContacts():Contact[] - враќа низа од сите контакти сортирани според нивното име (нека враќа копија од низата)
* removeContact(String name):boolean - го брише соодветниот контакт од именикот и раќа true доколку постои, во спротивно враќа false
* toString():String - враќа текстуален опис на именикот каде се наредени сите контакти подредени според нивното име, одделени со по еден празен ред
* saveAsTextFile(PhoneBook phonebook,String path):boolean - статички метод кој го запишува именикот во текстуална датотека која се наоѓа на локација path,доколку не постои датотеката треба да се креира- методот враќа false само доколку има некаков проблем при запишување на податоците во датотеката
* loadFromTextFile(String path):Phonebook - статички метод кој вчитува именик претходно запишан со методот saveAsTextFile - доколку датотеката не постои или неможе да се отвори за читање се пропагира оригиналниот IOException, а доколку настане проблем при парсирањето на текстот од датотеката треба да се фрли исклучок InvalidFormatException
* getContactsForNumber(String number_prefix):Contact[] - за даден префикс од број (првите неколку цифри) ги враќа сите контакти кои имаат барем еден број со тој префикс - низата не треба да содржи дупликат контакти или null елементи подредена според имињата на контактите

*Сите исклучоци освен IOException треба сами да ги напишете - секаде каде што е можно додате дополнително објаснување или податочни членови во врска со причината за исклучокот