/**
 * MIT License
 *
 * Copyright (c) 2024  David Rodríguez Alfayate - Divisa iT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.proxiasuite.dateparser;

import com.proxiasuite.dateparser.parser.DateExpressionVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Almacena una fecha resuelta por el sistema, una expresión que puede ser reubicada en una cierta posición
 * en función de expresiones de condicionante, tales como un conjunto fijo de días, o el día antes o despues
 * de una determinada expresión, etc.
 *
 * @author David Rodríguez Alfayate 
 */
public class DateExpression {
    /**
     * Lista de las operaciones posibls que tenemos con expresiones a la hora de comparar
     */
    public enum Opcode {
        EQUALS,
        LESSER,
        GREATER;

        public boolean compare(LocalDate l1, LocalDate l2) {
            switch (this) {
                case EQUALS:
                    return l1.equals(l2);
                case LESSER:
                    return l1.isBefore(l2);
                case GREATER:
                    return l1.isAfter(l2);

            }
            return false;
        }
    }
    /**
     * La lista de fechas de esta expresión
     */
    NavigableMap<Integer, List<LocalDate>> dates;

    /**
     * El código de semana asociado a este elemento
     */
    WeekFields week;

    /**
     * Si soportamos o no semanas completas
     */
    boolean fullWeks;

    /**
     * Si es una expresión aproximada
     */
    boolean approximate;

    private DateExpression(WeekFields week,boolean fullWeks, NavigableMap<Integer,List<LocalDate>> dates) {
        this.dates = new TreeMap<>(dates);
        this.week = week;
        this.fullWeks = fullWeks;
    }

    /**
     * Constructor, recibe las fechas sobre las que se opera
     *
     * @param dates Array de fechas en las que operamos
     */
    private DateExpression(WeekFields week, boolean fullWeeks,Stream<LocalDate> dates) {
        this.dates = new TreeMap<>();
        this.week = week;
        this.fullWeks = fullWeeks;
        dates.sorted().distinct().forEach(ld->{
            List<LocalDate> group = this.dates.computeIfAbsent(ld.getYear(),(e)->new ArrayList<>());
            group.add(ld);
        });
    }

    /**
     * Constructor, recibe las fechas sobre las que se opera
     *
     * @param dates Array de fechas en las que operamos
     */
    private DateExpression(WeekFields week, boolean fullWeks, List<LocalDate> dates) {
        this.dates = new TreeMap<>();
        this.week = week;
        this.fullWeks = fullWeks;
        dates.stream().sorted().distinct().forEach(ld->{
            List<LocalDate> group = this.dates.computeIfAbsent(ld.getYear(),(e)->new ArrayList<>());
            group.add(ld);
        });
    }

    /**
     * Constructor, recibe las fechas sobre las que se opera
     *
     * @param locale Idioma de trabajo
     * @param fullWeeks Si se considera que las semanas son completas, de lunes a domingo (o domingo a sabado en función del idioma)
     * @param dates Array de fechas en las que operamos
     */
    public DateExpression(Locale locale,boolean fullWeeks,List<LocalDate> dates) {
        this(WeekFields.of(locale),fullWeeks,dates);
    }


    /**
     * Resuelve una expresión en un día concreto (día 1, día 2, día 3)
     *
     * @param days  Los días del conjunto en el que va a recuperar un conjunto de valores.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression at(Integer ... days) {
        if(days == null || days.length == 0) {
            return new DateExpression(week,fullWeks,this.dates);
        }
        // Ordenamos los días recibicos y buscamos
        List<LocalDate> allDates =new ArrayList<>();

        for(int year: this.dates.keySet()) {
            int [] actualDays = adaptDayIndexes(this.dates.get(year),days);

            List<LocalDate> innerDates = this.dates.get(year);
            for(int i=0;i<innerDates.size();i++) {
                if(Arrays.binarySearch(actualDays,(i+1))>=0) {
                    allDates.add(innerDates.get(i));
                }
            }
        }
        return new DateExpression(week,fullWeks,allDates);
    }


    /**
     * Resuelve una expresión un conjunto de días de la semana, indicando un factor de agrupamiento
     * y el número de días de cada grupo que se va a quedar (por ejemplo un factor 2 implica que agrupamos
     * los lunes en bloques de 2 y si pedimos el primero sólo nos vamos a quedar con la posición 0 y 1)
     *
     * @param weekDay       Días de la semana
     * @param days          "Días" de los que pedimos datos.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression at(DayOfWeek [] weekDay, Integer ... days) {
        if(weekDay == null || weekDay.length==0) {
            return new DateExpression(week,fullWeks,this.dates);
        }


        List<LocalDate> allDates =new ArrayList<>();

        for(int year: this.dates.keySet()) {
            Map<DayOfWeek,List<LocalDate>> localDates = new HashMap<>();
            for(LocalDate ld: this.dates.get(year)) {
                if(Arrays.stream(weekDay).anyMatch(we-> we.equals(ld.getDayOfWeek()))) {
                    List<LocalDate> innerDates = localDates.computeIfAbsent(ld.getDayOfWeek(),(d)->new ArrayList<>());
                    innerDates.add(ld);
                }
            }

            if(days!=null && days.length!=0) {

                for (DayOfWeek dw : localDates.keySet()) {
                    int [] actualDays = adaptDayIndexes(localDates.get(dw),days);

                    for (int i = 0; i < localDates.get(dw).size(); i++) {
                        if (Arrays.binarySearch(actualDays, i+1) >= 0) {
                            allDates.add(localDates.get(dw).get(i));
                        }
                    }
                }
            } else {
                allDates.addAll(localDates.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
            }
        }
        return new DateExpression(week,fullWeks,allDates);
    }

    /**
     * Devuelve la expresión de fecha que se cumple para los datos que se encuentran en la semasna 1 a n del
     * conjunto especificado.
     *
     * @param weeks Las semanas del conjunto para las que pedimos datos.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression atWeek(Integer ... weeks) {
        if(weeks == null || weeks.length == 0) {
            return new DateExpression(week,fullWeks,dates);
        }

        // Lo primero es convertir la expresión que tenemos a una expresión de semnas completas,
        DateExpression de = new DateExpression(week,fullWeks,getDatesAsStream());

        List<LocalDate> allDates = new ArrayList<>();

        for(int year: de.dates.keySet()) {
            List<LocalDate> yearDates = de.dates.get(year);

            // Separamos en semanas...
            List<List<LocalDate>> weekList = new ArrayList<>();
            for(LocalDate ld: yearDates) {
                if(ld.getDayOfWeek() == week.getFirstDayOfWeek()) {
                    // Nueva semana...
                    weekList.add(new ArrayList<>());
                }
                if(weekList.isEmpty()) {
                    weekList.add(new ArrayList<>());
                }
                List<LocalDate> currentWeek = weekList.get(weekList.size()-1);
                // Añadimos el elemento en cuestión.
                currentWeek.add(ld);
            }

            // Si solo soportamos semanas completas, eliminamos aquellas que no tienen 7 días.
            if(fullWeks) {
                weekList.removeIf(l -> l.size() != 7);
            }

            int [] actualDays = adaptDayIndexes(weekList,weeks);
            for (int i = 0; i < weekList.size(); i++) {
                if (Arrays.binarySearch(actualDays, i+1) >= 0) {
                    allDates.addAll(weekList.get(i));
                }
            }
        }
        return new DateExpression(week,fullWeks,allDates);
    }

    /**
     * DEvuelve la expresión de fecha que peermite seleccionar aquellas fechas que son de aplicación en los
     * fines de semana especificasdos por la expresión weeekEnd
     *
     * @param weekEnd   Fines de semana que queremos recuperar.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression atWeekend(Integer ... weekEnd) {
        // Similar al computo de fines de semana, lo agrupamos en "fines" de semana y nos quedamos con
        // los que corrsponda.
        if(weekEnd == null || weekEnd.length == 0) {
            return new DateExpression(week,fullWeks,dates);
        }

        // Lo primero es convertir la expresión que tenemos a una expresión de semnas completas,
        DateExpression de = new DateExpression(week,fullWeks,getDatesAsStream());

        List<LocalDate> allDates = new ArrayList<>();

        for(int year: de.dates.keySet()) {
            List<LocalDate> yearDates = de.dates.get(year);

            // Ignoramos hasta el primer "sabado"
            int firstSaturday = -1;
            for(int i=0;i<yearDates.size();i++) {
                if(yearDates.get(i).getDayOfWeek() == DayOfWeek.SATURDAY) {
                    firstSaturday = i;
                    break;
                }
            }
            // Si no hay sabados, no tnemos nada que hacer
            if(firstSaturday == -1) continue;

            // Separamos en semanas...
            List<List<LocalDate>> weekendList = new ArrayList<>();

            for(int i=firstSaturday;i<yearDates.size();i+=7) {
                LocalDate first = yearDates.get(i);
                if(i!= yearDates.size()-1) {
                    LocalDate scnd = yearDates.get(i + 1);
                    if (first.getDayOfWeek() == DayOfWeek.SATURDAY && scnd.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        weekendList.add(Arrays.asList(first, scnd));
                    }
                }
            }


            int [] actualDays = adaptDayIndexes(weekendList,weekEnd);
            for (int i = 0; i < weekendList.size(); i++) {
                if (Arrays.binarySearch(actualDays, i+1) >= 0) {
                    allDates.addAll(weekendList.get(i));
                }
            }
        }
        return new DateExpression(week,fullWeks,allDates);
    }

    /**
     * Devuelve la expresión de fecha que permite seleeccionar aquellas fechas que se encuentran
     * dentro de una determinada quincena.
     *
     * @param fortNight Los números de quincena a recuperar.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression atFortnight(Integer ... fortNight) {
        if(fortNight == null ||fortNight.length == 0) {
            return new DateExpression(week,fullWeks,dates);
        }

        // Lo primero es convertir la expresión que tenemos a una expresión de semnas completas,
        DateExpression de = new DateExpression(week,fullWeks,getDatesAsStream());

        List<LocalDate> allDates = new ArrayList<>();

        for(int year: de.dates.keySet()) {
            List<LocalDate> yearDates = de.dates.get(year);
            List<List<LocalDate>> fortnightList = new ArrayList<>();
            for(int i=0;i<yearDates.size();i+=15) {
                fortnightList.add(yearDates.subList(i,Math.min(i+15,yearDates.size())));
            }

            int [] actualDays = adaptDayIndexes(fortnightList,fortNight);
            for (int i = 0; i < fortnightList.size(); i++) {
                if (Arrays.binarySearch(actualDays, i+1) >= 0) {
                    allDates.addAll(fortnightList.get(i));
                }
            }

        }
        return new DateExpression(week,fullWeks,allDates);
    }

    /**
     * Computa todas las fechas que tenemos entre esta expresión y otra expresión de destino, se considera
     * que son siempre expresiones inclusivas. Considera todas las fechas entre el primer elemento de este
     * conjunto y el último elemento del segundo conjunto.
     *
     * @param until  La expresión hasta la que incluimos datos.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression to(DateExpression until) {
        if(until == null) return this;

        List<LocalDate> fullSet = new ArrayList<>();
        for(int year: this.dates.keySet()) {
            List<LocalDate> fromSet = this.dates.get(year);
            List<LocalDate> toSet   = until.dates.get(year);
            if(fromSet !=null && toSet!=null && !fromSet.isEmpty() && !toSet.isEmpty()) {
                LocalDate from = fromSet.get(0);
                LocalDate target = toSet.get(toSet.size()-1);
                if(from.isAfter(target)) {
                    toSet   = until.dates.get(year+1);
                    if(toSet!=null && !toSet.isEmpty()) {
                        target = toSet.get(toSet.size()-1);
                    } else {
                        target = null;
                    }
                }
                if(target!=null) {
                    while (!from.isAfter(target)) {
                        fullSet.add(from);
                        from = from.plusDays(1);
                    }
                }
            }
        }
        return new DateExpression(week,fullWeks,fullSet);
    }

    /**
     * Genera una operación and (concatenación) entre varias expresiones de fechas, mantiene el conjunto
     * ordenado y con el nº de resultados mínimo.
     *
     * @param   expr    la expresión de fecha que vamos a adicionar
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression and(DateExpression expr) {
        return new DateExpression(week,fullWeks,Stream.concat(getDatesAsStream(),expr.getDatesAsStream()));
    }


    /**
     * Nos lleva todas las fechas una unidad temporal identificada por unit hacia atrás
     *
     * @param amount    La cantidad que decrementamos.
     * @param unit      La unidad en la que nos desplazamos
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression before(long amount, TemporalUnit unit) {
        return new DateExpression(week,fullWeks,this.getDatesAsStream().map(d->d.minus(amount,unit)));
    }

    /**
     * Nos lleva las fechas a un dia de la semana anterior a los datos indicados, puede provocar
     * que varias fechas se compacten, por ejemplo si tenemos un martes y un miercoles y queremos
     * llevarlos al primer lunes anterior ambas combinan en la misma fecha.
     *
     * @param weekDay   El día de la semana al que nos queremos ir
     * @param account   Cuantos días de la semana queremos ir hacia atrás
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression before(DayOfWeek weekDay, int account) {
        return new DateExpression(week,fullWeks,this.getDatesAsStream().map(d->{
            // Vamos a ver cuantos días le quedan;
            int add = weekDay.getValue() - d.getDayOfWeek().getValue();
            if(add < 0) add+=7;
            LocalDate after = d.plusDays(add);
            return after.minusWeeks(account);
        }));
    }

    /**
     * Nos lleva las fechas a un fin de semana anterior a los datos indicados, puede provocar
     * que varias fechas se compacten, por ejemplo si tenemos un martes y un miercoles y queremos
     * llevarlos al fin de semana anterior se van  copactar.
     *
     * @param account   Cuantos días de la semana queremos ir hacia atrás
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression beforeWeekend(int account) {
        return new DateExpression(week,fullWeks,before(DayOfWeek.SATURDAY,account).getDatesAsStream().flatMap(d->Stream.of(d,d.plusDays(1))));
    }


    /**
     * Nos lleva todas las fechas una unidad temporal idenrificada por unit hacia delante
     *
     * @param amount    La cantidad que decrementamos.
     * @param unit      La unidad temporal que vamos a considerar.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression after(long amount, TemporalUnit unit) {
        return new DateExpression(week,fullWeks,this.getDatesAsStream().map(d->d.plus(amount,unit)));
    }

    /**
     * Nos lleva las fechas a un dia de la semana posterior a los datos indicados, puede provocar
     * que varias fechas se compacten, por ejemplo si tenemos un martes y un miercoles y queremos
     * llevarlos al  3 lunes siguiente ambas combinan en la misma fecha.
     *
     * @param weekDay   El día de la semana al que nos queremos ir
     * @param account   Cuantos días de la semana queremos ir hacia atrás
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression after(DayOfWeek weekDay, int account) {
        return new DateExpression(week,fullWeks,this.getDatesAsStream().map(d->{
            // Vamos a ver cuantos días le quedan;
            int add = weekDay.getValue() - d.getDayOfWeek().getValue();
            if(add < 0) add+=7;
            return d.plusDays(add).plusWeeks(add == 0 ? account : account - 1);
        }));
    }

    /**
     * Nos lleva las fechas a un fin de semana poserior a los datos indicados, puede provocar
     * que varias fechas se compacten, por ejemplo si tenemos un martes y un miercoles y queremos
     * llevarlos al fin de semana posterior se van  copactar.
     *
     * @param account   Cuantos días de la semana queremos ir hacia atrás
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression afterWeekend(int account) {
        return new DateExpression(week,fullWeks,after(DayOfWeek.SATURDAY,account).getDatesAsStream().flatMap(d->Stream.of(d,d.plusDays(1))));
    }

    /**
     * Calcula la fecha más próxima como día de la semana a una pasada como parámetro
     *
     * @param weekDay   El día de la semana al que nos queremos aproximar
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression near(DayOfWeek weekDay) {
        return new DateExpression(week,fullWeks,this.getDatesAsStream().map(d->{
            if(d.getDayOfWeek() == weekDay) return d;
            // Vamos a ver cuantos días le quedan;
            int add = weekDay.getValue() - d.getDayOfWeek().getValue();
            if(add < 0) add+=7;
            LocalDate after = d.plusDays(add);
            LocalDate before = after.minusWeeks(1);

            if(ChronoUnit.DAYS.between(d,after) < ChronoUnit.DAYS.between(before,d)) {
                return after;
            }
            return before;
        }));

    }

    /**
     * Obtiene el fin de semana más próximo a las fechas pasadas... el sistema ordena, simplifica
     * y genera una expresión limitada.
     * @return Una nueva expresión de fecha, no se modifica la actual.
     */
    public DateExpression nearWeekend() {
        return new DateExpression(week,fullWeks,this.getDatesAsStream().flatMap(d->{
            if(d.getDayOfWeek() == DayOfWeek.SATURDAY) return Stream.of(d,d.plusDays(1));
            if(d.getDayOfWeek() == DayOfWeek.SUNDAY) return Stream.of(d,d.minusDays(1));
            // Vamos a ver cuantos días le quedan;
            int add = DayOfWeek.SATURDAY.getValue() - d.getDayOfWeek().getValue();
            if(add < 0) add+=7;

            LocalDate after = d.plusDays(add);
            LocalDate before = after.minusWeeks(1);

            if(ChronoUnit.DAYS.between(d,after) < ChronoUnit.DAYS.between(before,d) && ChronoUnit.DAYS.between(d,after.plusDays(1)) < ChronoUnit.DAYS.between(before.plusDays(1),d)) {
                return Stream.of(after,after.plusDays(1));
            }
            return Stream.of(before,before.plusDays(1));
        }));
    }


    /**
     * Interno, devuelve el flujo de fechas como un stream para facilitar el uso interno, evitando
     * generar listas innecesarias
     */
    private Stream<LocalDate> getDatesAsStream() {
        return this.dates.entrySet().stream().flatMap(e->e.getValue().stream());
    }

    /**
     * Resuelve los días, convirtiendo los días negativos a las posiciones correspondientes
     * del array (los da la vuelta) y ordenando los resultados para facilitar la busqueda
     * binaria.
     *
     * @param set   El conjunto de datos en los que buscamos
     * @param days  El array de días
     *
     * @return Los días adaptados y ordenados, ajsutando valores negativos.
     */
    private int [] adaptDayIndexes(List<?> set, Integer []days) {
        if(days == null || days.length == 0) return null;
        // Los dias negativos implican que nos colocamos al final de bloque de posición. (útimo, penultimo, ...)
        // logicamaente un nuevo array, puesto que el concepto de último (e.j. febrero u otro bloque, cambia por año)
        int [] actualDays = new int[days.length];
        for(int i=0;i<days.length;i++) {
            actualDays[i] = days[i];
            while(actualDays[i]<0) {
                // Imaginemos que tenemos 31 posiciones, la posición -1 será el último día, por tanto
                // Tamaño del array + la posición en i + 1 (para compensar el -1)
                actualDays[i] = set.size()+actualDays[i] +1;
            }
        }
        // Ordenamos los días.
        Arrays.sort(actualDays);
        return actualDays;
    }



    /**
     * Compara la expresión de fecha pasada con la expresión de fecha actual, verificando que se cumple
     * el código de operación para todos los casos. Genera una salida que evalua para cada año del origen
     * y el destino la condición indicada, con lo que la salida podrá combinar parte de los parámetros
     * onTruthy y onFalsy
     *
     * @param de        Expresión de fecha con la que verificamos la igualdad
     * @param opcode    Código de operación
     * @param onTruthy  Expresión de fecha que se utiliza para los resultados positivos
     * @param onFalsy   Expresión de fecha que se utiliza para los resultados negativos
     * @return Una nueva expresión de fecha.
     */
    public DateExpression compare(DateExpression de,Opcode opcode,
                                  DateExpression onTruthy,
                                  DateExpression onFalsy) {
        List<LocalDate> allDates = new ArrayList<>();

        for(int year: dates.keySet()) {
            boolean falsy = false;
            if(!de.dates.containsKey(year) || dates.get(year).size() != de.dates.get(year).size()) {
                falsy = true;
            } else {
                for (int i = 0; i < this.dates.get(year).size(); i++) {
                    LocalDate current = this.dates.get(year).get(i);
                    LocalDate target = de.dates.get(year).get(i);
                    if (!opcode.compare(current, target)) {
                        falsy = true;
                        break;
                    }
                }
            }
            List<LocalDate> dates;
            if(falsy) {
                dates = onFalsy.dates.get(year);
            } else {
                dates = onTruthy.dates.get(year);
            }
            if(dates != null) allDates.addAll(dates);
        }
        return new DateExpression(week,fullWeks,allDates);
    }

    /**
     * Indica si es una expresión aproximada
     *
     * @return  Un booleano indicando si es un dato exacto o aproximado
     */
    public boolean isApproximate() {
        return approximate;
    }

    /**
     * Permite modificar el valor del estado de aproximado
     *
     * @param approximate   Si es o no aproximada
     */
    public void setApproximate(boolean approximate) {
        this.approximate = approximate;
    }


    /**
     * Obtiene el conjunto de fechas final sobre el que podremos operar externamente.
     *
     * @return Las fechas internas almacenadas por esta expresión
     */
    public List<LocalDate> getDates() {
        return getDatesAsStream().collect(Collectors.toList());
    }

    /**
     * Método estático que genera una expresión de fecha a partir de una expresión,
     * utiliza la gramática para resolver la misma
     *
     * @param locale    El idioma asociado
     * @param zoneId    La zona horaria
     * @param fullWeeks Si el computo de semanas es siempre de semana completa, es decir
     *                  si decimos primera semana de abril y el día 1 cae en jueves, la primera
     *                  semana empiza el día 5 (en función del locale, en US el día 4)
     * @param years     Para cuantos años resolvemos la expresión
     * @param expr      Texto con la expresión a resolver.
     *
     * @return Una expresión de fecha a partir de la gramática.
     */
    public static DateExpression parse(Locale locale, ZoneId zoneId, boolean fullWeeks, int years,
                                       String expr) {
        return parse(locale,zoneId,fullWeeks,LocalDate.now(zoneId).getYear(),years,expr);

    }

    /**
     * Método estático que genera una expresión de fecha a partir de una expresión,
     * utiliza la gramática para resolver la misma
     *
     * @param locale    El idioma asociado
     * @param zoneId    La zona horaria
     * @param fullWeeks Si el computo de semanas es siempre de semana completa, es decir
     *                  si decimos primera semana de abril y el día 1 cae en jueves, la primera
     *                  semana empiza el día 5 (en función del locale, en US el día 4)
     * @param firstYear El año con el que comenzamos
     * @param years     Para cuantos años resolvemos la expresión
     * @param expr      Texto con la expresión a resolver.
     *
     * @return Una expresión de fecha a partir de la gramática.
     */
    public static DateExpression parse(Locale locale, ZoneId zoneId, boolean fullWeeks, int firstYear,
                                       int years,
                                       String expr) {
        DateExpressionVisitor dev = new DateExpressionVisitor(zoneId,locale,firstYear,years,fullWeeks);

        com.proxiasuite.dateparser.grammar.DateExpressionGrammarParser pp =
                new com.proxiasuite.dateparser.grammar.DateExpressionGrammarParser(new CommonTokenStream(new com.proxiasuite.dateparser.grammar.DateExpressionGrammarLexer(CharStreams.fromString(expr))));
        pp.addParseListener(dev);
        pp.prog();
        return dev.getDateExpression();


    }



}
