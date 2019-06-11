import java.util.Objects;

public class LinkedList<T> {

    private static class Node<T>{
        private Node<T> prev;
        private Node<T> next;
        private T obj;

        public Node(T obj){
            prev = null;
            next = null;
            this.obj = obj;
        }

        public Node(Node<T> prev, Node<T> next,T obj){
            this.prev = prev;
            this.next = next;
            this.obj = obj;
        }

        public Node<T> getNext() {
            return next;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public T getObj() {
            return obj;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        public void setObj(T obj) {
            this.obj = obj;
        }
    }

    public static class Iterator<T>{
        private Node<T> node;

        private Iterator(Node<T> node){
            this.node = node;
        }

        public boolean isEmpty(){
            return node == null;
        }

        public void next(){
            if ( isEmpty() ) throw new LinkedListException();
            node = node.getNext();
        }

        public void prev(){
            if ( isEmpty() ) throw new LinkedListException();
            node = node.getPrev();
        }

        public T getObject(){
            if ( isEmpty() ) throw new LinkedListException();
            return node.getObj();
        }

    }

    private Node<T> first;
    private Node<T> last;
    private int count;

    // CONSTRUCTORS

    public LinkedList(){
        first = last = null;
        count = 0;
    }

    // MODIFIERS

    public void addBegin(T obj){

        if ( isEmpty() ) {
            addFirst(obj);
            return;
        }

        Node<T> newNode = new Node<>(obj);
        first.setPrev(newNode);
        newNode.setNext(first);
        first = newNode;
        count++;
    }

    public void addEnd(T obj){

        if ( isEmpty() ) {
            addFirst(obj);
            return;
        }

        Node<T> newNode = new Node<T>(obj);
        last.setNext( newNode );
        newNode.setPrev( last );
        last = newNode;
        count++;
    }

    public void addAfter(Iterator<T> iter, T obj){

        // предполагается, что т.к. элементов нет, то итератор должен указывать на NULL
        if ( isEmpty() ) {
            addFirst(obj);
            return;
        }

        // добавляем в конец
        if ( iter.node == last ){
            addEnd(obj);
            return;
        }

        Node<T> newNode = new Node<>(obj);
        Node<T> iNext = iter.node.getNext();

        iNext.setPrev( newNode );
        iter.node.setNext(newNode);
        newNode.setNext(iNext);
        newNode.setPrev(iter.node);
        count++;
    }

    public void remove(Iterator<T> iter){

        Node<T> node = iter.node;

        // удаляем единственный элемент
        if ( node == first &&  node == last)
        {
            last = first = null;
            count = 0;
            return;
        }

        if ( node == first ){
            first = first.next;
            first.setPrev(null);
            count--;
            return;
        }

        if ( node == last ){
            last = last.prev;
            last.setNext(null);
            count--;
            return;
        }

        Node<T> iPrev = node.getPrev();
        Node<T> iNext = node.getNext();
        iPrev.setNext( iNext );
        iNext.setPrev( iPrev );
        count--;
    }

    public void removeFirst(){

        if ( last == first ){
            last = first = null;
            count = 0;
            return;
        }

        first = first.next;
        first.setPrev(null);

        count--;
    }

    public void removeLast(){

        if ( last == first ){
            last = first = null;
            count = 0;
            return;
        }

        last = last.prev;
        last.setNext(null);

        count--;
    }

    private void addFirst(T obj){
        first = last = new Node<>(obj);
        count++;
    }

    // GETTERS

    public boolean isEmpty(){
        return first == null;
    }

    public int getCount() {
        return count;
    }

    public Iterator<T> getFirst(){
        return new Iterator<>(first);
    }

    public Iterator<T> getLast(){
        return new Iterator<>(last);
    }

    public Iterator<T> find(T obj){
        Iterator<T> iter = getFirst();
        for ( ; !iter.isEmpty(); iter.next() )
            if (Objects.equals(iter.getObject(), obj) ) break;
        return iter;
    }

    // OVERRIDE

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for ( Iterator<T> iter = getFirst(); !iter.isEmpty(); iter.next() )
            sb.append(iter.getObject().toString() + " , ");

        sb.replace(sb.length()-3, sb.length(), "" ); // WARNING: KOSTIL

        sb.append("]");
        return sb.toString();
    }
}
