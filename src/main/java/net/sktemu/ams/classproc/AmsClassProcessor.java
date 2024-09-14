package net.sktemu.ams.classproc;

import net.sktemu.ams.AppModel;
import net.sktemu.ams.classproc.ExceptionDebugProcessor;
import net.sktemu.ams.classproc.ResourceMethodProcessor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AmsClassProcessor extends ClassVisitor {
    private static final boolean exceptionDebugEnabled =
            "true".equals(System.getProperty("sktemu.exceptionDebugEnable"));

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

    private final boolean enableSecureUtilWorkaround;

    public AmsClassProcessor(ClassVisitor parent, AppModel appModel) {
        super(Opcodes.ASM9, parent);
        enableSecureUtilWorkaround = appModel.getDeviceProfile().getSecureUtilWorkaround();
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
