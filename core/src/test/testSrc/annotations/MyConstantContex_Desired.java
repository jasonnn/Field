package annotations;

import field.bytecode.protect.annotations.ConstantContext;
import field.bytecode.protect.instrumentation.FieldBytecodeAdapter;
import field.graphics.core.Base;
import field.graphics.core.BasicContextManager;

/**
 * Created by jason on 7/22/14.
 */
@SuppressWarnings("UnusedDeclaration")
public
class MyConstantContex_Desired {
    String name = "dispatchOverTopology" +
                  "+" + "performPass" +
                  "+" + "()V" +
                  "+" + "null" +
                  "+" + 22;
    String onMethod_name = "performPass";
    String paramName = "parameter:" + 23;
    String returnNumber = "0";

    /*
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            loadArgArray();
     */

    /*
            storeLocal(exceptionLocal);

            push((String) null);
            push(name);
            loadThis();
            push(onMethod.getName());
            push(parameterName);
            push("" + returnNumber++);
            invokeStatic(FIELD_BYTECODE_ADAPTER_TYPE, handleExit_O_OSOSSS);

            loadLocal(exceptionLocal);
            throwException();
     */

    @ConstantContext(immediate = false, topology = Base.class)
    public
    void performPass() {
        try {
            FieldBytecodeAdapter.handleEntry(name, this, onMethod_name, paramName, null);
            int id = BasicContextManager.getId(this);
            if ((id == BasicContextManager.ID_NOT_FOUND) || (!BasicContextManager.isValid(this))) {
                System.out.println("1st~!");

            }
        } catch (Throwable t) {
            FieldBytecodeAdapter.handleExit(null, name, this, onMethod_name, paramName, returnNumber);
            throw new RuntimeException(t);
        }


    }

    @ConstantContext(immediate = false, topology = Base.class)
    public
    int performPass_orig() {
        return BasicContextManager.getId(this);

    }

    int probablyWhatIWant() {
        enter();
        int ret = 0;
        try {
            ret = performPass_orig();
        } finally {
            exit(ret);
        }
        return ret;

    }

/*
// access flags 0x0
  probablyWhatIWant()I
    TRYCATCHBLOCK L0 L1 L2 null
    TRYCATCHBLOCK L2 L3 L2 null
   L4
    LINENUMBER 72 L4
    INVOKESTATIC annotations/MyConstantContex_Desired.enter ()V
   L5
    LINENUMBER 73 L5
    ICONST_0
    ISTORE 1

    //TRY:::::
   L0
    LINENUMBER 75 L0
    ALOAD 0
    INVOKEVIRTUAL annotations/MyConstantContex_Desired.performPass_orig ()I
    ISTORE 1

  //FINALLY::::
   L1
    LINENUMBER 77 L1
    ILOAD 1
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    INVOKESTATIC annotations/MyConstantContex_Desired.exit (Ljava/lang/Object;)Ljava/lang/Object;
    POP
   L6
    LINENUMBER 78 L6
    GOTO L7
   L2
    LINENUMBER 77 L2
   FRAME FULL [annotations/MyConstantContex_Desired I] [java/lang/Throwable]
    ASTORE 2
   L3
    ILOAD 1
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    INVOKESTATIC annotations/MyConstantContex_Desired.exit (Ljava/lang/Object;)Ljava/lang/Object;
    POP
    ALOAD 2
    ATHROW
   L7
    LINENUMBER 79 L7
   FRAME SAME
    ILOAD 1
    IRETURN
   L8
    LOCALVARIABLE this Lannotations/MyConstantContex_Desired; L4 L8 0
    LOCALVARIABLE ret I L0 L8 1
    MAXSTACK = 1
    MAXLOCALS = 3
 */

    //TODO why 2 tryCatch blocks?

    private static
    <T> T exit(T t) {
        return t;
    }

    private static
    void enter() {

    }
}
