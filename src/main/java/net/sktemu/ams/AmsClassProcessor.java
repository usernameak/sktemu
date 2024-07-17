package net.sktemu.ams;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AmsClassProcessor extends ClassVisitor {
    private static class MethodProcessor extends MethodVisitor {
        public MethodProcessor(MethodVisitor parent) {
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

    public AmsClassProcessor(ClassVisitor parent) {
        super(Opcodes.ASM9, parent);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new MethodProcessor(super.visitMethod(access, name, descriptor, signature, exceptions));
    }
}
