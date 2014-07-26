package annotations;

import asm.FieldBytecodeAdapter;
import asm.handlers.dispatch.impl.Cont;
import field.bytecode.protect.annotations.DispatchOverTopology;
import asm.FieldBytecodeAdapter2;
import field.protect.asm.ASMType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jason on 7/23/14.
 */
@SuppressWarnings("UnusedDeclaration")
public
class MyDispatchOverTopology_Desired {
    private final DoPCtx __generated_params_for_case1 = DoPCtx.handleCreate();
    private final DoPCtx __generated_params_for_case2 = DoPCtx.handleCreate();


    String nameID;
    String paramID;

    int tick = 0;

    @DispatchOverTopology(topology = Cont.class)
    public
    int case2_new_ex() {
        FieldBytecodeAdapter2.handleEntry(__generated_params_for_case2, null);
        int val = 0;
        boolean success = false;
        try {
            val = case2_orig();
            success = true;
        } finally {
            FieldBytecodeAdapter2.handleExit(success, val, __generated_params_for_case2);
        }
        return val;

    }

    @DispatchOverTopology(topology = Cont.class)
    public
    void case1_new_ex() {
        FieldBytecodeAdapter2.handleEntry(__generated_params_for_case1, null);
        try {
            case1_orig();
        } finally {
            FieldBytecodeAdapter2.handleExit(null, __generated_params_for_case1);
        }

    }

    @DispatchOverTopology(topology = Cont.class)
    public
    void case1_new_simple() {
        FieldBytecodeAdapter2.handleEntry(__generated_params_for_case1, null);
        case1_orig();
        FieldBytecodeAdapter2.handleExit(null, __generated_params_for_case1);
    }


    @DispatchOverTopology(topology = Cont.class)
    public
    void case1() {
        FieldBytecodeAdapter.handleEntry(nameID, this, "update", paramID, null);
        tick++;
        FieldBytecodeAdapter.handleExit(null, nameID, this, "update", paramID, "void");
    }

    @DispatchOverTopology(topology = Cont.class)
    public
    void case1_orig() {
        tick++;
    }

    @DispatchOverTopology(topology = Cont.class)
    public
    int case2_orig() {
        return tick++;
    }

    static
    class DoPCtx extends HashMap<String, Object> implements Map<String, Object> {
        static
        DoPCtx handleCreate() {
            return new DoPCtx();
        }

        ASMType topology;

    }
}
