package field.apt.gen

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
        def genericIn = acceptorGenericArg(me)
        //def vv = me.mirrorKind == MirrorKind.MirrorNoReturnNoArgsMethod

        def call = me.simpleName + '(' + me.params().keySet().join(',') + ')'

        def impls = ["IAcceptor<${genericIn}>",
                     "IFunction<$genericIn,${me.boxedReturnType}>"]
        if(me.params().isEmpty()) impls<<"field.launch.IUpdateable"

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
            field([name     : 'a',
                   type     : "IAcceptor<$genericIn>",
                   modifiers: [Modifier.FINAL]])
            field([name     : 'f',
                   type     : "IFunction<$genericIn,$me.boxedReturnType>",
                   modifiers: [Modifier.FINAL]])
            constructor(params: [x: me.parent.simpleName]) {
                stmnt 'this.x=x'
                stmnt "this.a=${me.generatedName}_s.acceptor(x)"
                stmnt "this.f=${me.generatedName}_s.function(x)"
            }

            def maybeReturn = me.hasReturnType() ? 'return' : ''

            impl(wrapMethod) {
                stmnt "$maybeReturn this.x.$call"
                if (!me.hasReturnType()) stmnt 'return null'
            }
            def updateMeth=[name:'update']
            if(me.params().isEmpty()) impl(updateMeth){
                //TODO cache field
            'updateable().update()'
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
                """ |return new IProvider<$me.boxedReturnType>(){
                    |    public $me.boxedReturnType get(){
                    |        $bindCall;
                    |        }
                    |}""".stripMargin()
            }
            impl([name      : 'apply',
                  returnType: me.boxedReturnType,
                  params    : [p: acceptorGenericArg(me)]]) {
                "return f.apply(p)"
            }
            impl([name      : 'set',
                  returnType: "IAcceptor<$genericIn>",
                  params    : [p: acceptorGenericArg(me)]]) {
                stmnt 'this.a.set(p)'
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


}
