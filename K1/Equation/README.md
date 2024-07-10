Да се дефинира генеричка класа за равенка (Equation) во која ќе се чуваат имплементации на интерфејсите Supplier и Function. Генеричката класа треба да има два генерички параметри - еден за влезниот тип (типот на аргументите на равенката) и еден за излезниот тип (типот на резултатите од равенката).

Во класата Equation да се дефинира метод calculate кој не прима агрументи, а враќа објект од генеричката класата Optional со генерички параметар ист како излезниот тип на класата Equation. Методот треба да врати Optional објект пополнет со резултатот добиен од Function имплементацијата применет на аргументот добиен со Supplier имплементацијата.

Дополнително, да се дефинира класа EqationProcessor со еден генерички статички метод process кој ќе прими два аргументи:

* Листа од влезни податоци (објекти од влезниот тип)
* Листа од равенки (објекти од класа Equation)

Методот потребно е за секој елемент од листата на влезни податоци да го испечати тој елемент, да направи пресметка на равенката и да го испечати резултатот. Доколку равенката се евалуира исто на сите елементи од листата на влезни податоци, тогаш испечатете го резултатот од секоја равенка само еднаш, на крај.

Во главната класа на местата означени со TODO да се дефинираат потребните објекти од класата Equation. Да се користат ламбда изрази за дефинирање на објекти од тип Supplier и Function.


Напомена: Решенијата кои нема да може да се извршат (не компајлираат) нема да бидат оценети.