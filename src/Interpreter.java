import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Interpreter {

    private ArrayList<Command> commands;

    public Interpreter(ArrayList<Command> commands){
        this.commands = commands;
    }

    public void exec(){

        System.out.println("... exec ...");

        Map<String, Integer> vars = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        int cmdIndex = 0;
        while ( cmdIndex < commands.size() )
        {
            Command cmd = commands.get(cmdIndex++);
            switch ( cmd.getType() )
            {
                case VAR_S:{
                    String str = cmd.getStringVal();
                    if ( str == null ) throw new InterpreterException("wrong command");
                    vars.put(str, 0);
                }break;

                case SAVE_I:{
                    Integer val = cmd.getIntVal();
                    if ( val == null ) throw new InterpreterException("wrong command");
                    stack.push(val);
                }break;

                case SAVE_S:{
                    String key = cmd.getStringVal();
                    if ( key == null ) throw new InterpreterException("wrong command");
                    Integer val = vars.get(key);
                    if ( val == null ) throw new InterpreterException("unknown variable (" + key + ")");
                    stack.push(val);
                }break;

                case LOAD_S:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    String key = cmd.getStringVal();
                    if ( key == null ) throw new InterpreterException("wrong command");
                    Integer val = vars.get(key);
                    if ( val == null ) throw new InterpreterException("unknown variable");
                    vars.put(key, stack.pop());
                }break;

                case IFGOTO_I:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int cond = stack.pop();
                    Integer val = cmd.getIntVal();
                    if ( val == null ) throw new InterpreterException("wrong command");
                    if ( cond != 0 ) cmdIndex = val;
                }break;

                case GOTO_I:{
                    Integer val = cmd.getIntVal();
                    if ( val == null ) throw new InterpreterException("wrong command");
                    cmdIndex = val;
                }break;

                case PRINT_I:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    System.out.println(stack.pop());
                }break;

                case PLUS:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push(a + b);
                }break;

                case BIN_MINUS:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push(a - b);
                }break;

                case MULT:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push(a * b);
                }break;

                case DIV:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push(a / b);
                }break;

                case AND:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    boolean b = stack.pop() != 0;
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    boolean a = stack.pop() != 0;
                    stack.push(a && b ? 1 : 0 );
                }break;

                case OR:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    boolean b = stack.pop() != 0;
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    boolean a = stack.pop() != 0;
                    stack.push(a || b ? 1 : 0 );
                }break;

                case NOT:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    boolean b = stack.pop() != 0;
                    stack.push( !b ? 1 : 0 );
                }break;

                case EQUALS:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push( a == b ? 1 : 0 );
                }break;

                case BIGGER:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push( a > b ? 1 : 0 );
                }break;

                case LESS:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int a = stack.pop();
                    stack.push( a < b ? 1 : 0 );
                }break;

                case UN_MINUS:{
                    if ( stack.isEmpty() ) throw new InterpreterException("trying to load from empty stack");
                    int b = stack.pop();
                    stack.push(-b);
                }break;

                default:
                    throw new InterpreterException("unknown command");
            }
        }
        /// print all vars???
    }

}
