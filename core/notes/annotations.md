field annotations
================

* woven(noop? at least on methods)



annotation usage
================
|Name               | Usages|
|-------------------|:-----:|
|Woven              | a lot |
|Notable            | 1     |
|AliasedParameter   | 3     |
|Aliases            | 1     |
|Aliasing           | 1     |
|AliasingParameter  | 0     |
|Cached             | 1     |
|CachedPerUpdate    | 2     |
|CacheParameter     | 0     |
|ConstantContext    | 8     |
|Context_begin      | 0     |
|Context_set        | 0     |
|DiskCached         | 0     |
|DispatchOverTopology| a lot|
|FastDispatch       | 0     |
|FromContext        | ?     |
|InQueue            | 19    |
|InQueueThrough     | 0     |
|Inside             | 1     |
|InsideParameter    | 1     |
|Mirror             | a lot |
|NewThread          | 1     |
|NextUpdate         | a lot |
|NonSwing           | 2     |
|SimplyWrapped      | 0.5   |
|TimingStatistics   | 1     |
|Traced             | 0     |
|Yield              | 8     |

DispatchOverTopology Calls
===========================
*TLDR
    - i still have no idea what the purpose of any of this stuff is

* begin->
* FieldBytecodeAdapter.handleEntry(nameID, this, methodName, paramID, args);
    - finds EntryHandler for nameId, params(Map) 
      in this case the handler is DispatchOverTopologyHandler, which delegates to DispatchSupport
* DispatchSupport.enter
    -first run:
        * reflectively create a new instance of 'context'(from params) 
        * it must be an instanceof field.bytecode.protect.dispatch.DispatchProvider
            * Cont or ContainerTopology. in this case i'm going to say it's a Cont.
        * puts instance into params as 'context_cached'
        * context.getTopologyForInstance(obj,params,args,clsName)->Apply
        * all of this stuff then gets cached
        * Apply.head(args)
    -otherwise
        * some stack thing
        * DispatchProvider.notifyExecuteBegin 
            - in Cont this is a noop
*Cont
    - Apply getTopologyForEntrance(final Object root, Map<String, Object> parameters, Object[] args, String className)
        * reflectively gets the target method, puts into parameters with some gibberish string

