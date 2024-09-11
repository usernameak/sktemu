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

    public AmsClassProcessor(ClassVisitor parent) {
        super(Opcodes.ASM9, parent);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        visitor = new ResourceMethodProcessor(visitor);
        if (exceptionDebugEnabled) {
            visitor = new ExceptionDebugProcessor(visitor);
        }
        return visitor;
    }
}
