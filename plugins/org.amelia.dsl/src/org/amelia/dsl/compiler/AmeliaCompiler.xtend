/*
 * Copyright © 2015 Universidad Icesi
 * 
 * This file is part of the Amelia DSL.
 * 
 * The Amelia DSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Amelia DSL is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with The Amelia DSL. If not, see <http://www.gnu.org/licenses/>.
 */
package org.amelia.dsl.compiler

import org.amelia.dsl.amelia.ChangeDirectory
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.compiler.Later
import org.eclipse.xtext.xbase.compiler.XbaseCompiler
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable

/**
 * @author Miguel Jiménez - Initial contribution and API
 */
class AmeliaCompiler extends XbaseCompiler {

	override internalToConvertedExpression(XExpression obj, ITreeAppendable appendable) {
		switch (obj) {
			ChangeDirectory: _toJavaExpression(obj, appendable)
			default: super.internalToConvertedExpression(obj, appendable)
		}
	}

	override doInternalToJavaStatement(XExpression expr, ITreeAppendable appendable, boolean isReferenced) {
		switch (expr) {
			ChangeDirectory: _toJavaStatement(expr, appendable, isReferenced)
			default: super.doInternalToJavaStatement(expr, appendable, isReferenced)
		}
	}
	
	def protected void _toJavaStatement(ChangeDirectory expr, ITreeAppendable b, boolean isReferenced) {
		if (!isReferenced) {
			internalToConvertedExpression(expr, b);
			b.append(";");
		} else if (isVariableDeclarationRequired(expr, b)) {
			val later = new Later() {
				override void exec(ITreeAppendable appendable) {
					internalToConvertedExpression(expr, appendable);
				}
			};
			declareFreshLocalVariable(expr, b, later);
		}
	}
	
	def protected void _toJavaExpression(ChangeDirectory expr, ITreeAppendable b) {
		b.append("new ").append(org.amelia.dsl.lib.descriptors.ChangeDirectory).append("(")
		internalToConvertedExpression(expr.directory, b)
		b.append(")")
	}

}
