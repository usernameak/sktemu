package net.sktemu.ams;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AmsClassLoader extends ClassLoader implements Closeable {
    private final JarFile jarFile;

    private final HashMap<String, Class<?>> classCache = new HashMap<>();

    public AmsClassLoader(File jarPath) throws IOException {
        super(AmsClassLoader.class.getClassLoader());

        this.jarFile = new JarFile(jarPath);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classCache.containsKey(name)) {
            return classCache.get(name);
        }

        String path = name.replace('.', '/').concat(".class");
        JarEntry entry = jarFile.getJarEntry(path);
        if (entry == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            InputStream stream = jarFile.getInputStream(entry);

            ClassReader classReader = new ClassReader(stream);

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            AmsClassProcessor classProcessor = new AmsClassProcessor(classWriter);

            classReader.accept(classProcessor, 0);

            byte[] data = classWriter.toByteArray();

            Class<?> clazz = defineClass(name, data, 0, data.length);

            classCache.put(name, clazz);
            return clazz;
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    public InputStream getAmsResourceAsStream(String path) throws IOException {
        JarEntry entry = jarFile.getJarEntry(path);
        if (entry == null) {
            return null;
        }
        return jarFile.getInputStream(entry);
    }

    @Override
    public void close() throws IOException {
        jarFile.close();
    }
}
