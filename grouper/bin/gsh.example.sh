#!/bin/sh

# if not in bin dir, but in grouper_home, then cd to bin
[ -e bin/gsh.sh ] || cd bin

[ -e gsh.sh ] || echo "Cant find gsh.sh.  Are you in the grouper_home dir or grouper_home\bin dir?"
[ -e gsh.sh ] || exit 1

JAVA=java

GSH=edu.internet2.middleware.grouper.app.gsh.GrouperShell

GROUPER_HOME=..

GROUPER_CONF=../conf

MEM_START=64m

MEM_MAX=256m

${JAVA} -Xms${MEM_START} -Xmx${MEM_MAX} -jar ${GROUPER_HOME}/lib/grouper/invoker.jar -cpdir ${GROUPER_CONF} -cpalljars ${GROUPER_HOME}/lib -cpjar ${GROUPER_HOME}/dist/lib/grouper.jar ${GSH_JVMARGS} ${GSH} "$@"

exit $?
