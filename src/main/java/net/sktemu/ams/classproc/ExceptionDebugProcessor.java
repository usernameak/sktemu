package net.sktemu.ams.classproc;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;

public class ExceptionDebugProcessor extends MethodVisitor {
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
