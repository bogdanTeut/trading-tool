import java.util.Iterator;
import java.util.ListIterator;

public class SList<E> {

    private Link<E> lastElement;
    private Link<E> firstElement;
    private int size;

    private class SIterator implements ListIterator<E> {

        private Link<E> currentElement;

        @Override
        public void add(E value){
            if (size ==0){
                firstElement = lastElement = new Link<E>(value);
            }else{
                Link<E> nextElement = new Link<E>(value);
                lastElement.setNext(nextElement);
                lastElement = nextElement;
            }
            size++;
        }

        @Override
        public boolean hasNext(){
            if (size == 0) return false;
            if (currentElement==null) {
                return firstElement.getNext() != null;
            }
            return currentElement.getNext() != null;
        }

        @Override
        public E next(){
            if (size == 0){
                return null;
            }
            if(currentElement == null){
                currentElement =  firstElement;
            }else {
                currentElement = currentElement.getNext();
            }

            return currentElement.getValue();
        }

        @Override
        public boolean hasPrevious() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public E previous() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int nextIndex() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int previousIndex() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void remove() {
            remove(currentElement);
        }

        @Override
        public void set(E e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void remove(Link<E> elementToRemove){
            if (size ==0 ) return;
            Link<E> iteratedElement = firstElement;
            Link<E> prevElement = null;

            //search for the element
            while (iteratedElement != null && !elementToRemove.equals(iteratedElement)){
                prevElement = iteratedElement;
                iteratedElement =  iteratedElement.getNext();
            }

            //element not found
            if (iteratedElement == null) throw new RuntimeException("element not found");

            if (prevElement == null){
                //the element to delete is on the first position
                firstElement = firstElement.getNext();
            }else {
                prevElement.setNext(iteratedElement.getNext());
            }
            if (iteratedElement.equals(currentElement)){
                currentElement = prevElement;
            }
            size--;
        }


    };

    public ListIterator<E> iterator(){
        return new SIterator();
    }

    public static void main(String[] args) {
        SList<String> sList = new SList<String>();
        ListIterator<String> sIterator = sList.iterator();
        sIterator.add("First Element");
        sIterator.add("Second Element");
        sIterator.add("Third Element");
        sIterator.add("Fourth Element");

        while (sIterator.hasNext()){
            String element = sIterator.next();
            System.out.println(element);
        }

        System.out.println("=======Adding the fifth element=========");
        sIterator.add("Fifth Element");

        sIterator = sList.iterator();
        while (sIterator.hasNext()){
            String element = sIterator.next();
            System.out.println(element);
        }

        //System.out.println("==========Removing the last element==========");
        //sIterator.remove();

        System.out.println("========Removing an element in the middle");
        sIterator =  sList.iterator();
        sIterator.next();
        //sIterator.next();
        //System.out.println(sIterator.next());
        sIterator.remove();

        sIterator =  sList.iterator();
        while (sIterator.hasNext()){
            String element = sIterator.next();
            System.out.println(element);
        }

    }

}

class Link<E>{
    private E value;
    private Link<E> next;

    public Link(E value) {
        this.value = value;
    }

    Link<E> getNext() {
        return next;
    }

    void setNext(Link<E> next) {
        this.next = next;
    }

    E getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Link<E> other = (Link<E>)obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
};