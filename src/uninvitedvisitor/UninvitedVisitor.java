package uninvitedvisitor;

import javassist.*;
import javassist.bytecode.analysis.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

public abstract class UninvitedVisitor<T> {
    final private static Comparator<Method> methodComparator = new Comparator<Method>() {
        public int compare(Method m0, Method m1) {
            Class[] paramTypes0 = m0.getParameterTypes();
            if (paramTypes0.length != 1) {
               return 1;
            }

            Class[] paramTypes1 = m1.getParameterTypes();
            if (paramTypes1.length != 1) {
               return -1;
            }

            if (paramTypes0[0].isAssignableFrom(paramTypes1[0])) {
                return 1;
            }

            return -1;
        }
    };

    private final Dispatcher<T> dispatcher;

    private static ConcurrentHashMap<Class, Dispatcher> dispatcherMap = new ConcurrentHashMap<Class, Dispatcher>();

    protected UninvitedVisitor() {
        Class clazz = this.getClass();
        Dispatcher<T> dispatcher = dispatcherMap.get(clazz);
        if (dispatcher == null) {
            Method[] methods = clazz.getMethods();
            Arrays.sort(methods, methodComparator);

            StringBuilder code = new StringBuilder();
            code.append("{\n");

            for (Method m : methods) {
                Class[] paramTypes = m.getParameterTypes();
                if (Modifier.isPublic(m.getModifiers()) && !Modifier.isVolatile(m.getModifiers()) && m.getName().equals("visit") && paramTypes.length == 1 && !paramTypes[0].isPrimitive()) {
                    code.append("if ($2 instanceof " + paramTypes[0].getName() + ") { ((" + clazz.getName() + ") $1).visit((" + paramTypes[0].getName() + ") $2); }\n");
                    code.append("else ");
                }
            }

            code.append("{ }\n");
            code.append("}\n");

            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass c = cp.makeClass(Dispatcher.class.getName() + dispatcherMap.size(), cp.get(Dispatcher.class.getName()));
                CtConstructor cnstr = CtNewConstructor.defaultConstructor(c);
                c.addConstructor(cnstr);

                CtMethod m = CtNewMethod.make(CtClass.voidType, "dispatch", new CtClass[]{cp.get(UninvitedVisitor.class.getName()), Type.OBJECT.getCtClass()}, null, code.toString(), c);
                c.addMethod(m);

                dispatcher = ((Class<Dispatcher<T>>) c.toClass()).newInstance();
                dispatcherMap.put(clazz, dispatcher);
            } catch (NotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (CannotCompileException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        this.dispatcher = dispatcher;
    }

    public abstract void visit(T at);
    public void inviteYourself(T at) {
        dispatcher.dispatch(this, at);
    }
}
