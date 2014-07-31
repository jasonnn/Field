package field.apt.gen

import field.apt.util.GenUtils
import groovy.transform.InheritConstructors
import javabuilder.delegates.StatementHandler

import javax.lang.model.element.Modifier

/**
 * Created by jason on 7/30/14.
 */
@InheritConstructors
class WrapperTypesGen extends GeneratorBase {


    static Map<String, String> makeParamsFinal(Map<String, String> params) {
        params.collectEntries { name, val -> [name, 'final ' + val] }
    }

    void generate(MethodElement me) {
        def params = makeParamsFinal(me.params())
        def genericIn = genArgs(me)
        def vv = me.mirrorKind == MirrorKind.MirrorNoReturnNoArgsMethod

        def call = me.simpleName + '(' + me.params().keySet().join(',') + ')'

        def impls = ["IAcceptor<${genericIn}>",
                     "IFunction<$genericIn,${me.boxedReturnType}>"]

        def wrapMethod = [name      : me.simpleName,
                          returnType: me.rawReturnType,
                          params    : params]

        def updateable = [name      : 'updateable',
                          returnType: 'IUpdateable',
                          params    : params]

        def bind = [name      : 'bind',
                    returnType: "IProvider<$me.boxedReturnType>",
                    params    : params]

        createInterface([name      : me.generatedName + '_interface',
                         modifiers : [Modifier.PUBLIC, Modifier.STATIC],
                         implements: impls])
                {
                    method new HashMap(wrapMethod)
                    method new HashMap(updateable)
                    method new HashMap(bind)
                }

        createClass([name      : me.generatedName + '_impl',
                     modifiers : [Modifier.STATIC],
                     implements: [me.generatedName + '_interface']]) {
            field([name     : 'x',
                   type     : me.parent.simpleName,
                   modifiers: [Modifier.FINAL]])
            if (!vv) field([name     : 'a',
                            type     : 'IAcceptor',
                            modifiers: [Modifier.FINAL]])
            field([name     : 'f',
                   type     : 'IFunction',
                   modifiers: [Modifier.FINAL]])
            constructor(params: [x: me.parent.simpleName]) {
                stmnt 'this.x=x'
                if (!vv) stmnt "this.a=${me.generatedName}_s.acceptor(x)"
                stmnt "this.f=${me.generatedName}_s.function(x)"
            }

            def maybeReturn = me.hasReturnType() ? 'return' : ''

            impl(wrapMethod) {
                stmnt "$maybeReturn this.x.$call"
                if (!me.hasReturnType()) stmnt 'return null'
            }
            impl(updateable) {
                """ |return new IUpdateable(){
                    |   public void update(){
                    |      $call;
                    |   }
                    |}""".stripMargin()
            }
            impl(bind) {
                def bindCall = me.hasReturnType() ? "return $call" : "$call;\nreturn null"
                """ |return new IProvider(){
                    |    public Object get(){
                    |        $bindCall;
                    |        }
                    |}""".stripMargin()
            }
            impl([name      : 'apply',
                  returnType: me.boxedReturnType,
                  params    : [p: genArgs(me)]]) {
                "return ($me.boxedReturnType) f.apply(p)"
            }
            impl([name      : 'set',
                  returnType: "IAcceptor<$genericIn>",
                  params    : [p: genArgs(me)]]) {
                if (!vv) stmnt 'this.a.set(p)'
                stmnt 'return this'
            }
        }


    }

    def impl(Map args, @DelegatesTo(StatementHandler) Closure c) {
        method([*          : args,
                modifiers  : [Modifier.PUBLIC],
                annotations: [Override]],
               c)
    }


    def genArgs(MethodElement me) {
        def nParams = me.parameters.size();
        if (nParams == 1) {
            def param = me.parameters[0].asType()
            return GenUtils.getRawTypeName(true, types, param)
        } else {
            return 'Object[]'
        }
    }


}
