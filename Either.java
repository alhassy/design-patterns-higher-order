@FunctionalInterface
interface Function<Src,Tgt>
{
    public Tgt apply(Src s);
}


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


class EitherIterator implements Iterator{
    Either e; boolean consumed = false;
    public EitherIterator(Either e){ this.e = e; }
    public boolean hasNext(){ return (!consumed && e != null); }
    public Object  next(){ consumed = true; return e.Match(x -> x, x -> x); }
}


public class Either<A,B>
{
    private A left;
    private B right;
    
    private static enum Location {LEFT, RIGHT};
    private Location tag;
    
    public static<C,D> Either<C,D> Left(C c)
    {
	Either<C,D> e = new Either<C,D>();
	e.left = c; e.tag = Location.LEFT;
	return e;
    }

    public static<C,D> Either<C,D> Right(D d)
    {
	Either<C,D> e = new Either<C,D>();
	e.right = d; e.tag = Location.RIGHT;
	return e;
    }

    public<R> R Match(Function<A,R> f, Function<B,R> g)
    {
	if (this.tag == Location.LEFT)
	    return f.apply(left);
	else
	    return g.apply(right);
    }

    public<C,D> Either<C,D> Bimap(Function<A,C> f, Function<B,D> g)
    {
	if (this.tag == Location.LEFT)
	    return Either.Left(f.apply(left));
	else
	    return Either.Right(g.apply(right));
    }

    public static void main(String[] args) 
    {
	// Have only of the following uncommented.
	
	// Either<Integer,String> ramanujan = Either.Left(1729);
	Either<Integer,String> ramanujan = Either.Right("seventeen twenty-nine!");

	// String s = ramanujan.Match(Object::toString, Object::toString);
	// System.out.println("Ramanujan's number is: " + s);
	Iterator it = new EitherIterator(ramanujan);
	it.iterate(System.out::println);

	while(it.hasNext())
	    {
		System.out.println(it.next());
	    }
	
    }

}

// compile : javac filename.java
// run : java filename

// Local Variables:
// eval: (setq-local NAME (file-name-sans-extension (buffer-name)))
// compile-command: (concat "NAME=" NAME " ; javac $NAME.java ; java $NAME")
// end:
