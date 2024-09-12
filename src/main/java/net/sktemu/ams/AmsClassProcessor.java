package net.sktemu.ams;

import org.objectweb.asm.*;

import java.util.HashSet;

public class AmsClassProcessor extends ClassVisitor {
    private static final boolean exceptionDebugEnabled =
            "true".equals(System.getProperty("sktemu.exceptionDebugEnable"));

    private static class ResourceMethodProcessor extends MethodVisitor {
        private final HashSet<Label> catchLabels = new HashSet<>();

        public ResourceMethodProcessor(MethodVisitor parent) {
            super(Opcodes.ASM9, parent);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (opcode == Opcodes.INVOKEVIRTUAL &&
                    owner.equals("java/lang/Class") &&
                    name.equals("getResourceAsStream") &&
                    descriptor.equals("(Ljava/lang/String;)Ljava/io/InputStream;")) {

                super.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "net/sktemu/ams/AmsResourceManager",
                        "getResourceAsStream",
                        "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;",
                        false
                );

                return;
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }

    private static class ExceptionDebugProcessor extends MethodVisitor {
        private final HashSet<Label> catchLabels = new HashSet<>();

        public ExceptionDebugProcessor(MethodVisitor parent) {
            super(Opcodes.ASM9, parent);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            super.visitTryCatchBlock(start, end, handler, type);

            catchLabels.add(handler);
        }

        @Override
        public void visitLabel(Label label) {
            super.visitLabel(label);

            if (catchLabels.contains(label)) {
                super.visitInsn(Opcodes.DUP);
                super.visitMethodInsn(
                        Opcodes.INVOKEVIRTUAL,
                        "java/lang/Throwable",
                        "printStackTrace",
                        "()V",
                        false
                );
            }
        }
    }

    private static class SecureUtilWorkaroundProcessor extends MethodVisitor {
        private final MethodVisitor target;

        public SecureUtilWorkaroundProcessor(MethodVisitor target) {
            super(Opcodes.ASM9);

            this.target = target;
        }

        @Override
        public void visitCode() {
            target.visitCode();
            target.visitInsn(Opcodes.ICONST_1);
            target.visitInsn(Opcodes.IRETURN);
            target.visitMaxs(1, 0);
            target.visitEnd();
        }
    }

    private String className;
    private final boolean enableSecureUtilWorkaround;

    public AmsClassProcessor(ClassVisitor parent, boolean enableSecureUtilWorkaround) {
        super(Opcodes.ASM9, parent);
        this.enableSecureUtilWorkaround = enableSecureUtilWorkaround;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);

        visitor = new ResourceMethodProcessor(visitor);

        if (exceptionDebugEnabled) {
            visitor = new ExceptionDebugProcessor(visitor);
        }

        if (enableSecureUtilWorkaround &&
                (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC &&
                "(Ljavax/microedition/midlet/MIDlet;)Z".equals(descriptor)
        ) {
            visitor = new SecureUtilWorkaroundProcessor(visitor);
        }

        return visitor;
    }
}
