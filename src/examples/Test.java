package examples;

import uninvitedvisitor.UninvitedVisitor;

public class Test {
    public static class ToString extends UninvitedVisitor<Exp> {
        String res;

        public void visit(Exp.IntExp host) {
            res = Integer.toString(host.value);
        }

        public void visit(Exp.AddExp host) {
            this.inviteYourself(host.left);
            String l = res;

            this.inviteYourself(host.right);
            String r = res;

            res = "(" + l + " + " + r + ")";
        }
    }

    public static class ToInt extends UninvitedVisitor<Exp> implements Exp.Visitor {
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
    }

    public static void main(String args[]) throws InterruptedException {
        // CtClass.debugDump = "/Users/Max/Desktop/";

        Exp e = new Exp.IntExp(0);
        for (int i = 1; i < 100; i++) {
            e = new Exp.AddExp(e, new Exp.IntExp(i));
        }

        ToString visitor = new ToString();

        long start;
        long end;

        for (int i = 0; i < 10000; i++) {
            // e.accept(visitor);
            visitor.inviteYourself(e);
        }

        System.gc();
        Thread.sleep(2000);

        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            // e.accept(visitor);
            visitor.inviteYourself(e);
        }
        end = System.nanoTime();
        System.out.println(visitor.res);
        System.out.println(end - start);
    }
}
