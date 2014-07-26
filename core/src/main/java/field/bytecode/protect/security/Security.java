package field.bytecode.protect.security;

import field.util.Registration;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by jason on 7/15/14.
 */
public
class Security {
    public static
    interface SecurityManagerChangeListener {
        void securityManagerChanged(@Nullable SecurityManager oldManager, @Nullable SecurityManager newManager);
    }

    public static
    Security getInstance() {
        return Singleton.INSTANCE;
    }

    static
    enum Singleton {
        ;
        static final Security INSTANCE = new Security();
    }

    public
    void setSecurityManager(SecurityManager newManager) {
        SecurityManager old = System.getSecurityManager();
        System.setSecurityManager(newManager);
        for (SecurityManagerChangeListener listener : listeners) {
            listener.securityManagerChanged(old, newManager);
        }
    }

    public
    void usePermissiveSecurityManager() {
        setSecurityManager(new PermissiveSecurityManager());
    }

    public
    void useNoWriteSecurityManager() {
        setSecurityManager(new NoWriteSecurityManager());
    }

    public
    void useNoopSecurityManager() {
        setSecurityManager(new NoopSecurityManager());
    }

    public
    void useCollectResourcesSecurityManager() {
        setSecurityManager(new CollectResourcesSecurityManager());
    }

    private final List<SecurityManagerChangeListener> listeners =
            new CopyOnWriteArrayList<SecurityManagerChangeListener>();

    public
    Registration addSecurityManagerListener(final SecurityManagerChangeListener listener) {
        listeners.add(listener);
        return new Registration() {
            @Override
            public
            void remove() {
                removeSecurityManagerListener(listener);
            }
        };
    }

    public
    void removeSecurityManagerListener(SecurityManagerChangeListener listener) {
        listeners.remove(listener);
    }
}
