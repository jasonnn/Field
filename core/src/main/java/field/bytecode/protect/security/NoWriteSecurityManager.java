/**
 *
 */
package field.bytecode.protect.security;

import field.launch.SystemProperties;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class NoWriteSecurityManager extends
        SecurityManager {
    private static final Logger log = Logger.getLogger(NoWriteSecurityManager.class.getName());

    @Override
    public void checkWrite(
            FileDescriptor fd) {
        log.info("NoWriteSecurityManager.checkWrite(fd)");
        //	Allow file descriptor writes:
        //throw new SecurityException();
    }

    @Override
    public void checkWrite(
            String fd) {

		/*	Allow writes to temporary files, or to anywhere with (directory) prefix in property "writeableDirPrefixes".
         *
		 * 	This looks a little odd: add "/" to the end of the file we're checking, regardless of whether it's an actual
		 * 	file or just a directory. That lets us match against "/okpath/" and allow "/okpath" but not "/okpathbutnotreally" -
		 * 	but "/okpath/foo.txt/ will also pass." 
		 */

        fd = fd + "/";

        final String prop = "writeableDirPrefixes";

        String tmpdir = System.getProperty("java.io.tmpdir");
        String[] whitelistedDirs = SystemProperties.getDirProperties(prop);

        boolean ok = fd.startsWith(tmpdir);

        for (String d : whitelistedDirs) {
            log.info(String.format("Checking %s against whitelist entry %s", fd, d));
            ok = ok || fd.startsWith(d);
        }

        log.info(String.format("NoWriteSecurityManager.checkWrite(fdslash='%s', tmp='%s', wl='%s') -> %s)",
                fd, tmpdir, SystemProperties.getProperty(prop), (ok ? "TRUE" : "FALSE")));

        if (ok) return;
        throw new SecurityException();
    }

    @Override
    public void checkAccept(
            String host,
            int port) {
    }

    @Override
    public void checkAccess(Thread t) {
    }

    @Override
    public void checkAccess(
            ThreadGroup g) {
    }

    @Override
    public void checkAwtEventQueueAccess() {
    }

    @Override
    public void checkConnect(
            String host,
            int port,
            Object context) {
    }

    @Override
    public void checkConnect(
            String host,
            int port) {
    }

    @Override
    public void checkCreateClassLoader() {
    }

    @Override
    public void checkDelete(
            String file) {
        throw new SecurityException();
    }

    @Override
    public void checkExec(String cmd) {
    }

    @Override
    public void checkExit(int status) {
    }

    @Override
    public void checkLink(String lib) {
    }

    @Override
    public void checkListen(int port) {
    }

    @Override
    public void checkMemberAccess(
            Class<?> clazz,
            int which) {
    }

    @Override
    public void checkMulticast(
            InetAddress maddr,
            byte ttl) {
    }

    @Override
    public void checkMulticast(
            InetAddress maddr) {
    }

    @Override
    public void checkPackageAccess(
            String pkg) {
    }

    @Override
    public void checkPackageDefinition(
            String pkg) {
    }

    @Override
    public void checkPermission(
            Permission perm,
            Object context) {
    }

    @Override
    public void checkPermission(Permission perm) {
    }

    @Override
    public void checkPrintJobAccess() {
    }

    @Override
    public void checkPropertiesAccess() {
    }

    @Override
    public void checkPropertyAccess(String key) {
    }

    @Override
    public void checkRead(
            FileDescriptor fd) {
    }

    @Override
    public void checkRead(
            String file,
            Object context) {
    }

    @Override
    public void checkRead(
            String file) {
    }

    @Override
    public void checkSecurityAccess(
            String target) {
    }

    @Override
    public void checkSetFactory() {
    }

    @Override
    public void checkSystemClipboardAccess() {
    }

    @Override
    public boolean checkTopLevelWindow(
            Object window) {
        return super.checkTopLevelWindow(window);
    }

    @Override
    protected int classDepth(
            String name) {
        return super.classDepth(name);
    }

    @Override
    protected int classLoaderDepth() {
        return super.classLoaderDepth();
    }

    @Override
    protected ClassLoader currentClassLoader() {
        return super.currentClassLoader();
    }

    @Override
    protected Class<?> currentLoadedClass() {
        return super.currentLoadedClass();
    }

    @Override
    protected Class[] getClassContext() {
        return super.getClassContext();
    }

    @Override
    public boolean getInCheck() {
        return super.getInCheck();
    }

    @Override
    public Object getSecurityContext() {
        return super.getSecurityContext();
    }

    @Override
    public ThreadGroup getThreadGroup() {
        return super.getThreadGroup();
    }

    @Override
    protected boolean inClass(
            String name) {
        return super.inClass(name);
    }

    @Override
    protected boolean inClassLoader() {
        return super.inClassLoader();
    }
}