/* Generated By:JJTree: Do not edit this line. ASTWhileStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package edu.internet2.middleware.grouperClientExt.org.apache.commons.jexl2.parser;

public
class ASTWhileStatement extends JexlNode {
  public ASTWhileStatement(int id) {
    super(id);
  }

  public ASTWhileStatement(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1968a8f6c9f2ee8990d893a3b04f315d (do not edit this line) */