package org.grails.databinding.compiler;

import java.util.Map;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class BindingFormatASTTransformation implements ASTTransformation {

	@Override
	public void visit(final ASTNode[] astNodes, final SourceUnit source) {
		if (!(astNodes[0] instanceof AnnotationNode) || !(astNodes[1] instanceof FieldNode)) {
			throw new RuntimeException("Internal error: wrong types: $node.class / $parent.class");
		}

		final AnnotationNode annotationNode = (AnnotationNode) astNodes[0];
		final FieldNode fieldNode = (FieldNode) astNodes[1];
		final Map<String, Expression> members = annotationNode.getMembers();
		if(members == null || (!members.containsKey("code") && !members.containsKey("value"))) {
			final String message = "The @BindingFormat annotation on the field ["
					+ fieldNode.getName() +
					"] in class [" +
					fieldNode.getDeclaringClass().getName() +
					"] must provide a value for either the value() or code() attribute.";
			
			error(source, fieldNode, message);
		}
	}
	
	protected void error(final SourceUnit sourceUnit, final ASTNode astNode, final String message) {
		final SyntaxException syntaxException = new SyntaxException(message, astNode.getLineNumber(), astNode.getColumnNumber());
		final SyntaxErrorMessage syntaxErrorMessage = new SyntaxErrorMessage(syntaxException, sourceUnit);
		sourceUnit.getErrorCollector().addError(syntaxErrorMessage, false);
	}
}
