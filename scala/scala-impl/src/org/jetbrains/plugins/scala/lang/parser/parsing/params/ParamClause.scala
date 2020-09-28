package org.jetbrains.plugins.scala
package lang
package parser
package parsing
package params

import org.jetbrains.plugins.scala.lang.lexer.{ScalaTokenType, ScalaTokenTypes}
import org.jetbrains.plugins.scala.lang.parser.parsing.builder.ScalaPsiBuilder

/**
* @author Alexander Podkhalyuzin
* Date: 06.03.2008
*/

/*
 * ParamClause ::= [nl] '(' [Params] ')'
 */
object ParamClause extends ParsingRule {

  override def apply()(implicit builder: ScalaPsiBuilder): Boolean = {
    val paramMarker = builder.mark
    if (builder.twoNewlinesBeforeCurrentToken) {
      paramMarker.drop()
      return false
    }
    builder.getTokenType match {
      case ScalaTokenTypes.tLPARENTHESIS =>
        builder.advanceLexer() //Ate (
        builder.disableNewlines()
      case _ =>
        paramMarker.rollbackTo()
        return false
    }
    builder.getTokenType match {
      case ScalaTokenTypes.kIMPLICIT =>
        paramMarker.rollbackTo()
        builder.restoreNewlinesState()
        return false
      case _ =>
    }

    if (builder.isScala3 && builder.tryParseSoftKeyword(ScalaTokenType.UsingKeyword)) {
      if (!TypesAsParams()) {
        Params()
      }
    } else {
      Params()
    }


    builder.getTokenType match {
      case ScalaTokenTypes.tRPARENTHESIS =>
        builder.advanceLexer() //Ate )
      case _ =>
        builder error ScalaBundle.message("rparenthesis.expected")
    }
    builder.restoreNewlinesState()
    paramMarker.done(ScalaElementType.PARAM_CLAUSE)
    true
  }
}