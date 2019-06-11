import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.lang.reflect.Array;
import java.util.*;
import java.util.LinkedList;

public class Parser {

    ArrayList<Command> commands = new ArrayList<>();

    private List<Token> tokens;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    private Token getNextToken(){
        if ( tokens.isEmpty() ) return null;
        return tokens.get(0);
    }

    private Token readNextToken(){
        if ( tokens.isEmpty() ) return null;
        Token res = tokens.get(0);
        tokens.remove(0);
        return res;
    }

    private void returnToken(Token token){
        tokens.add(0, token);
    }

    private boolean hasTokens(){
        return !tokens.isEmpty();
    }

    public ArrayList<Command> compile(){
        while ( hasTokens() )
        {
            compileStatement();

            if ( !hasTokens() )
                throw new ParserException("semicolon expected (null)");

            Token tmp = readNextToken();
            if ( tmp.getType() != TokenType.SEMICOLON )
                throw new ParserException("semicolon expected (" + tmp + ")");

        }
        return commands;
    }

    public void compileStatement(){
        // не убираем текущий токен!
        // это нужно для compileAssign, чтобы
        // там можно было получить имя идентификатора
        Token token = getNextToken();
        switch ( token.getType() )
        {
            case VAR_KW:{
                compileVar();
            }break;

            case WHILE_KW:{
                compileWhile();
            }break;

            case IDENTIFIER:{
                compileAssign();
            }break;

            case IF_KW:{
                compileIf();
            }break;

            case PRINT_KW:{
                compilePrint();
            }break;

            case OPEN_CURLY_BRACKET:{
                readNextToken(); // пропускаем открывающуюся фигурную скобку
                while (true)
                {
                    if ( !hasTokens() ) throw new ParserException("statement expected");
                    Token t = getNextToken();
                    if ( t.getType() == TokenType.CLOSE_CURLY_BRACKET ) {
                        readNextToken(); // пропускаем закрывающуюся фигурную скобку
                        break;
                    }
                    compileStatement();
                    if ( readNextToken().getType() != TokenType.SEMICOLON )
                        throw new ParserException("semicolon expected");
                }
            }break;

            default:
                throw new ParserException("statement expected (" + token + ")");
        }
    }

    public void compileVar(){
        readNextToken(); // пропускаем VAR_KW
        Token t = readNextToken();
        if ( t.getType() != TokenType.IDENTIFIER )
            throw new ParserException("identifier expected");

        String name = t.getValue();

        commands.add( new Command(Command.Type.VAR_S, name) );
    }

    public void compileWhile(){
        readNextToken(); // пропускаем WHILE_KW
        if ( readNextToken().getType() != TokenType.OPEN_ROUND_BRACKET )
            throw new ParserException("\"(\" expected");
        int startWhile = commands.size(); // сохраняем позицию для goto (позиция сразу после последней добавленной команды)

        compileExpr();

        if ( !hasTokens() )
            throw new ParserException("\")\" expected (null)");

        Token tmp = readNextToken();
        if ( tmp.getType() != TokenType.CLOSE_ROUND_BRACKET )
            throw new ParserException("\")\" expected (" + tmp + ")");

        commands.add(new Command(Command.Type.NOT, null)); // инвертируем условие для ifgoto

        Command cmdGotoEnd = new Command(Command.Type.IFGOTO_I, null); // пока не знаем, куда переходить
        commands.add(cmdGotoEnd);

        compileStatement();

        commands.add(new Command(Command.Type.GOTO_I, startWhile)); // переход в начало цикла

        cmdGotoEnd.setVal( commands.size() ); // нужная позиция - сразу после последней на данным момент команды

    }

    public void compileAssign(){

        Token ident = readNextToken();

        if ( readNextToken().getType() != TokenType.ASSIGN_S )
            throw new ParserException("\"=\" expected");

        compileExpr();

        commands.add( new Command(Command.Type.LOAD_S, ident.getValue()) );
    }

    public void compileIf(){

        readNextToken(); // пропускаем IF_KW

        if ( readNextToken().getType() != TokenType.OPEN_ROUND_BRACKET )
            throw new ParserException("\"(\" expected");

        compileExpr();

        if ( readNextToken().getType() != TokenType.CLOSE_ROUND_BRACKET )
            throw new ParserException("\")\" expected");


        commands.add(new Command(Command.Type.NOT, null)); // инвертируем условие для ifgoto

        Command cmdGotoSt2 = new Command(Command.Type.IFGOTO_I, null); // пока не знаем, куда переходить
        commands.add(cmdGotoSt2);

        compileStatement();

        Command cmdGotoEnd = new Command(Command.Type.GOTO_I, null); // пока не знаем, где конец
        commands.add(cmdGotoEnd);

        cmdGotoSt2.setVal( commands.size() ); // нужно перейти сразу после последней на данный момент команды

        Token tmp = readNextToken();
        if ( tmp.getType() != TokenType.ELSE_KW )
            throw new ParserException("\"else\" expected (" + tmp + ")");

        compileStatement();

        cmdGotoEnd.setVal( commands.size() ); // нужно перейти сразу после последней на данный момент команды

    }

    public void compilePrint(){
        readNextToken(); // пропускаем PRINT_KW

        compileExpr();

        commands.add( new Command(Command.Type.PRINT_I, null) );
    }

    public void compileExpr(){

        List<Token> out = new LinkedList<>();
        Stack<Token> stack = new Stack<>();
        int bracketCount = 0;
        HashSet<TokenType> types = new HashSet<>(); // типы токенов, которые могут присутствовать в выражении
        types.add( TokenType.IDENTIFIER );
        types.add( TokenType.OPEN_ROUND_BRACKET );
        types.add( TokenType.CLOSE_ROUND_BRACKET );
        types.add( TokenType.LITERAL );
        types.add( TokenType.PLUS_S );
        types.add( TokenType.MINUS_S );
        types.add( TokenType.MULT_S );
        types.add( TokenType.DIV_S );
        types.add( TokenType.AND_S );
        types.add( TokenType.OR_S );
        types.add( TokenType.NOT_S );
        types.add( TokenType.BIGGER_S );
        types.add( TokenType.LESS_S );
        types.add( TokenType.EQUALS_S );

        Map<TokenType, Integer> priorities = new HashMap<>();
        priorities.put( TokenType.MULT_S, 6 );
        priorities.put( TokenType.DIV_S, 6 );
        priorities.put( TokenType.PLUS_S, 5 );
        priorities.put( TokenType.MINUS_S, 5 );
        priorities.put( TokenType.BIGGER_S, 4 );
        priorities.put( TokenType.LESS_S, 4 );
        priorities.put( TokenType.EQUALS_S, 3 );
        priorities.put( TokenType.AND_S, 2 );
        priorities.put( TokenType.OR_S, 1 );

        Set<TokenType> unaryOperations = new HashSet<>();
        unaryOperations.add( TokenType.NOT_S );
        unaryOperations.add( TokenType.UN_MINUS );

        // флаг для определения унарности минуса
        boolean flag = true;

        while (hasTokens())
        {
            Token t = getNextToken();
            TokenType type = t.getType();

            if ( type == TokenType.CLOSE_ROUND_BRACKET )
                bracketCount--;

            if ( type == TokenType.OPEN_ROUND_BRACKET )
                bracketCount++;

            if ( !types.contains(type) || bracketCount < 0) break;
            readNextToken(); // пропускаем токен t

            // если минус, то проверяем, унарный ли он
            if ( type == TokenType.MINUS_S && flag )
            {
                t = new Token(TokenType.UN_MINUS, "-");
                type = t.getType();
            }

            flag = priorities.containsKey(type) || unaryOperations.contains(type) || type == TokenType.OPEN_ROUND_BRACKET;


            if ( type == TokenType.LITERAL || type == TokenType.IDENTIFIER ){
                out.add(t);
                continue;
            }

            if ( unaryOperations.contains(type) )
            {
                stack.push( t );
                continue;
            }

            if ( priorities.containsKey(t.getType()) )
            {
                while ( !stack.isEmpty() && priorities.containsKey(stack.peek().getType())
                        && priorities.get(t.getType()) <= priorities.get(stack.peek().getType()) )
                    out.add( stack.pop() );
                stack.push( t );
                continue;
            }

            if ( t.getType() == TokenType.OPEN_ROUND_BRACKET )
            {
                stack.push(t);
                continue;
            }

            if ( t.getType() == TokenType.CLOSE_ROUND_BRACKET )
            {
                while ( !stack.isEmpty() && stack.peek().getType() != TokenType.OPEN_ROUND_BRACKET )
                    out.add( stack.pop() );

                if ( !stack.isEmpty() )
                    stack.pop();
                else throw new ParserException("brackets mismatch");

                while ( !stack.isEmpty() && unaryOperations.contains(stack.peek().getType()) )
                    out.add( stack.pop() );
            }

        }

        while ( !stack.isEmpty() )
        {
            if ( priorities.containsKey(stack.peek().getType()) || unaryOperations.contains(stack.peek().getType()) )
                out.add(stack.pop());
            else stack.pop();
        }

        // генерируем команды

        for ( Token t : out )
        switch ( t.getType() )
        {
            case IDENTIFIER:{
                commands.add( new Command(Command.Type.SAVE_S, t.getValue()) );
            }break;

            case LITERAL:{
                int val = 0;
                try{
                    val = Integer.parseInt(t.getValue());
                } catch (NumberFormatException ex)
                {
                    throw new ParserException("cant parse literal");
                }
                commands.add( new Command(Command.Type.SAVE_I, val) );
            }break;

            case PLUS_S:{
                commands.add( new Command(Command.Type.PLUS, null) );
            }break;

            case MINUS_S:{
                commands.add( new Command(Command.Type.BIN_MINUS, null) );
            }break;

            case MULT_S:{
                commands.add( new Command(Command.Type.MULT, null) );
            }break;

            case DIV_S:{
                commands.add( new Command(Command.Type.DIV, null) );
            }break;

            case AND_S:{
                commands.add( new Command(Command.Type.AND, null) );
            }break;

            case OR_S:{
                commands.add( new Command(Command.Type.OR, null) );
            }break;

            case NOT_S:{
                commands.add( new Command(Command.Type.NOT, null) );
            }break;

            case UN_MINUS:{
                commands.add( new Command(Command.Type.UN_MINUS, null) );
            }break;

            case BIGGER_S:{
                commands.add( new Command(Command.Type.BIGGER, null) );
            }break;

            case LESS_S:{
                commands.add( new Command(Command.Type.LESS, null) );
            }break;

            case EQUALS_S:{
                commands.add( new Command(Command.Type.EQUALS, null) );
            }break;

            default:
                throw new ParserException("unexpected token");
        }

    }
}
