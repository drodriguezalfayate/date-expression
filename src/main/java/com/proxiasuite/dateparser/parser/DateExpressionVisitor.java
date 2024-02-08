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
package com.proxiasuite.dateparser.parser;

import com.proxiasuite.dateparser.resolver.DateType;
import com.proxiasuite.dateparser.resolver.IDateResolver;
import com.proxiasuite.dateparser.DateExpression;
import com.proxiasuite.dateparser.grammar.DateExpressionGrammarBaseListener;
import com.proxiasuite.dateparser.grammar.DateExpressionGrammarParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * Realiza la interpretación de la gramática. El proceso de interpretación sigue
 * el esquema de una máquina de pila clásica, en la que se van introduciendo
 * información en la pila y es procesado en iteraciones siguientes.
 *
 * @author David Rodríguez Alfayate 
 */
public class DateExpressionVisitor extends DateExpressionGrammarBaseListener {
    /**
     * Diccionario en el que almacenamos las variables definidas
     */
    Map<String, DateExpression> dictionary = new HashMap<>();

    /**
     * La de pila que mantiene el estado de las expresiones de fecha.
     */
    Deque<Deque<Object>> globalStack = new ArrayDeque<>();

    /**
     * La expresión de fecha procesada
     */
    DateExpression result;

    /**
     * Numero de años que vamos a periodificar la expresión
     */
    private final int years;

    /**
     * Si soportamos semanas completas o parciales
     */
    private final boolean fullWeeks;

    /**
     * Zona horaria de trabajo
     */
    private final ZoneId zoneId;

    /**
     * Idioma de trabajo
     */
    private final Locale locale;

    /**
     * El primer año
     */
    private final int firstYear;

    public DateExpressionVisitor(ZoneId zoneId, Locale locale, int firstYear, int years, boolean fullWeeks) {
        this.years = years;
        this.fullWeeks = fullWeeks;
        this.zoneId = zoneId;
        this.locale = locale;
        this.firstYear = firstYear;
    }

    /**
     * Una vez terminado el procesamiento noes devuelve la expresión de fecha que
     * se ha resuelto, extractandola de la pila
     *
     * @return  La expresión de fecha reseulta.
     */
    public DateExpression getDateExpression() {
        return result;
    }

    @Override
    public void exitProg(DateExpressionGrammarParser.ProgContext ctx) {
        Object o = getStack().poll();
        if(o instanceof DateExpression) {
            result = (DateExpression) o;
        }
    }

    @Override
    public void exitLogic(DateExpressionGrammarParser.LogicContext ctx) {

        DateExpression main = null;
        DateExpression compared = null;
        DateExpression positive = null;
        DateExpression negative = null;
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            // La lógica la definen 4 variables.
            // Primera que extraemos de la pila -> Condición si no
            // Segunda que extraemos de la pila -> Condición SI
            // Terera que extreamos de la pila -> Destino de la comparación
            // Cuarta que extraemos de la pila -> Objeto sobre el que que comparamos.
            if (o instanceof DateExpression) {
                if(negative == null) negative = (DateExpression)o;
                else if(positive == null) positive = (DateExpression)o;
                else if(compared == null) compared = (DateExpression)o;
                else main = (DateExpression)o;
            }
        }
        DateExpression.Opcode opcode = DateExpression.Opcode.EQUALS;
        if(ctx.Greater()!=null) opcode = DateExpression.Opcode.GREATER;
        else if(ctx.Lesser()!=null) opcode = DateExpression.Opcode.LESSER;

        getStack().push(main.compare(compared,opcode,positive,negative));

    }

    @Override
    public void exitDef(DateExpressionGrammarParser.DefContext ctx) {
        DateExpression expr = null;
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            if (o instanceof DateExpression) {
                expr = (DateExpression) o;
            }
        }
        // Tenemos que asignar esa variable al diccionario.
        dictionary.put(ctx.ID().getText(),expr);
    }

    @Override
    public void exitExpr(DateExpressionGrammarParser.ExprContext ctx) {
        DateExpression expr = null;
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            if (o instanceof DateExpression) {
                if(expr == null) {
                    expr = (DateExpression) o;
                } else {
                    expr = expr.and((DateExpression)o);
                }
            }
        }
        getStack().push(expr);
    }

    @Override
    public void exitPeriod(DateExpressionGrammarParser.PeriodContext ctx) {
        DateExpression from = null;
        DateExpression to = null;
        while(!getStack().isEmpty()) {
            // Tenemos que tener en cuenta que la pila se recorre al reves,
            // es decir... primero tenemos el FROm y luego el TO pero al sacar
            // d la pila sacamos primero el TO y luego el FROM
            Object o = getStack().poll();
            if (o instanceof DateExpression) {
                if (to == null) to = (DateExpression) o;
                else from = (DateExpression) o;
            }
        }
        if(from == null) {
            getStack().push(to);
        } else {
            getStack().push(from.to(to));
        }
    }

    @Override
    public void exitBefore(DateExpressionGrammarParser.BeforeContext ctx) {
        // En la pila estan todas las posibilidades, podemos tener un día
        // un día de la semana (weekDay) una expresión
        Integer amount = 1;
        DayQualifierType qualifierType = null;
        DateExpression expr = null;
        // Regulador de fechas que por defecto será 1 día.
        TemporalUnit unit = ChronoUnit.DAYS;
        if(ctx.Month()!=null) unit = ChronoUnit.MONTHS;
        if(ctx.Week()!=null) unit = ChronoUnit.WEEKS;

        // Rellneamos con la pila.
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            if(o instanceof DayQualifierType) {
                qualifierType = (DayQualifierType) o;
            } else if(o instanceof DateExpression) {
                expr = (DateExpression) o;
            } else if(o instanceof Integer) {
                amount = (Integer)o;
            }
        }

        // Ahora tenemos que decidir que tipo de operación after aplicamos
        // en función de los datos.
        if(qualifierType==null) {
            expr = expr.before(amount,unit);
        } else if(qualifierType.dayOfWeek != null){
            expr = expr.before(qualifierType.dayOfWeek,amount);
        } else if(qualifierType == DayQualifierType.Weekend) {
            expr = expr.beforeWeekend(amount);
        }
        // Lo recolocamos en la pila...
        getStack().push(expr);
    }

    @Override
    public void exitAfter(DateExpressionGrammarParser.AfterContext ctx) {
        // En la pila estan todas las posibilidades, podemos tener un día
        // un día de la semana (weekDay) una expresión
        Integer amount = 1;
        DayQualifierType qualifierType = null;
        DateExpression expr = null;
        // Regulador de fechas que por defecto será 1 día.
        TemporalUnit unit = ChronoUnit.DAYS;
        if(ctx.Month()!=null) unit = ChronoUnit.MONTHS;
        if(ctx.Week()!=null) unit = ChronoUnit.WEEKS;

        // Rellneamos con la pila.
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            if(o instanceof DayQualifierType) {
                qualifierType = (DayQualifierType) o;
            } else if(o instanceof DateExpression) {
                expr = (DateExpression) o;
            } else if(o instanceof Integer) {
                amount = (Integer)o;
            }
        }

        // Ahora tenemos que decidir que tipo de operación after aplicamos
        // en función de los datos.
        if(qualifierType==null) {
            expr = expr.after(amount,unit);
        } else if(qualifierType.dayOfWeek != null){
            expr = expr.after(qualifierType.dayOfWeek,amount);
        } else if(qualifierType == DayQualifierType.Weekend) {
            expr = expr.afterWeekend(amount);
        }
        // Lo recolocamos en la pila...
        getStack().push(expr);
    }

    @Override
    public void exitNear(DateExpressionGrammarParser.NearContext ctx) {
        // Tenemos que tener en el contexto un día de la semana y una expresión
        // de fecha, vaciamos la pila...
        DayQualifierType dayType = null;
        DateExpression expr = null;
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            if(o instanceof DayQualifierType) {
                dayType = (DayQualifierType) o;
            } else if(o instanceof DateExpression) {
                expr = (DateExpression) o;
            }
        }
        if(dayType == DayQualifierType.Weekend) {
            expr = expr.nearWeekend();
        } else if(dayType.dayOfWeek != null) {
            expr = expr.near(dayType.dayOfWeek);
        }
        // Lo recolocamos en la pila...
        getStack().push(expr);

    }


    @Override
    public void exitAt(DateExpressionGrammarParser.AtContext ctx) {
        // Sobrecargamos el exit, porque ya ha introducido en la pila las operaciones
        // correspondientes a los días, a los posibles días de la semana y la
        // expresión de fecha sobre la que trabajamos.
        List<Integer> days = new ArrayList<>();
        List<DayQualifierType> dayQualifiers = new ArrayList<>();
        DateExpression expr = null;
        while(!getStack().isEmpty()) {
            Object o = getStack().poll();
            if(o instanceof Integer) {
                days.add((Integer)o);
            } else if(o instanceof DayQualifierType) {
                dayQualifiers.add((DayQualifierType)o);
            } else if(o instanceof DateExpression) {
                expr = (DateExpression)o;
            }
        }
        // Tenemos que aplicar sobre la expresión la condición de "at" que tengamos
        // determinada.
        if(dayQualifiers.stream().anyMatch(d->d == DayQualifierType.Week)) {
            expr = expr.atWeek(days.toArray(new Integer[0]));
        } else if(dayQualifiers.stream().anyMatch(d->d == DayQualifierType.Weekend)) {
            expr = expr.atWeekend(days.toArray(new Integer[0]));
        } else if(dayQualifiers.stream().anyMatch(d->d == DayQualifierType.Fortnight)) {
            expr = expr.atFortnight(days.toArray(new Integer[0]));
        } else if(!dayQualifiers.isEmpty()) {
            expr = expr.at(dayQualifiers.stream().filter(d->d.dayOfWeek!=null).map(d->d.dayOfWeek).toArray(DayOfWeek[]::new),
                           days.toArray(new Integer[0]));
        } else {
            expr = expr.at(days.toArray(new Integer[0]));
        }
        // Lo recolocamos en la pila...
        getStack().push(expr);
    }

    @Override
    public void exitDay(DateExpressionGrammarParser.DayContext ctx) {
        TerminalNode tn = ctx.getChild(TerminalNode.class,0);
        int numberType = tn.getSymbol().getType();
        if(numberType == DateExpressionGrammarParser.NUMBER) {
            getStack().push(Integer.parseInt(tn.getText()));
        } else if(numberType == DateExpressionGrammarParser.Last || numberType == DateExpressionGrammarParser.Yesterday) {
            getStack().push(-1); // Construcciones espeicales del DatExpression
        } else if(numberType == DateExpressionGrammarParser.Prelast) {
            getStack().push(-2); // Construcciones espeicales del DatExpression
        } else {
            // Nos basamos en que son consecutivos desde el 1, no es que me "guste"
            // mucho, pero un switch de 32 valores es infumable.
            getStack().push(numberType - DateExpressionGrammarParser.One  +1);
        }

    }

    @Override
    public void exitWeekday(DateExpressionGrammarParser.WeekdayContext ctx) {
        if(ctx.baseDay()!=null) return;
        getStack().push(DayQualifierType.decode(ctx.getChild(TerminalNode.class,0)));
    }

    @Override
    public void exitBaseDay(DateExpressionGrammarParser.BaseDayContext ctx) {
        TerminalNode tn = ctx.getChild(TerminalNode.class,0);
        getStack().push(DayQualifierType.decode(tn));
    }

    @Override
    public void exitAtom(DateExpressionGrammarParser.AtomContext ctx) {
        if(ctx.month()!=null) {
            // Ya esta procesado, volvemos.
            return;
        }
        TerminalNode tn = ctx.getChild(TerminalNode.class,0);
        DateType dateType  = null;
        switch(tn.getSymbol().getType()) {
            case DateExpressionGrammarParser.HolyWeek:
                dateType = DateType.HolyWeek;
                break;
            case DateExpressionGrammarParser.Carnival:
                dateType = DateType.Carnival;
                break;
            case DateExpressionGrammarParser.AshWednesday:
                dateType = DateType.AshWednesday;
                break;
            case DateExpressionGrammarParser.CorpusChristi:
                dateType = DateType.CorpusChristi;
                break;
            case DateExpressionGrammarParser.Ascension:
                dateType = DateType.Ascension;
                break;
            case DateExpressionGrammarParser.Pentecost:
                dateType = DateType.Pentecost;
                break;
            case DateExpressionGrammarParser.Easter:
                dateType = DateType.Easter;
                break;
            case DateExpressionGrammarParser.ID:
                getStack().push(dictionary.get(tn.getText()));
                break;

        }
        if(dateType != null) {
            getStack().push(IDateResolver.getInstance(dateType).resolve(firstYear,years,fullWeeks,locale,zoneId));
        }


    }

    @Override
    public void exitMonth(DateExpressionGrammarParser.MonthContext ctx) {
        TerminalNode tn = ctx.getChild(TerminalNode.class,0);
        DateType dateType  = null;
        switch(tn.getSymbol().getType()) {
            case DateExpressionGrammarParser.January:
                dateType = DateType.January;
                break;
            case DateExpressionGrammarParser.February:
                dateType = DateType.February;
                break;
            case DateExpressionGrammarParser.March:
                dateType = DateType.March;
                break;
            case DateExpressionGrammarParser.April:
                dateType = DateType.April;
                break;
            case DateExpressionGrammarParser.May:
                dateType = DateType.May;
                break;
            case DateExpressionGrammarParser.Jun:
                dateType = DateType.Jun;
                break;
            case DateExpressionGrammarParser.July:
                dateType = DateType.July;
                break;
            case DateExpressionGrammarParser.August:
                dateType = DateType.August;
                break;
            case DateExpressionGrammarParser.September:
                dateType = DateType.September;
                break;
            case DateExpressionGrammarParser.October:
                dateType = DateType.October;
                break;
            case DateExpressionGrammarParser.November:
                dateType = DateType.November;
                break;
            case DateExpressionGrammarParser.December:
                dateType = DateType.December;
                break;

        }
        if(dateType != null) {
            getStack().push(IDateResolver.getInstance(dateType).resolve(firstYear,years,fullWeeks,locale,zoneId));
        }
    }
    
    Deque<Object> getStack() {
        return globalStack.peek();
    }

    /**
     * Sobrergamos estos dos métodos para gestionar la pila interna de llamadas
     * por función, la entrada genera una nueva pila
     */
    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        globalStack.push(new ArrayDeque<>());
    }

    /**
     * Sobrergamos estos dos métodos para gestionar la pila interna de llamadas
     * por función, la salida obtiene de la pila actual el último valor y lo
     * introduce en la pila de nivel superior
     */
    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        Deque<Object> currentStack = globalStack.poll();
        Deque<Object> parentStack = globalStack.peek();
        if(parentStack!=null && currentStack.peek()!=null) {
            Object returned = currentStack.poll();
            parentStack.push(returned);
        }
    }
}
