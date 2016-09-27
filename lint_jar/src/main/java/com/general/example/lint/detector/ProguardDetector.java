package com.general.example.lint.detector;

import com.android.tools.lint.client.api.JavaParser;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.Speed;

import java.util.Collections;
import java.util.List;

import lombok.ast.AstVisitor;
import lombok.ast.Expression;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.MethodInvocation;
import lombok.ast.Node;
import lombok.ast.StrictListAccessor;

/**
 * Created by general on 9/27/16.
 */

public class ProguardDetector extends Detector implements Detector.JavaScanner {

    public static final Issue ISSUE = Issue.create(
            "AvoidProGuard1",
            "Error!",
            "Error,Error!",
            Category.SECURITY, 6, Severity.ERROR,
            new Implementation(ProguardDetector.class, Scope.JAVA_FILE_SCOPE));

    public ProguardDetector() {}

    @Override
    public Speed getSpeed(Issue issue) {

        if (issue == ISSUE) {
            return Speed.FAST;
        }
        return super.getSpeed();
    }

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Collections.<Class<? extends Node>>singletonList(MethodInvocation.class);
    }

    @Override
    public AstVisitor createJavaVisitor(final JavaContext context) {
        return new ForwardingAstVisitor() {
            @Override
            public boolean visitMethodInvocation(MethodInvocation node) {
                JavaParser.ResolvedNode resolve = context.resolve(node);
                if (resolve instanceof JavaParser.ResolvedMethod) {
                    JavaParser.ResolvedMethod method = (JavaParser.ResolvedMethod) resolve;
                    // 方法所在的类校验
                    JavaParser.ResolvedClass containingClass = method.getContainingClass();

                    if (containingClass.matches("com.example.general.custom.lint.TestUtil")) {
                        StrictListAccessor<Expression, MethodInvocation> arguments = node.astArguments();
                        if (arguments != null && arguments.size() > 0) {
                            int index = arguments.size() - 1;
                            JavaParser.TypeDescriptor descriptor = method.getArgumentType(index);
                            JavaParser.ResolvedClass resolvedClass = descriptor.getTypeClass();
                            if (resolvedClass.isSubclassOf("java.io.Serializable",false) == false) {
                                context.report(ISSUE, node, context.getLocation(node), "must be implement Ser or Parcel");
                                return true;
                            }
                        }
                    }
                }
                return super.visitMethodInvocation(node);
            }
        };
    }
}
