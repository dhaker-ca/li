package li.template.compiler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;

/**
 * @author li
 */
public final class JavaFileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {

    private final ClassLoaderImpl classLoader;

    private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

    public JavaFileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
        super(fileManager);
        this.classLoader = classLoader;
    }

    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        FileObject o = fileObjects.get(uri(location, packageName, relativeName));
        if (o != null) {
            return o;
        }
        return super.getFileForInput(location, packageName, relativeName);
    }

    public void putFileForInput(StandardLocation location, String packageName, String relativeName, JavaFileObject file) {
        fileObjects.put(uri(location, packageName, relativeName), file);
    }

    private URI uri(Location location, String packageName, String relativeName) {
        try {
            return new URI(location.getName() + '/' + packageName + '/' + relativeName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject outputFile) throws IOException {
        JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
        classLoader.add(qualifiedName, file);
        return file;
    }

    public ClassLoader getClassLoader(JavaFileManager.Location location) {
        return classLoader;
    }

    public String inferBinaryName(Location loc, JavaFileObject file) {
        if (file instanceof JavaFileObjectImpl)
            return file.getName();
        return super.inferBinaryName(loc, file);
    }

    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        List<URL> urlList = new ArrayList<URL>();
        Enumeration<URL> e = contextClassLoader.getResources("httl");
        while (e.hasMoreElements()) {
            urlList.add(e.nextElement());
        }

        ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

        if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
            for (JavaFileObject file : fileObjects.values()) {
                if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }

            files.addAll(classLoader.files());
        } else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
            for (JavaFileObject file : fileObjects.values()) {
                if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
                    files.add(file);
                }
            }
        }

        for (JavaFileObject file : result) {
            files.add(file);
        }

        return files;
    }
}