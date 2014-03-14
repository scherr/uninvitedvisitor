package uninvitedvisitor;

abstract class Dispatcher<T> {
    abstract void dispatch(UninvitedVisitor<T> visitor, T to);
}
