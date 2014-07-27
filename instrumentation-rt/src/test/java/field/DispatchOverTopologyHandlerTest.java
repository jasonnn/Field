package field;

public
class DispatchOverTopologyHandlerTest {

//    static ASMAnnotatedMethodCtx ctx;
//    static String nameID;
//    static String paramID;
//    static DispatchOverTopologyHandler handler;
//
//    static Registration reg;
//
//    @Before
//    public
//
//    void setUp() throws Exception {
//        ctx = new ASMAnnotatedMethodCtx();
//        ctx.params.put("topology", Type.getType(Cont.class));
//        ctx.params.put("method", new ASMMethod("update", "()V"));
//        ctx.desc = "()V";
//        ctx.name = "update";
//        ctx.classCtx.name = DispatchOverTopologyHandlerTest.class.getName() + "$DoT_Instr";
//        nameID = Namer.createName(DispatchOverTopology.class, ctx);
//        paramID = Namer.uniqueParamID();
//        handler = new DispatchOverTopologyHandler(nameID, ctx.classCtx.name);
//        reg = Registrations.concat(FieldBytecodeAdapter.registerHandler(nameID, handler),
//                                   FieldBytecodeAdapter.registerParameters(paramID, ctx.params));
//
//    }
//
//    @After
//    public
//    void tearDown() throws Exception {
//        reg.remove();
//    }
//
//    @Test
//    public
//    void testRT() throws Exception {
//        DoT_Instr instr = new DoT_Instr();
//        instr.update();
//        instr.update();
//
//        assertEquals(2, instr.tick);
//
//    }
//
//
//    static
//    class DoT_Instr {
//        int tick = 0;
//
//        @DispatchOverTopology(topology = Cont.class)
//        public
//        void update() {
//            FieldBytecodeAdapter.handleEntry(nameID, this, "update", paramID, null);
//            tick++;
//            FieldBytecodeAdapter.handleExit(null, nameID, this, "update", paramID, "void");
//        }
//    }
//
//    static
//    class DoT_Orig {
//        int tick = 0;
//
//        @DispatchOverTopology(topology = Cont.class)
//        public
//        void update() {
//            tick++;
//        }
//    }


}