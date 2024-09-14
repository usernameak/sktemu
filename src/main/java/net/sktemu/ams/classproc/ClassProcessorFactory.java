package net.sktemu.ams.classproc;

import net.sktemu.ams.AppModel;
import org.objectweb.asm.ClassVisitor;

public interface ClassProcessorFactory {
    ClassVisitor createClassProcessor(ClassVisitor parent, AppModel appModel);
}
