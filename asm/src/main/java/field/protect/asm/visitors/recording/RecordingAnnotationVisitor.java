package field.protect.asm.visitors.recording;

import field.protect.asm.visitors.AbstractAnnotationVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jason on 7/27/14.
 */
public
class RecordingAnnotationVisitor extends AbstractAnnotationVisitor {
    static abstract
    class AVInvocation {
        abstract
        void accept(AnnotationVisitor av);
    }

    public
    RecordingAnnotationVisitor(String desc, boolean visible) {
        super(null);
        this.desc = desc;
        this.visible = visible;
    }

    final String desc;
    final boolean visible;
    private final List<AVInvocation> calls = new LinkedList<AVInvocation>();

    @Override
    public
    void visit(final String name, final Object value) {
        calls.add(new AVInvocation() {
            @Override
            void accept(AnnotationVisitor av) {
                av.visit(name, value);
            }
        });
    }

    @Override
    public
    void visitEnum(final String name, final String desc, final String value) {
        calls.add(new AVInvocation() {
            @Override
            void accept(AnnotationVisitor av) {
                av.visitEnum(name, desc, value);
            }
        });
    }

    @Override
    public
    AnnotationVisitor visitAnnotation(final String name, final String desc) {
        calls.add(new AVInvocation() {
            @Override
            void accept(AnnotationVisitor av) {
                av.visitAnnotation(name, desc);
            }
        });
        return this;
    }

    @Override
    public
    AnnotationVisitor visitArray(final String name) {
        calls.add(new AVInvocation() {
            @Override
            void accept(AnnotationVisitor av) {
                av.visitArray(name);
            }
        });
        return this;
    }

    @Override
    public
    void visitEnd() {
        calls.add(new AVInvocation() {
            @Override
            void accept(AnnotationVisitor av) {
                av.visitEnd();
            }
        });
    }

    public
    void replay(ClassVisitor cv) {
        AnnotationVisitor ann = new ReplayingAnnotationVisitor(cv.visitAnnotation(desc, visible));
        for (AVInvocation invocation : calls) {
            invocation.accept(ann);
        }
    }

    static
    class ReplayingAnnotationVisitor extends AbstractAnnotationVisitor {
        private final Deque<AnnotationVisitor> visitors = new ArrayDeque<AnnotationVisitor>(4);

        public
        ReplayingAnnotationVisitor(AnnotationVisitor av) {
            super(av);
            visitors.push(av);
        }

        @Override
        public
        AnnotationVisitor visitArray(String name) {
            AnnotationVisitor v = super.visitArray(name);
            visitors.push(v);
            av = v;
            return this;
        }

        @Override
        public
        AnnotationVisitor visitAnnotation(String name, String desc) {
            AnnotationVisitor v = super.visitAnnotation(name, desc);
            visitors.push(v);
            av = v;
            return this;
        }

        @Override
        public
        void visit(String name, Object value) {
            super.visit(name, value);
        }

        @Override
        public
        void visitEnum(String name, String desc, String value) {
            super.visitEnum(name, desc, value);
        }

        @Override
        public
        void visitEnd() {
            super.visitEnd();
            av = visitors.pop();
        }
    }

}
