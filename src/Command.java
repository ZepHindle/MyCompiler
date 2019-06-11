public class Command {

    public enum Type{
        VAR_S,    // добавляет в таблицу переменных переменную с именем, задающимся аргументом
        SAVE_I,   // загружает в стек свой аргумент (целое)
        SAVE_S,   // загружает в стек переменную из таблицы с ключем, задающимся аргументом
        LOAD_S,   // записывает в переменную из таблицы с ключем, задающимся аргументом, вершину стека
        IFGOTO_I, // если на вершине стека ненулевое значение, то переходит к команде с номером, задающимся аргументом
        GOTO_I,   // переходит к команде с номером, задающимся аргументом
        PRINT_I,  // выводит на экран вершину стека
        // следующие команды берут аргмуенты из стека,
        // выполняют операцию и помещают результат в стек

        PLUS,
        BIN_MINUS,
        MULT,
        DIV,

        AND,
        OR,
        NOT,

        EQUALS,
        BIGGER,
        LESS,

        UN_MINUS
    }

    private Type type;
    private Object val;

    public Command(Type type, Object val){
        this.type = type;
        this.val = val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public Type getType() {
        return type;
    }

    public String getStringVal(){
        return (val.getClass() == String.class) ? (String) val : null;
    }

    public Integer getIntVal(){
        return (val.getClass() == Integer.class) ? (Integer) val : null;
    }

    public String toString(){
        return "{" + getType() + ", " + val + "}";
    }
}
