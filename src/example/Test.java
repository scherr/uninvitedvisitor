package example;

import uninvitedvisitor.UninvitedVisitor;

public class Test {
    public static class Printer extends UninvitedVisitor<Exp> implements Exp.Visitor {
        String res;

        public void visit(Exp.IntExp host) {
            res = Integer.toString(host.value);
        }

        public void visit(Exp.AddExp host) {
            // host.left.accept(this);
            this.inviteYourself(host.left);
            String l = res;

            // host.right.accept(this);
            this.inviteYourself(host.right);
            String r = res;

            res = "(" + l + " + " + r + ")";
        }

        public void visit(Exp at) { }
    }

    public static class Evaluator extends UninvitedVisitor<Exp> implements Exp.Visitor {
        int res;

        public final void visit(Exp.IntExp host) {
            res = host.value;
        }

        public final void visit(Exp.AddExp host) {
            // host.left.accept(this);
            this.inviteYourself(host.left);
            int l = res;

            // host.right.accept(this);
            this.inviteYourself(host.right);
            int r = res;

            res = l + r;
        }

        public void visit(Exp at) { }
    }

    public static void main(String args[]) throws InterruptedException {
        Exp e = new Exp.IntExp(0);
        for (int i = 1; i < 100; i++) {
            e = new Exp.AddExp(e, new Exp.IntExp(i));
        }

        Printer visitor = new Printer();
        // Evaluator visitor = new Evaluator();

        long start;
        long end;

        for (int i = 0; i < 10000; i++) {
            // e.accept(visitor);
            visitor.inviteYourself(e);
        }

        System.gc();
        Thread.sleep(4000);

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            // e.accept(visitor);
            visitor.inviteYourself(e);
        }
        end = System.nanoTime();
        System.out.println(end - start);

        System.out.println(visitor.res);
    }
}
