package test;

public abstract class Exp {
    public static class IntExp extends Exp {
        int value;
        IntExp(int value) { this.value = value; }
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    public static class AddExp extends Exp {
        Exp left;
        Exp right;
        AddExp(Exp left, Exp right) { this.left = left; this.right = right; }
        public void accept(Visitor v) {
            v.visit(this);
        }
    }

    public abstract void accept(Visitor v);

    public static interface Visitor {
        public void visit(IntExp exp);
        public void visit(AddExp exp);
    }
}
