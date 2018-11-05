import java.util.List;
import java.util.Arrays;
import java.util.Stack;
import java.util.ArrayList;

/////////////////////////////////////////////////////////////////////////////////
// 
// In Java, a class or interface with a single method can use lambda expressions
// as in `x -> { return 3*x; }`, where the {return} is only needed for complex bodies.

@FunctionalInterface
interface Function<Src,Tgt>
{
    public Tgt apply(Src s);
}

/////////////////////////////////////////////////////////////////////////////////
// Iterator Interface

interface Iterator<T> {
    boolean hasNext();    // Are there more elements to iterate over?
    T       next();       // Actually obtain the next element of type T.

    // The above suffices as an external iterator.
    // The following default method makes it into an internal iterator.
    
    // Almost like fmap, but loses structure.
    // Consumer<T>  ≅ Function<T,void>.
    default void iterate(java.util.function.Consumer consumer)
    {
	while(this.hasNext())
	    consumer.accept(this.next());
    }
}

// Need this to interop with standard iterator and the one we make here ourselves.
class ListIterator<T> implements Iterator<T>
{
    private java.util.Iterator<T> it = null;
    public ListIterator(List l){ it = l.iterator(); }
    public boolean hasNext(){ return it.hasNext(); }
    public T       next(){ return it.next(); }
}

/////////////////////////////////////////////////////////////////////////////////

// Iterate over all subtrees, including sub-sub-trees, etc, down to leafs are included.
//
class TreeIterator<T> implements Iterator<T>
{
    Stack<Iterator<RoseTree<T>>> unvisited = new Stack();

    // We push onto the stack the iterators for the root of the given tree 
    // and its children --all are unvisited.
    // We turn the root into a singleton tree first then obtain an iterator for it.
    public TreeIterator(RoseTree<T> t)
      { 
	  ArrayList root = new ArrayList();
	  root.add( RoseTree.rose(t.value) );

	  unvisited.push(new ListIterator(t.children));
	  unvisited.push(new ListIterator(root));
      }

    public boolean hasNext()
    {
	// If the stack is empty, there are no next elements.
	if(unvisited.empty()) return false;

	// Otherwise look at the [reference of the] iterator at the top
	// of the stack without removing it from the stack.
	Iterator it = unvisited.peek();

	// If the top iterator is empty, discard it
	// and see if the remaining stack is has an element.
	if(!it.hasNext()){ unvisited.pop(); return hasNext(); }

	// Otherwise there is a next element.
	return true;
    }

    // Precondition: hasNext() is true; namely top iterator is non-empty.
    public T next()
    {
	// hasNext() ensures that the top iterator is non-empty, so get
	// an element from it and leave the rest of it on the stack.	
	Iterator<RoseTree<T>> it = unvisited.peek();
	RoseTree<T> item = it.next();

	// The obtained element itself might have subchildren that need to be visited; if any.
        unvisited.push(new ListIterator(item.children));

	return item.value;	
    }
}

/////////////////////////////////////////////////////////////////////////////////

public class RoseTree<T>{

    T value;
    List<RoseTree<T>> children;

    public Iterator<T> iterator() { return new TreeIterator(this); }

    public RoseTree(T value, List<RoseTree<T>> children) {
		this.value = value;
		this.children = children;
	}

    public<U> RoseTree<U> map(Function<T,U> f)
    {
	return new RoseTree(f.apply(this.value),
		    (List<RoseTree<U>>)children.stream().map(c -> c.map(f)));
    }

    public static <T> RoseTree<T> rose(T value, RoseTree<T>... children) {
		return new RoseTree<T>(value, Arrays.asList(children));
	}

    public static void main(String[] args) {
	    /*
              1 → 2 → ⟨21, 22, 23⟩
	        → 3 → ⟨31, 4:⟨341, 342⟩ ⟩
		→ 5 → ⟨⟩
	     */
	    RoseTree t4 = rose("    4", rose("      341"), rose("      342"));
 	    RoseTree t3 = rose("  3", rose("    31"), t4);
 	    RoseTree t2 = rose("  2", rose("    21"), rose("    22"), rose("    23"));
 	    RoseTree t1 = rose("1", t2, t3, rose("  5"));

	    System.out.println("------------Preorder Traversal------------");
 
	    Iterator it = t1.iterator();
	    it.iterate(x -> System.out.println(x));
	    // while(it.hasNext()){ System.out.println(it.next()); }

	}
}

// compile : javac filename.java
// run : java filename

// Local Variables:
// eval: (setq-local NAME (file-name-sans-extension (buffer-name)))
// compile-command: (concat "NAME=" NAME " ; javac $NAME.java ; java $NAME")
// end:
